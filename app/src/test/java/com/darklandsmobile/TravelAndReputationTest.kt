package com.darklandsmobile

import com.darklandsmobile.core.TravelPartyState
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.CityFaction
import com.darklandsmobile.systems.ReputationSystem
import com.darklandsmobile.systems.TravelSystem
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class TravelAndReputationTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
        ReputationSystem.clear()
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
    }

    @Test
    fun `travel increases fatigue and time`() {
        val initial = TravelPartyState(fatigue = 0, totalHoursTraveled = 0)
        val (updated, result) = TravelSystem.travel(
            fromCityId = "magdeburg",
            toCityId = "hamburg",
            partyState = initial,
            random = Random(7)
        )

        assertEquals("hamburg", result.destinationCityId)
        assertTrue(result.hoursSpent >= 2)
        assertTrue(updated.totalHoursTraveled >= result.hoursSpent)
        assertTrue(updated.fatigue > initial.fatigue)
    }

    @Test
    fun `rest lowers fatigue`() {
        val rested = TravelSystem.restInCity(TravelPartyState(fatigue = 9), restHours = 8)
        assertTrue(rested.fatigue < 9)
    }

    @Test
    fun `reputation is isolated per city`() {
        ReputationSystem.modify("magdeburg", CityFaction.MERCHANTS, 25)
        ReputationSystem.modify("hamburg", CityFaction.MERCHANTS, -10)

        assertEquals(25, ReputationSystem.score("magdeburg", CityFaction.MERCHANTS))
        assertEquals(-10, ReputationSystem.score("hamburg", CityFaction.MERCHANTS))
        assertEquals(0, ReputationSystem.score("wien", CityFaction.MERCHANTS))
        assertTrue(ReputationSystem.priceModifier("magdeburg") < 1.0f)
        assertTrue(ReputationSystem.priceModifier("hamburg") > 1.0f)
    }
}
