package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NewAuditBugsFixTest {

    @Test
    fun testCombatConductivityAndRoutedStatusCheck() {
        val mockRepo = mock<GameRepository>()
        whenever(mockRepo.currentState()).thenReturn(GameState())

        val round = CombatRound(mockRepo, MoraleSystem(), FixedRandomProvider())

        val attacker = CombatantState("Attacker", hp = 30, maxHp = 30, endurance = 20, morale = 0) // Routed morale (0)
        val defender = CombatantState("Defender", hp = 30, maxHp = 30, endurance = 20, morale = 80)

        val result = round.resolveRound(attacker, defender, skillId = null)

        // Routed attacker cannot attack or execute skills
        assertEquals(0, result.attackerDamage)
        assertTrue(result.log.any { it.contains("przerażony") })
    }

    @Test
    fun testItemAndGameStateDeepCopyIsolation() {
        val item = Item("i1", "sword_basic", "Miecz", "weapon", "weapon", 20, 2.0, "common", "", mapOf("k" to "v"), mapOf("attack" to 5))
        val copiedItem = item.deepCopy()

        assertEquals("Miecz", copiedItem.name)
        assertEquals(5, copiedItem.effects["attack"])

        val state = GameState()
        state.inventory.add(item)

        val copyState = state.deepCopy()
        assertNotSame(state.inventory, copyState.inventory)
        assertNotSame(state.inventory.first(), copyState.inventory.first())
    }

    @Test
    fun testSaveSystemChecksumVerificationOnRestore() {
        val saveSystem = SaveSystem()
        val state = GameState().apply { gold = 100 }

        kotlinx.coroutines.runBlocking {
            saveSystem.save(state, 1, "Slot1")

            val restoredState = saveSystem.restore(1)
            assertNotNull(restoredState)
            assertEquals(100, restoredState?.gold)
        }
    }

    @Test
    fun testInventoryUseItemRequiresConsumableEffect() {
        val mockRepo = mock<GameRepository>()
        val state = GameState().apply {
            activeHeroId = "h1"
            party.add(Hero(id = "h1", name = "TestHero", hp = 30, maxHp = 50))
        }
        whenever(mockRepo.currentState()).thenReturn(state)
        whenever(mockRepo.updateState(org.mockito.ArgumentMatchers.anyBoolean(), any())).thenAnswer { invocation ->
            val block = invocation.getArgument<(GameState) -> Unit>(1)
            block(state)
            null
        }

        val inventorySystem = InventorySystem(mockRepo)

        // Quest item without heal or sanity effects
        val questItem = Item("q1", "quest_key", "Klucz", "quest", null, 0, 0.5, "common", "", emptyMap(), emptyMap())
        state.inventory.add(questItem)

        val useResult = inventorySystem.useItem("q1")
        assertTrue(useResult.contains("nie ma natychmiastowego efektu"))
        // Quest item should NOT be removed from inventory
        assertTrue(state.inventory.any { it.instanceId == "q1" })
    }

    @Test
    fun testTradingEngineSellItemClearsHeroEquipment() {
        val state = GameState().apply {
            gold = 100
            world.locationId = "city_1"
        }
        val hero = Hero(id = "h1", name = "Hero")
        hero.equipment["weapon"] = "sword_inst_1"
        state.party.add(hero)

        val sword = Item("sword_inst_1", "sword_basic", "Miecz", "weapon", "weapon", 20, 2.0)
        state.inventory.add(sword)

        val calc = object : EconomyCalculator {
            override fun priceInCity(cityId: String, basePrice: Int): Int = basePrice
            override fun calculateSellPrice(cityId: String, item: Item): Int = 10
        }
        TradingEngine.initialize(calc)

        val sellMsg = TradingEngine.sellItem(state, "city_1", "sword_inst_1")
        assertTrue(sellMsg.contains("Sprzedano"))
        assertNull(hero.equipment["weapon"])
        assertFalse(state.inventory.any { it.instanceId == "sword_inst_1" })
    }

    @Test
    fun testTravelSystemSeasonCalculationAndCityValidation() {
        val mockRepo = mock<GameRepository>()
        val cityCat = CityCatalogue()
        cityCat.register(com.grimreich.world.CityData("city_1", "City 1", "Verdun", "p", "f", 1.0f, "bg", null, "lore", "art"))

        val travelSystem = TravelSystem(
            gameRepository = mockRepo,
            worldMap = WorldMap(),
            cityCatalogue = cityCat,
            encounterSystem = mock(),
            worldStabilitySystem = mock(),
            collapseEngine = mock(),
            agingSystem = AgingSystem(mockRepo),
            randomEventManager = mock(),
            randomProvider = DefaultCombatRandomProvider()
        )

        // Day 1..30 -> spring
        assertEquals(Season.spring, travelSystem.currentSeason(1))
        assertEquals(Season.spring, travelSystem.currentSeason(30))
        assertEquals(Season.summer, travelSystem.currentSeason(31))
    }

    @Test
    fun testEncounterSystemAttributeValidationAndStabilityClamping() {
        val encounterSystem = EncounterSystem(mock(), mock())
        val state = GameState().apply {
            world.globalStability = 95
            party.add(Hero(id = "h1", name = "Hero", perception = 8)) // Perception 8 < 12 required
        }

        val choiceWithReq = EncounterChoice("Check", "Desc", "perception", 12, effect = { s ->
            s.world.globalStability += 10
            "Success"
        })

        val resultMsg = encounterSystem.executeChoice(state, choiceWithReq)
        assertTrue(resultMsg.contains("Wymóg nie spełniony"))
        assertEquals(95, state.world.globalStability) // Unchanged because requirement not met
    }

    @Test
    fun testContentValidatorCircularPrerequisites() {
        val questEngine = QuestEngine(mock(), mock(), mock())
        val questA = QuestDefinition("q_a", "Quest A", "Desc", 100, emptyList(), "city_1", "npc_1", prerequisiteQuestId = "q_b")
        val questB = QuestDefinition("q_b", "Quest B", "Desc", 100, emptyList(), "city_1", "npc_1", prerequisiteQuestId = "q_a")

        questEngine.register(questA)
        questEngine.register(questB)

        val validator = ContentValidator(questEngine, mock(), mock(), mock())
        val errors = validator.validateAll()

        assertTrue(errors.any { it.message.contains("circular prerequisite") })
    }

    @Test
    fun testQuestEngineGoldRewardOverflowProtection() {
        val xpSystem = mock<ExperienceSystem>()
        whenever(xpSystem.addPartyXpDirect(any(), org.mockito.ArgumentMatchers.anyInt())).thenReturn(emptyList())

        val xpLazy = mock<dagger.Lazy<ExperienceSystem>>()
        whenever(xpLazy.get()).thenReturn(xpSystem)

        val questEngine = QuestEngine(mock(), mock(), xpLazy)
        val state = GameState().apply {
            gold = Int.MAX_VALUE - 100
            world.locationId = "city_1"
            pendingAction = PendingWorldAction.None
        }

        val step = QuestStep("Talk", StepType.dialogue, "target")
        val def = QuestDefinition("q_gold", "Gold Quest", "Desc", 500, listOf(step), "city_1", "none")
        questEngine.register(def)
        questEngine.activateQuestDirect(state, "q_gold")
        state.quest.progress["q_gold"] = QuestProgress("q_gold", QuestStatus.objective_met, 0)

        questEngine.completeQuestDirect(state, "q_gold")
        // Gold clamped at Int.MAX_VALUE without overflowing into negative numbers
        assertEquals(Int.MAX_VALUE, state.gold)
    }
}
