package com.darklandsmobile

import com.darklandsmobile.core.TravelPartyState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.CityFaction
import com.darklandsmobile.systems.QuestResolutionSystem
import com.darklandsmobile.systems.QuestStatus
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuestResolutionSystemTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
        CityEventSystem.seedStage1Events()
        QuestSystem.seedIntegratedContent(seed = 12)
    }

    @Test
    fun `quest completion grants gold and local reputation`() {
        val quest = QuestSystem.all().first()
        val result = QuestResolutionSystem.completeQuestWithRewards(
            questId = quest.id,
            partyState = TravelPartyState(),
            faction = CityFaction.COMMONERS,
            reputationDelta = 7
        )

        assertEquals(quest.rewardGold, result.goldAwarded)
        assertEquals(QuestStatus.COMPLETED, result.updatedQuestStatus)
        assertEquals(7, ReputationSystem.score(result.cityId, CityFaction.COMMONERS))
        assertTrue(result.updatedPartyState.lastEncounterId?.startsWith("quest_complete:") == true)
    }
}
