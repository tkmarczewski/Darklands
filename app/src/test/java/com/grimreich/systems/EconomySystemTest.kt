package com.grimreich.systems

import com.grimreich.TestSupport
import com.grimreich.core.GameBootstrap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EconomySystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
        GameBootstrap.init()
    }

    @Test
    fun `priceInCity applies city price modifier`() {
        // Frankfurt: 1.1f
        assertEquals(110, EconomySystem.priceInCity("frankfurt", 100))
        // Lubeck: 0.9f
        assertEquals(90, EconomySystem.priceInCity("lubeck", 100))
        // Grimhold: 1.0f
        assertEquals(100, EconomySystem.priceInCity("grimhold", 100))
    }

    @Test
    fun `priceInCity returns base price for unknown city`() {
        assertEquals(123, EconomySystem.priceInCity("nowhere", 123))
    }

    @Test
    fun `priceInCity never goes below 1`() {
        assertEquals(1, EconomySystem.priceInCity("lubeck", 0))
    }
}
