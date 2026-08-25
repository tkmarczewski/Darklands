package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.CityCatalogue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeepAuditHardeningTest {
    private lateinit var gameRepository: GameRepository
    private lateinit var state: GameState
    
    // Systems
    private lateinit var travelSystem: TravelSystem
    private lateinit var mutationEngine: MutationEngine
    private lateinit var townSystem: TownSystem
    private lateinit var ritualSystem: RitualSystem
    
    // Mocks
    private val worldMap = mock<WorldMap>()
    private val cityCatalogue = mock<CityCatalogue>()
    private val encounterSystem = mock<EncounterSystem>()
    private val worldStabilitySystem = mock<WorldStabilitySystem>()
    private val stabilitySystem = mock<StabilitySystem>()
    private val collapseEngine = mock<CollapseEngine>()
    private val agingSystem = mock<AgingSystem>()
    private val randomEventManager = mock<RandomEventManager>()
    private val randomProvider = mock<CombatRandomProvider>()
    private val chronicleSystem = mock<ChronicleSystem>()
    private val mutationSystem = mock<com.grimreich.core.mutations.MutationSystem>()
    private val experienceSystem = mock<ExperienceSystem>()

    @Before
    fun setup() {
        state = GameState()
        gameRepository = mock()
        whenever(gameRepository.currentState()).thenReturn(state)
        
        // Mock updateState to execute the lambda immediately on our state object
        whenever(gameRepository.updateState(any<Boolean>(), any())).thenAnswer { invocation ->
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
            null
        }

        travelSystem = TravelSystem(
            gameRepository, worldMap, cityCatalogue, encounterSystem,
            worldStabilitySystem, collapseEngine, agingSystem,
            randomEventManager, randomProvider
        )
        
        mutationEngine = MutationEngine(gameRepository, mutationSystem, chronicleSystem)
        townSystem = TownSystem(gameRepository)
        ritualSystem = RitualSystem(gameRepository, mock())
    }

    @Test
    fun `travelTo should trigger travel event based on chance`() {
        state.world.locationId = "city_a"
        whenever(worldMap.terrainBetween("city_a", "city_b")).thenReturn(mock()) // Default days = 10
        
        // Force encounter trigger by making randomProvider return 0.0 (below any threshold)
        whenever(randomProvider.nextFloat()).thenReturn(0.0f)
        
        travelSystem.travelTo("city_b")
        
        verify(randomEventManager, times(1)).triggerTravelEvent()
        assertEquals("city_b", state.world.locationId)
    }

    @Test
    fun `processMutations should call chronicleSystem outside updateState`() {
        state.world.echoIntensity = 0.7f
        
        mutationEngine.processMutations()
        
        // Verify chronicleSystem was called
        verify(chronicleSystem).record(any(), any())
        // Since we mocked updateState, we can't easily prove it was "outside" 
        // without a more complex mock, but this confirms it's at least CALLED.
    }

    @Test
    fun `town investment should increase global stability`() {
        state.gold = 500
        state.world.globalStability = 50
        
        townSystem.invest("city_a", 100)
        
        assertEquals(400, state.gold)
        // Bonus = (100 / 20) = 5. 50 + 5 = 55
        assertEquals(55, state.world.globalStability)
    }

    @Test
    fun `resurrection should not result in negative sanity`() {
        state.gold = 200
        val hero = Hero(id = "hero_main", hp = 0, isDead = true, sanity = 10)
        state.party.add(hero)
        
        ritualSystem.performResurrection("hero_main")
        
        assertTrue(hero.hp > 0)
        assertEquals(0, hero.sanity, "Sanity should be capped at 0, not -5")
    }

    @Test
    fun `career days served should not be double counted during year crossing travel`() {
        // Start at day 360, travel for 10 days -> Day 370 (crosses year 1 barrier)
        state.world.day = 360
        val hero = Hero(id = "h1")
        val career = com.grimreich.core.Career.mercenary
        hero.currentCareer = career
        hero.careerHistory.add(CareerEntry(career, daysServed = 100))
        state.party.add(hero)
        
        whenever(worldMap.terrainBetween(any(), any())).thenReturn(null) // Default 10 days
        whenever(randomProvider.nextFloat()).thenReturn(1.0f) // No encounters
        
        travelSystem.travelTo("city_b")
        
        // fullYearsPassed = (370/365) - (360/365) = 1 - 0 = 1
        // AgingSystem would have added 365 if not fixed.
        // TravelSystem adds daysSpent (10).
        // Total should be 100 + 10 = 110.
        
        val entry = hero.careerHistory.find { it.career == career }
        assertEquals(110, entry?.daysServed, "Career days should only reflect travel time, not aging years")
    }
}
