package com.grimreich

import com.grimreich.world.CityCatalogue
import com.grimreich.world.ProceduralLocationGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProceduralLocationsTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        CityCatalogue.seedSprint1()
    }

    @Test
    fun `generation is deterministic for same seed`() {
        val first = ProceduralLocationGenerator.generate(seed = 42, count = 5)
        val second = ProceduralLocationGenerator.generate(seed = 42, count = 5)

        assertEquals(first, second)
    }

    @Test
    fun `generation differs for different seeds`() {
        val first = ProceduralLocationGenerator.generate(seed = 42, count = 5)
        val second = ProceduralLocationGenerator.generate(seed = 7, count = 5)

        assertNotEquals(first, second)
    }

    @Test
    fun `generated locations attach to known cities`() {
        val cityIds = CityCatalogue.all().map { it.id }.toSet()
        val locations = ProceduralLocationGenerator.generate(seed = 17, count = 8)

        assertEquals(8, locations.size)
        assertTrue(locations.all { it.nearestCityId in cityIds })
        assertTrue(locations.all { it.rewardGold >= 15 })
    }
}
