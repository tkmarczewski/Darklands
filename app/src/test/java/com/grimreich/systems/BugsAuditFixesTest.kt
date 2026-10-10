package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BugsAuditFixesTest {

    @Test
    fun testSaveIntegrityHashDistinguishesQuestIdsAndReputation() {
        val stateA = GameState()
        stateA.quest.activeQuestIds.add("quest_1")
        stateA.reputation.globalFactions["f_merchants"] = 10
        stateA.reputation.globalFactions["f_church"] = -10

        val stateB = GameState()
        stateB.quest.activeQuestIds.add("quest_2") // Different quest ID, same list size
        stateB.reputation.globalFactions["f_merchants"] = -10 // Swapped reputation, same sum
        stateB.reputation.globalFactions["f_church"] = 10

        val hashA = SaveIntegrity.computeStateHash(stateA)
        val hashB = SaveIntegrity.computeStateHash(stateB)

        assertNotEquals("Hashes must be different when quest IDs or reputation distributions differ", hashA, hashB)
    }

    @Test
    fun testWorldStatusEffectsPersistAndAdvanceDaily() {
        val hero = Hero(id = "h1", name = "TestHero")
        val worldEffect = StatusEffect(StatusEffectType.poison, duration = 3, strength = 1)
        hero.worldStatusEffects.add(worldEffect)

        val state = GameState()
        state.party.add(hero)

        assertEquals(1, hero.worldStatusEffects.size)
        assertEquals(3, hero.worldStatusEffects[0].duration)

        // Advance day
        val stabilitySystem = WorldStabilitySystem(mock())
        stabilitySystem.advanceDayDirect(state, "Test pass day")

        assertEquals("World status effect should remain on hero and duration decremented", 1, hero.worldStatusEffects.size)
        assertEquals(2, hero.worldStatusEffects[0].duration)
    }

    @Test
    fun testNormalizeStateCoercesNegativeGoldToZero() {
        val state = GameState(gold = -500)
        state.normalizeState()

        assertEquals(0, state.gold)
    }

    @Test
    fun testCityEventCooldownPreventsDuplicateTriggersSameDay() {
        val randomProvider = object : CombatRandomProvider {
            override fun nextFloat(): Float = 0.01f // Always triggers
            override fun nextInt(until: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int = from
        }

        val gameRepo = mock<GameRepository>()
        val state = GameState()
        state.world.day = 5
        state.world.lastCityEventDay = 0L

        whenever(gameRepo.currentState()).thenReturn(state)
        whenever(gameRepo.updateState(org.mockito.kotlin.any<Boolean>(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            state
        }

        val manager = RandomEventManager(gameRepo, randomProvider)
        val msg1 = manager.triggerCityEvent()
        assertNotNull("First trigger should generate city event", msg1)
        assertEquals(5L, state.world.lastCityEventDay)

        val msg2 = manager.triggerCityEvent()
        assertNull("Second trigger on same day should return null due to cooldown", msg2)
    }

    @Test
    fun testHeroDeathSetWhenHpReachesZeroFromEvent() {
        val hero = Hero(id = "h1", name = "TestHero", hp = 5, maxHp = 20)
        assertFalse(hero.isDead)

        val state = GameState()
        state.party.add(hero)

        val event = RandomEventManager.GameEvent("Severe trap", hpDelta = -10)
        val manager = RandomEventManager(mock(), mock())
        manager.applyEventEffectsDirect(state, event)

        assertEquals(0, hero.hp)
        assertTrue("Hero should be marked dead when HP reaches 0 from random event", hero.isDead)
    }

    @Test
    fun testRitualSystemDoesNotRemoveIngredientsIfItemCreationFails() {
        val mockRepo = mock<GameRepository>()
        val state = GameState()
        state.activeHeroId = "hero1"
        state.party.add(Hero(id = "hero1", name = "Hero", hp = 20))
        state.inventory.add(Item(instanceId = "ing1", templateId = "herb", name = "Herb", type = "material", value = 5))

        whenever(mockRepo.itemCatalogue).thenReturn(mock()) // Returns null for created items

        whenever(mockRepo.updateState(org.mockito.kotlin.any<Boolean>(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            state
        }

        val recipe = RitualRecipe(
            id = "r1",
            name = "Test Ritual",
            requiredCipher = listOf(SymbolType.CROSS),
            requiredIngredients = listOf("herb"),
            targetItemId = "invalid_item",
            sacrificeHp = 0,
            successMessage = "Success!"
        )

        val ritualSystem = RitualSystem(mockRepo, mock())
        val success = ritualSystem.performRitual(recipe, listOf(SymbolType.CROSS))

        assertFalse(success)
        assertEquals("Ingredient should remain in inventory if item creation failed", 1, state.inventory.size)
    }
}
