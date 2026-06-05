package com.grimreich.systems

import com.grimreich.TestSupport
import com.grimreich.core.GameBootstrap
import com.grimreich.core.GameRepository
import com.grimreich.core.Season
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TravelSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
        GameBootstrap.init()  // seeds WorldMap + CityCatalogue
    }

    @Test
    fun `travelTo unknown node returns guard message`() {
        val msg = TravelSystem.travelTo("not_a_place")
        assertTrue(msg.startsWith("Nieznane miejsce"))
    }

    @Test
    fun `travelTo known city advances world state`() {
        val w = GameRepository.state.world
        val dayBefore = w.day

        val msg = TravelSystem.travelTo("frankfurt")

        assertEquals("Frankfurt", w.location)
        assertEquals("central_west", w.region)
        assertEquals(dayBefore + 1, w.day)
        assertTrue("expected fatigue > 0 after travel", w.fatigue > 0)
        assertTrue(msg.contains("Podroz do Frankfurt"))
    }

    @Test
    fun `rest reduces fatigue and advances day`() {
        val w = GameRepository.state.world
        w.fatigue = 35
        val dayBefore = w.day

        val msg = TravelSystem.rest()

        assertEquals(15, w.fatigue) // -20 cap
        assertEquals(dayBefore + 1, w.day)
        assertEquals("morning", w.timeOfDay)
        assertTrue(msg.contains("Druzyna odpoczela"))
    }

    @Test
    fun `rest does not drop fatigue below zero`() {
        val w = GameRepository.state.world
        w.fatigue = 5

        TravelSystem.rest()

        assertEquals(0, w.fatigue)
    }

    @Test
    fun `advanceSeason cycles through all four seasons`() {
        val w = GameRepository.state.world
        w.season = Season.SPRING
        TravelSystem.advanceSeason(); assertEquals(Season.SUMMER, w.season)
        TravelSystem.advanceSeason(); assertEquals(Season.AUTUMN, w.season)
        TravelSystem.advanceSeason(); assertEquals(Season.WINTER, w.season)
        TravelSystem.advanceSeason(); assertEquals(Season.SPRING, w.season)
    }

    @Test
    fun `currentSeason wraps per 30 days`() {
        assertEquals(Season.SPRING, TravelSystem.currentSeason(0))
        assertEquals(Season.SPRING, TravelSystem.currentSeason(29))
        assertEquals(Season.SUMMER, TravelSystem.currentSeason(30))
        assertEquals(Season.AUTUMN, TravelSystem.currentSeason(60))
        assertEquals(Season.WINTER, TravelSystem.currentSeason(90))
        assertEquals(Season.SPRING, TravelSystem.currentSeason(120))
    }

    @Test
    fun `getSeasonDisplay returns localized name based on current day`() {
        val w = GameRepository.state.world
        w.day = 30
        assertEquals("Lato", TravelSystem.getSeasonDisplay())
        w.day = 90
        assertEquals("Zima", TravelSystem.getSeasonDisplay())
    }

    @Test
    fun `travelTo by region id picks a node from that region`() {
        val msg = TravelSystem.travelTo("north")
        // Lubeck jest jedynym miastem w regionie "north" w seedzie
        assertEquals("Lübeck", GameRepository.state.world.location)
        assertTrue(msg.contains("Lübeck"))
    }
}
