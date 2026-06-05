package com.grimreich.systems

import com.grimreich.TestSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReputationSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
    }

    @Test
    fun `changeCity adds delta`() {
        val msg = ReputationSystem.changeCity("grimhold", 25)
        assertEquals(25, ReputationSystem.getCityRep("grimhold"))
        assertTrue(msg.contains("grimhold"))
    }

    @Test
    fun `changeCity is clamped to plus minus 100`() {
        ReputationSystem.changeCity("grimhold", 500)
        assertEquals(100, ReputationSystem.getCityRep("grimhold"))

        ReputationSystem.changeCity("frankfurt", -1000)
        assertEquals(-100, ReputationSystem.getCityRep("frankfurt"))
    }

    @Test
    fun `changeCity creates a new entry for unknown city`() {
        // ReputationState juz ma kilka miast w domyslnej mapie, ale dodajemy nowe
        ReputationSystem.changeCity("dummy_city", 7)
        assertEquals(7, ReputationSystem.getCityRep("dummy_city"))
    }

    @Test
    fun `allCities returns snapshot map`() {
        ReputationSystem.changeCity("grimhold", 10)
        val snap = ReputationSystem.allCities()
        assertEquals(10, snap["grimhold"])
    }

    @Test
    fun `changeFaction rejects unknown faction`() {
        val msg = ReputationSystem.changeFaction("ghost_faction", 10)
        assertTrue(msg.startsWith("Nieznana frakcja"))
        assertEquals(0, ReputationSystem.getFactionRep("ghost_faction"))
    }

    @Test
    fun `changeFaction applies and clamps`() {
        ReputationSystem.changeFaction("church", 30)
        assertEquals(30, ReputationSystem.getFactionRep("church"))

        ReputationSystem.changeFaction("church", 200)
        assertEquals(100, ReputationSystem.getFactionRep("church"))
    }

    @Test
    fun `priceModifier maps reputation tiers correctly`() {
        ReputationSystem.changeCity("grimhold", 60)
        assertEquals(0.8f, ReputationSystem.priceModifier("grimhold"), 0.0001f)

        ReputationSystem.changeCity("koln", 10) // starts at 0, so end 10
        assertEquals(1.0f, ReputationSystem.priceModifier("koln"), 0.0001f)

        ReputationSystem.changeCity("frankfurt", -10) // 0 -> -10
        assertEquals(1.3f, ReputationSystem.priceModifier("frankfurt"), 0.0001f)

        ReputationSystem.changeCity("dummy", -80) // 0 -> -80
        assertEquals(2.0f, ReputationSystem.priceModifier("dummy"), 0.0001f)
    }

    @Test
    fun `unknown city defaults to neutral modifier`() {
        assertEquals(1.0f, ReputationSystem.priceModifier("never_visited"), 0.0001f)
    }
}
