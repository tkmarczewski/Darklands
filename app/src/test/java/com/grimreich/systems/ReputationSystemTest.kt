package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReputationSystemTest {

    @Before
    fun setUp() {
        GameRepository.state = GameState()
    }

    @Test
    fun `changeCity adds delta`() {
        ReputationSystem.changeCity("serce_krainy", 10)
        assertEquals(10, ReputationSystem.getCityRep("serce_krainy"))
    }

    @Test
    fun `changeCity is clamped to plus minus 100`() {
        ReputationSystem.changeCity("serce_krainy", 200)
        assertEquals(100, ReputationSystem.getCityRep("serce_krainy"))
        
        ReputationSystem.changeCity("serce_krainy", -1000)
        assertEquals(-100, ReputationSystem.getCityRep("serce_krainy"))
    }

    @Test
    fun `changeCity creates a new entry for unknown city`() {
        ReputationSystem.changeCity("ghost_town", 5)
        assertEquals(5, ReputationSystem.getCityRep("ghost_town"))
    }

    @Test
    fun `allCities returns snapshot map`() {
        ReputationSystem.changeCity("serce_krainy", 50)
        val all = ReputationSystem.allCities()
        assertEquals(1, all.size)
        assertEquals(50, all["serce_krainy"])
    }

    @Test
    fun `changeFaction rejects unknown faction`() {
        val result = ReputationSystem.changeFaction("unknown", 10)
        assertTrue(result.contains("Nieznana frakcja"))
    }

    @Test
    fun `changeFaction applies and clamps`() {
        ReputationSystem.changeFaction("commoners", 20)
        assertEquals(20, ReputationSystem.getFactionRep("commoners"))
        
        ReputationSystem.changeFaction("commoners", -200)
        assertEquals(-100, ReputationSystem.getFactionRep("commoners"))
    }

    @Test
    fun `priceModifier maps reputation tiers correctly`() {
        // Neutral: 1.0f
        assertEquals(1.0f, ReputationSystem.priceModifier("serce_krainy"), 0.0001f)
        
        // Hated: 1.3f
        ReputationSystem.changeCity("serce_krainy", -10) // 0 -> -10
        assertEquals(1.3f, ReputationSystem.priceModifier("serce_krainy"), 0.0001f)
        
        // Admired: 0.8f
        ReputationSystem.changeCity("serce_krainy", 80) // -10 -> 70
        assertEquals(0.8f, ReputationSystem.priceModifier("serce_krainy"), 0.0001f)
    }

    @Test
    fun `unknown city defaults to neutral modifier`() {
        assertEquals(1.0f, ReputationSystem.priceModifier("nowhere"), 0.0001f)
    }
}
