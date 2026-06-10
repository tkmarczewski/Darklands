package com.grimreich

import com.grimreich.core.PlayerState
import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.QuestJournalSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.world.CityCatalogue
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
        CityCatalogue.seedCanonical()
        WorldMap.seedStage1()
        CityEventSystem.seedStage1Events()
        QuestSystem.seedIntegratedContent(seed = 12)
    }

    @Test
    fun `journal contains active and completed info`() {
        val quest = QuestSystem.all().first()
        val state = PlayerState(
            currentCityId = "grimhold",
            activeQuestId = quest.id,
            completedQuestIds = listOf("quest_done")
        )

        val journal = QuestJournalSystem.build(state)
        assertTrue(journal.activeQuestId == quest.id)
        assertTrue(journal.entries.any { it.questId == quest.id && it.status == "ACTIVE" })
    }
}
