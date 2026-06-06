package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EconomySystemTest {

    @Before
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.seedSprint1()
    }

    @Test
    fun `priceInCity applies city price modifier`() {
        // Grimhold: 1.2f
        assertEquals(120, EconomySystem.priceInCity("grimhold", 100))
        
        // Others: 1.0f
        assertEquals(100, EconomySystem.priceInCity("serce_krainy", 100))
    }

    @Test
    fun `priceInCity returns base price for unknown city`() {
        assertEquals(50, EconomySystem.priceInCity("unknown", 50))
    }

    @Test
    fun `priceInCity never goes below 1`() {
        assertEquals(1, EconomySystem.priceInCity("grimhold", 0))
    }
}
