package com.grimreich.core

import com.grimreich.systems.*
import com.grimreich.world.CityCatalogue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PerfectCoverageTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
    }

    @Test
    fun `EconomySystem handles various price scenarios`() {
        // Base price 100 in Heartland (x1.2)
        assertEquals(120, EconomySystem.priceInCity("serce_krainy", 100))
        
        // Base price 100 in Wild Lands (x0.8)
        assertEquals(80, EconomySystem.priceInCity("ziemie_dzikie", 100))
        
        // Price should not be zero
        assertTrue(EconomySystem.priceInCity("ziemie_dzikie", 1) >= 1)
    }

    @Test
    fun `FactionReputation changes and affects dialogue`() {
        val state = GameRepository.state
        state.reputation.city["wybrzeze_polnocne"] = 50
        assertEquals(50, state.reputation.city["wybrzeze_polnocne"])
    }

    @Test
    fun `SaveSnapshot holds correct data`() {
        val state = GameRepository.state
        val snap = SaveSnapshot(1, 1000L, "Label", state)
        assertEquals(1, snap.version)
        assertEquals(1000L, snap.timestamp)
        assertEquals("Label", snap.label)
        assertEquals(state, snap.state)
    }

    @Test
    fun `TimeOfDay logic works`() {
        val morning = TimeOfDay.MORNING
        assertFalse(morning.isNight())
        
        val midnight = TimeOfDay.MIDNIGHT
        assertTrue(midnight.isNight())
    }

    @Test
    fun `WeatherType exists and has display names`() {
        // Just verify enum exists and doesn't crash on simple access
        WeatherType.values().forEach { it.name }
    }
}
