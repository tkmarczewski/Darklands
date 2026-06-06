package com.grimreich.systems

import com.grimreich.core.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TownAndEpilogueTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
    }

    @Test
    fun `TownSystem investment levels up town and modifies prices`() {
        val cityId = "wybrzeze_polnocne"
        GameRepository.state.gold = 1000
        
        // Invest 500 to level up from 1 to 2
        val result = TownSystem.invest(cityId, 500)
        
        assertTrue(result.contains("wzrósł do 2"))
        assertEquals(2, TownSystem.getTown(cityId).developmentLevel)
        assertEquals(0.9f, TownSystem.getPriceModifier(cityId), 0.01f)
    }

    @Test
    fun `EndingSystem generates correct hero epilogue based on stats`() {
        val heroCorr = Hero(id = "h1", name = "Corrupt", age = 30).apply { corruption = 85 }
        val heroMad = Hero(id = "h2", name = "Mad", age = 30).apply { sanity = 10 }
        val heroHoly = Hero(id = "h3", name = "Holy", age = 30).apply { virtue = 50 }
        
        assertTrue(EndingSystem.getHeroEpilogue(heroCorr).contains("naczyniem dla mroku"))
        assertTrue(EndingSystem.getHeroEpilogue(heroMad).contains("popadł w obłęd"))
        assertTrue(EndingSystem.getHeroEpilogue(heroHoly).contains("święty obrońca"))
    }
}
