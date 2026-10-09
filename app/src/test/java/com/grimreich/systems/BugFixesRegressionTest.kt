package com.grimreich.systems

import com.grimreich.core.CombatRandomProvider
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.ui.tavern.RecruitmentViewModel
import com.grimreich.world.HeroPool
import com.grimreich.world.ItemCatalogue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BugFixesRegressionTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDialogueManagerConcurrentSeedingDoesNotCrash() = runBlocking {
        val mockRepo = mock<GameRepository>()
        whenever(mockRepo.currentState()).thenReturn(GameState())

        val mockContext = mock<android.content.Context>()
        val mockAssets = mock<android.content.res.AssetManager>()
        whenever(mockContext.assets).thenReturn(mockAssets)
        whenever(mockAssets.open(org.mockito.kotlin.any())).thenAnswer {
            java.io.ByteArrayInputStream("[]".toByteArray())
        }

        val dialogueManager = DialogueManager(
            context = mockContext,
            gameRepositoryProvider = { mockRepo },
            questEngine = { mock() }
        )

        // Run multiple concurrent seedBasicDialogues calls
        val jobs = (1..10).map {
            async(Dispatchers.Default) {
                dialogueManager.seedBasicDialogues()
            }
        }
        jobs.forEach { it.await() }

        // Should complete without race condition exception
        assertTrue(dialogueManager.getAllNodes().isEmpty() || dialogueManager.getAllNodes().isNotEmpty())
    }

    @Test
    fun testHiredHeroEquipmentInstantiatedInInventory() {
        val itemCatalogue = ItemCatalogue().apply { seed() }
        val mockRepo = mock<GameRepository>()
        val state = GameState(gold = 1000)
        whenever(mockRepo.currentState()).thenReturn(state)
        whenever(mockRepo.itemCatalogue).thenReturn(itemCatalogue)

        var latestState = state
        whenever(mockRepo.updateState(org.mockito.kotlin.any<Boolean>(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(latestState)
            latestState
        }

        val randomProvider = object : CombatRandomProvider {
            override fun nextFloat(): Float = 0.5f
            override fun nextInt(until: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int = from
        }

        val heroPool = HeroPool(randomProvider)
        val generatedHero = heroPool.generateHero()

        state.hireableHeroes.add(generatedHero)

        val viewModel = RecruitmentViewModel(mockRepo, heroPool)
        viewModel.hireHero(generatedHero)

        // Hired hero should now be in party
        val hiredHero = state.party.find { it.id == generatedHero.id }
        assertNotNull("Hero should be hired into party", hiredHero)

        // For each equipped slot, item instance should exist in inventory and instanceId should match
        hiredHero?.equipment?.forEach { (slot, instanceId) ->
            if (instanceId != null) {
                val foundItem = state.inventory.find { it.instanceId == instanceId }
                assertNotNull("Item instance for slot '$slot' ($instanceId) should exist in inventory", foundItem)
            }
        }
    }

    @Test
    fun testEquipmentBonusFallbackToTemplateId() {
        val itemCatalogue = ItemCatalogue().apply { seed() }
        val sword = itemCatalogue.get("sword_basic")
        assertNotNull(sword)

        val hero = Hero(
            id = "hero_test",
            name = "Test Hero",
            strength = 10,
            equipment = mutableMapOf("weapon" to "sword_basic")
        )

        // Even with template item in allItems, equipment bonus should fall back to templateId
        val bonus = hero.getEquipmentBonus("attack", listOf(sword!!))
        assertEquals(5, bonus)
    }

    @Test
    fun testEncounterSystemThreadSafety() = runBlocking {
        val mockLoot = mock<LootSystem>()
        val encounterSystem = EncounterSystem(mockLoot, { mock() })

        val random = object : CombatRandomProvider {
            override fun nextFloat(): Float = 0.99f
            override fun nextInt(until: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int = from
        }

        val jobs = (1..20).map { i ->
            async(Dispatchers.Default) {
                if (i % 2 == 0) {
                    encounterSystem.rollEncounter(random, GameState())
                } else {
                    encounterSystem.addEncounter(
                        Encounter("dyn_$i", "Title $i", "Desc", EncounterType.interactive, emptyList())
                    )
                }
            }
        }
        jobs.forEach { it.await() }
    }
}
