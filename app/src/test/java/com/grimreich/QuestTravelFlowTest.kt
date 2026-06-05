package com.grimreich

import com.grimreich.core.TravelPartyState
import com.grimreich.core.WorldMap
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.CityFaction
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestTravelFlow
import com.grimreich.systems.ReputationSystem
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuestTravelFlowTest {

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
    fun `travel flow can move to quest city and resolve quest`() {
        val travelQuest = QuestSystem.all().firstOrNull { it.cityId != "grimhold" }
            ?: error("Expected a quest outside Grimhold")

        val result = QuestTravelFlow.travelAndResolve(
            fromCityId = "grimhold",
            questId = travelQuest.id,
            partyState = TravelPartyState(),
            faction = CityFaction.COMMONERS
        )

        assertTrue(result.cityId == travelQuest.cityId)
        assertTrue(result.updatedPartyState.totalHoursTraveled >= 0)
        assertTrue(ReputationSystem.score(travelQuest.cityId, CityFaction.COMMONERS) > 0)
    }
}
