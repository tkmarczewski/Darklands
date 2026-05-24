package com.darklandsmobile

import com.darklandsmobile.core.TravelPartyState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityEventSystem
import com.darklandsmobile.systems.CityFaction
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.QuestTravelFlow
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.world.CityCatalogue
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
        val travelQuest = QuestSystem.all().firstOrNull { it.cityId != "magdeburg" }
            ?: error("Expected a quest outside Magdeburg")

        val result = QuestTravelFlow.travelAndResolve(
            fromCityId = "magdeburg",
            questId = travelQuest.id,
            partyState = TravelPartyState(),
            faction = CityFaction.COMMONERS
        )

        assertTrue(result.cityId == travelQuest.cityId)
        assertTrue(result.updatedPartyState.totalHoursTraveled >= 0)
        assertTrue(ReputationSystem.score(travelQuest.cityId, CityFaction.COMMONERS) > 0)
    }
}
