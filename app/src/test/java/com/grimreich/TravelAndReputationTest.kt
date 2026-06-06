package com.grimreich

import com.grimreich.core.TravelPartyState
import com.grimreich.systems.CityFaction
import com.grimreich.systems.ReputationSystem
import com.grimreich.systems.TravelSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TravelAndReputationTest {

    @Before
    fun reset() {
        ReputationSystem.clear()
    }

    @Test
    fun `travel increases fatigue and time`() {
        val initial = TravelPartyState(totalHoursTraveled = 0, fatigue = 0)
        
        val (partyResult, travelResult) = TravelSystem.travel(
            fromCityId = "wybrzeze_polnocne",
            toCityId = "serce_krainy",
            partyState = initial
        )
        
        assertEquals("serce_krainy", travelResult.destinationCityId)
        assertTrue(partyResult.totalHoursTraveled > 0)
        assertTrue(partyResult.fatigue > 0)
    }

    @Test
    fun `rest lowers fatigue`() {
        val tired = TravelPartyState(fatigue = 20)
        val rested = TravelSystem.restInCity(tired, 10)
        assertEquals(10, rested.fatigue)
    }

    @Test
    fun `reputation is isolated per city`() {
        ReputationSystem.modify("wybrzeze_polnocne", CityFaction.MERCHANTS, 20)
        ReputationSystem.modify("serce_krainy", CityFaction.MERCHANTS, -10)
        
        assertEquals(20, ReputationSystem.score("wybrzeze_polnocne", CityFaction.MERCHANTS))
        assertEquals(-10, ReputationSystem.score("serce_krainy", CityFaction.MERCHANTS))
        assertEquals(0, ReputationSystem.score("rowniny_koronne", CityFaction.MERCHANTS))
        
        assertTrue(ReputationSystem.priceModifier("serce_krainy") > 1.0f)
    }
}
