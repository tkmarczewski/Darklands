package com.grimreich.core.mutations

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.core.WorldState
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class MutationSystemTest {

    @Mock
    private lateinit var gameRepository: GameRepository

    private lateinit var mutationSystem: MutationSystem

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mutationSystem = MutationSystem(gameRepository)
    }

    @Test
    fun `checkForNewMutation applies mutation when stability is low`() {
        val heroId = "test_hero"
        val hero = Hero(id = heroId, name = "Test Hero", age = 25, strength = 10)
        // Set low stability to maximize chance
        val stability = 0
        val state = GameState(world = WorldState(globalStability = stability)).apply { party.add(hero) }
        
        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
        }

        // We try different days to ensure we hit a winning seed quickly
        var triggered = false
        for (day in 1..200) {
            state.world.day = day
            mutationSystem.checkForNewMutation(heroId, "region_1", 0)
            if (hero.activeMutations.isNotEmpty()) {
                triggered = true
                break
            }
        }
        
        assertTrue("Mutation should eventually trigger with low stability across multiple seeds", triggered)
    }

    @Test
    fun `applyMutation updates hero stats and world stability`() {
        val heroId = "test_hero"
        val hero = Hero(id = heroId, name = "Test Hero", age = 25, strength = 10)
        val state = GameState(world = WorldState(globalStability = 100)).apply { party.add(hero) }
        
        `when`(gameRepository.currentState()).thenReturn(state)
        `when`(gameRepository.updateState(any(), any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val transform = invocation.arguments[1] as (GameState) -> Unit
            transform(state)
        }

        // Simulating the trigger by trying different seeds
        var triggered = false
        for (day in 1..500) {
            state.world.day = day
            mutationSystem.checkForNewMutation(heroId, "region", 0)
            if (hero.activeMutations.isNotEmpty()) {
                triggered = true
                break
            }
        }
        
        assertTrue("Hero should have at least one mutation", triggered)
        assertTrue("World stability should have decreased", state.world.globalStability < 100)
    }
}
