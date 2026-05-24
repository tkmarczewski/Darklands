package com.darklandsmobile

import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.systems.SaveLoadSystem
import com.darklandsmobile.ui.GameplayUiController
import com.darklandsmobile.world.CityCatalogue
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
        SaveLoadSystem.clear()
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

        val resolution = ui.resolve()
        assertTrue(resolution.contains("QUEST RESOLVED"))

        val journal = ui.journal()
        assertTrue(journal.contains("JOURNAL"))

        val save = ui.save()
        assertTrue(save.contains("Saved game"))

        val load = ui.load()
        assertTrue(load.contains("Loaded game"))
    }
}
