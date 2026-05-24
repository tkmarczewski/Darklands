package com.darklandsmobile.core

import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldMapAndBootstrapTest {

    @Test
    fun `bootstrap seeds map and city catalogue idempotently`() {
        GameBootstrap.init()
        GameBootstrap.init() // drugie wywolanie nie powinno duplikowac

        val nodes = WorldMap.all()
        assertEquals(6, nodes.size)

        val cities = CityCatalogue.all()
        assertEquals(6, cities.size)
    }

    @Test
    fun `Magdeburg has connections to all other seeded cities`() {
        GameBootstrap.init()
        val magdeburg = WorldMap.get("magdeburg")
        assertNotNull(magdeburg)
        // connect z 5 miast, ale connect dziala dwustronnie
        assertTrue("expected Magdeburg neighbors", magdeburg!!.connections.containsAll(
            listOf("frankfurt", "koln", "nurnberg", "praha", "lubeck")
        ))
    }

    @Test
    fun `Lübeck appears in northern region`() {
        GameBootstrap.init()
        val lubeck = WorldMap.get("lubeck")
        assertNotNull(lubeck)
        assertEquals("north", lubeck!!.region)
        assertEquals("Lübeck", lubeck.name)
    }
}
