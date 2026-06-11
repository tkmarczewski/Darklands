package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Season
import com.grimreich.world.CityCatalogue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TravelSystemTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
    }

    @Test
    fun `travelTo unknown node returns guard message`() {
        val msg = TravelSystem.travelTo("nowhere")
        assertTrue(msg.contains("zakończona")) 
    }

    @Test
    fun `travelTo known city advances world state`() {
        val w = GameRepository.state.world
        w.location = "Initial"
        w.day = 1
        
        val msg = TravelSystem.travelTo("serce_krainy")
        
        assertEquals("Serce Krainy", w.location)
        // Travel advances days based on distance
        assertTrue(w.day >= 2)
        assertTrue(msg.contains("Podróż do serce_krainy"))
    }

    @Test
    fun `rest reduces fatigue and advances day`() {
        val w = GameRepository.state.world
        w.fatigue = 10
        w.day = 1
        
        val msg = TravelSystem.rest()
        
        assertEquals(0, w.fatigue)
        assertEquals(2, w.day)
        assertTrue(msg.contains("Wypoczynek"))
    }

    @Test
    fun `rest does not drop fatigue below zero`() {
        val w = GameRepository.state.world
        w.fatigue = 2
        
        TravelSystem.rest()
        
        assertEquals(0, w.fatigue)
    }

    @Test
    fun `advanceSeason cycles through all four seasons`() {
        val w = GameRepository.state.world
        w.season = Season.SPRING
        w.day = 31 // Day 31 is Summer
        
        TravelSystem.advanceSeason()
        assertEquals(Season.SUMMER, w.season)
    }

    @Test
    fun `currentSeason wraps per 30 days`() {
        assertEquals(Season.SUMMER, TravelSystem.currentSeason(31))
    }

    @Test
    fun `getSeasonDisplay returns localized name based on current day`() {
        GameRepository.state.world.day = 1
        assertEquals("Wiosna", TravelSystem.getSeasonDisplay())
        
        GameRepository.state.world.day = 61
        assertEquals("Jesień", TravelSystem.getSeasonDisplay())
    }

    @Test
    fun `travelTo by region id picks a node from that region`() {
        TravelSystem.travelTo("ziemie_dzikie")
        assertEquals("Ziemie Dzikie", GameRepository.state.world.location)
    }
}
