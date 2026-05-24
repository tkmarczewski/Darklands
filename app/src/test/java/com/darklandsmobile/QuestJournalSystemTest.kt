package com.darklandsmobile

import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.QuestJournalSystem
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuestJournalSystemTest {

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
    fun `journal contains active and completed info`() {
        val quest = QuestSystem.all().first()
        val state = PlayerState(
            currentCityId = "magdeburg",
            activeQuestId = quest.id,
            completedQuestIds = listOf("quest_done")
        )

        val journal = QuestJournalSystem.build(state)
        assertTrue(journal.activeQuestId == quest.id)
        assertTrue(journal.entries.any { it.questId == quest.id && it.status == "ACTIVE" })
    }
}
