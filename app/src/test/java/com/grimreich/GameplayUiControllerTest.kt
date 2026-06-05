package com.grimreich

import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.ReputationSystem
import com.grimreich.ui.GameplayUiController
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameplayUiControllerTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityEventSystem.clear()
        QuestSystem.clear()
    }

    @Test
    fun `ui controller renders playable loop text`() {
        val ui = GameplayUiController()
        val city = ui.city()
        assertTrue(city.contains("CITY"))
        assertTrue(city.contains("Available quests"))

        val firstQuestLine = city.lines().first { it.trim().startsWith("-") }
        val questId = firstQuestLine.substringAfter("- ").substringBefore(":")

        val accepted = ui.acceptQuest(questId)
        assertTrue(accepted.contains(questId))

        val travel = ui.travel()
        assertTrue(travel.contains("TRAVEL"))

        val journal = ui.journal()
        assertTrue(journal.contains("JOURNAL"))
    }
}
