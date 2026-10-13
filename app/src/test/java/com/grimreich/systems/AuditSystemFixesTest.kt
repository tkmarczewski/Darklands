package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuditSystemFixesTest {

    @Test
    fun testSaveSystemRestoreRejectsCorruptedSave() = runBlocking {
        val saveSystem = SaveSystem()
        val state = GameState(gold = 100)
        saveSystem.save(state, slotId = 1)

        val slots = saveSystem.getSlots()
        val originalSnapshot = slots[1]
        assertNotNull(originalSnapshot)

        val corruptedState = state.copy(gold = 999999)
        val corruptedSnapshot = originalSnapshot!!.copy(state = corruptedState)
        
        val slotsField = SaveSystem::class.java.getDeclaredField("saveSlots")
        slotsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val map = slotsField.get(saveSystem) as MutableMap<Int, SaveSnapshot>
        map[1] = corruptedSnapshot

        val restored = saveSystem.restore(1)
        assertNull("Restoring a snapshot with mismatched checksum must return null", restored)
    }

    @Test
    fun testCombatRoundSkipsActionIfDefenderDefeated() {
        val moraleSystem = MoraleSystem()
        val gameRepo = mock<GameRepository>()
        whenever(gameRepo.currentState()).thenReturn(GameState())

        val combatRound = CombatRound(gameRepo, moraleSystem, object : CombatRandomProvider {
            override fun nextFloat(): Float = 0.5f
            override fun nextInt(until: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int = from
        })

        val attacker = CombatantState(name = "Attacker", hp = 10, maxHp = 10, endurance = 10)
        val defender = CombatantState(name = "Defender", hp = 0, maxHp = 10, endurance = 10) // Already defeated

        val result = combatRound.resolveRound(attacker, defender)
        assertEquals(0, result.attackerDamage)
        assertTrue(result.log.any { it.contains("już poległ") })
    }

    @Test
    fun testUseItemOnFullHpHeroPreservesItem() {
        val mockRepo = mock<GameRepository>()
        val state = GameState()
        val hero = Hero(id = "h1", name = "Hero", hp = 20, maxHp = 20, sanity = 100)
        state.activeHeroId = "h1"
        state.party.add(hero)

        val potion = Item(instanceId = "pot1", templateId = "potion", name = "Healing Potion", type = "consumable", effects = mapOf("heal" to 10))
        state.inventory.add(potion)

        whenever(mockRepo.updateState(org.mockito.kotlin.any<Boolean>(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            state
        }

        val invSystem = InventorySystem(mockRepo)
        val msg = invSystem.useItem("pot1")

        assertTrue(msg.contains("ma już pełne zdrowie"))
        assertEquals("Item should not be consumed when hero is at full health", 1, state.inventory.size)
    }

    @Test
    fun testSellItemClearsHeroEquipmentSlot() {
        val mockRepo = mock<GameRepository>()
        val state = GameState()
        val hero = Hero(id = "h1", name = "Hero")
        hero.equipment["weapon"] = "sword_instance_1"
        state.party.add(hero)

        val sword = Item(instanceId = "sword_instance_1", templateId = "sword_01", name = "Sword", type = "weapon", value = 20)
        state.inventory.add(sword)

        whenever(mockRepo.updateState(org.mockito.kotlin.any<Boolean>(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            state
        }

        val economySystem = EconomySystem(mock(), CityCatalogue())
        val tradeSystem = TradeSystem(mockRepo, economySystem)

        tradeSystem.sellItem(sword, "city_1")

        assertNull("Sold item must be unequipped from hero equipment map", hero.equipment["weapon"])
        assertEquals(0, state.inventory.size)
    }

    @Test
    fun testTravelToInvalidDestinationReturnsEarly() {
        val mockRepo = mock<GameRepository>()
        val state = GameState()
        state.world.day = 10
        state.world.locationId = "city_start"

        whenever(mockRepo.updateState(org.mockito.kotlin.any<Boolean>(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val transform = invocation.getArgument<(GameState) -> Unit>(1)
            transform(state)
            state
        }

        val travelSystem = TravelSystem(
            gameRepository = mockRepo,
            worldMap = WorldMap(),
            cityCatalogue = CityCatalogue(),
            encounterSystem = mock(),
            worldStabilitySystem = mock(),
            collapseEngine = mock(),
            agingSystem = mock(),
            randomEventManager = mock(),
            randomProvider = mock()
        )

        travelSystem.travelTo("non_existent_invalid_city")

        assertEquals("Day should not advance on invalid destination", 10, state.world.day)
        assertEquals("Location should not change on invalid destination", "city_start", state.world.locationId)
    }
}
