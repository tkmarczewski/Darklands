package com.grimreich.core

import com.grimreich.systems.GameLoopController
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldMapAndBootstrapTest {

    @Test
    fun `bootstrap seeds map and city catalogue idempotently`() {
        CityCatalogue.clear()
        WorldMap.clear()

        GameLoopController.bootstrap()
        val count = CityCatalogue.all().size

        GameLoopController.bootstrap()
        assertEquals(count, CityCatalogue.all().size)
        assertTrue(count >= 7)
    }

    @Test
    fun `Grimhold has connections to other seeded cities`() {
        GameLoopController.bootstrap()
        val neighbors = WorldMap.neighbors("wybrzeze_polnocne")
        assertTrue(neighbors.isNotEmpty())
    }

    @Test
    fun `Wybrzeze Polnocne appears in northern region`() {
        GameLoopController.bootstrap()
        val city = CityCatalogue.get("wybrzeze_polnocne")!!
        assertEquals("Wybrzeże Północne", city.name)
        assertEquals("north", city.region)
    }
}
