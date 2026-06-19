package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.CityCatalogue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class QuestAuditSyncTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
        CityCatalogue.seedCanonical()
        QuestSystem.clear()
        QuestSystem.seedIntegratedContent()
    }

    @Test
    fun `QuestSystem actions are reflected in GameState`() {
        val questId = "q_start_01"
        val state = GameRepository.state

        // 1. Activate
        QuestSystem.activate(questId)
        assertTrue("Quest should be in activeQuests", state.quest.activeQuests.contains(questId))

        // 2. Complete
        val initialGold = state.gold
        val quest = QuestSystem.getQuest(questId)!!
        QuestSystem.complete(questId)
        
        assertFalse("Quest should NOT be in activeQuests", state.quest.activeQuests.contains(questId))
        assertTrue("Quest should be in completedQuests", state.quest.completedQuests.contains(questId))
        assertEquals("Gold should be awarded", initialGold + quest.rewardGold, state.gold)
    }

    @Test
    fun `QuestSystem syncs from GameState on seed`() {
        val questId = "q_start_01"
        
        // 1. Manually inject into state
        GameRepository.state.quest.activeQuests.add(questId)
        
        // 2. Trigger seed (simulated reload)
        QuestSystem.clear()
        QuestSystem.seedIntegratedContent()
        
        // 3. Verify in-memory status
        val quest = QuestSystem.getQuest(questId)
        assertNotNull(quest)
        assertEquals(QuestStatus.AKTYWNE, quest?.status)
    }

    @Test
    fun `ReputationSystem actions are reflected in GameState`() {
        val cityId = "wybrzeze_polnocne"
        val faction = CityFaction.KNIGHTS
        val state = GameRepository.state

        ReputationSystem.modify(cityId, faction, 25)
        
        val storedValue = state.reputation.cityFactions[cityId]?.get(faction.name)
        assertEquals(25, storedValue)
        assertEquals(25, ReputationSystem.score(cityId, faction))
    }

    @Test
    fun `ReputationSystem syncs from GameState automatically`() {
        val cityId = "wybrzeze_polnocne"
        val faction = CityFaction.CHURCH
        val state = GameRepository.state

        // 1. Manually inject into state
        state.reputation.cityFactions[cityId] = mutableMapOf(faction.name to 40)
        
        // 2. Verify system reads it (no explicit seed needed as it reads state directly)
        assertEquals(40, ReputationSystem.score(cityId, faction))
    }
}
