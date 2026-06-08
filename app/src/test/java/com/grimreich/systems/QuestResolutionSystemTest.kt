package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.TravelPartyState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException

class QuestResolutionSystemTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        QuestSystem.clear()
        ReputationSystem.clear()
        QuestSystem.register(QuestEntry(
            id = "test_quest",
            title = "Test",
            description = "Desc",
            cityId = "grimhold",
            originType = QuestOriginType.ZDARZENIE_MIEJSKIE,
            originRefId = "evt_01",
            rewardGold = 100
        ))
    }

    @Test
    fun `completeQuestWithRewards updates state and returns result`() {
        val result = QuestResolutionSystem.completeQuestWithRewards(
            questId = "test_quest",
            partyState = TravelPartyState(),
            faction = CityFaction.COMMONERS,
            reputationDelta = 10
        )

        assertEquals("test_quest", result.questId)
        assertEquals(100, result.goldAwarded)
        assertEquals("grimhold", result.cityId)
        assertEquals(10, result.reputationDelta)
        
        // Verify QuestSystem state
        val quest = QuestSystem.all().find { it.id == "test_quest" }
        assertNotNull(quest)
        assertEquals(QuestStatus.UKONCZONE, quest?.status)
    }

    @Test
    fun `completeQuestWithRewards updates ReputationSystem`() {
        QuestResolutionSystem.completeQuestWithRewards(
            questId = "test_quest",
            partyState = TravelPartyState(),
            faction = CityFaction.KNIGHTS,
            reputationDelta = 15
        )

        val rep = ReputationSystem.score("grimhold", CityFaction.KNIGHTS)
        assertEquals(15, rep)
    }

    @Test
    fun `completeQuestWithRewards might award loot`() {
        val initialInventorySize = GameRepository.state.inventory.size
        
        var lootFound = false
        for (i in 1..20) {
            val qId = "loot_quest_$i"
            QuestSystem.register(QuestEntry(qId, "Loot", "L", "city", QuestOriginType.ZDARZENIE_MIEJSKIE, "r", 0))
            val result = QuestResolutionSystem.completeQuestWithRewards(qId, TravelPartyState())
            if (result.itemsAwarded.isNotEmpty()) {
                lootFound = true
                break
            }
        }
        
        assertTrue(lootFound || initialInventorySize < GameRepository.state.inventory.size, "Loot should be awarded eventually")
    }

    @Test
    fun `completeQuestWithRewards throws on non-existent quest`() {
        assertThrows(IllegalStateException::class.java) {
            QuestResolutionSystem.completeQuestWithRewards("invalid_id", TravelPartyState())
        }
    }
}
