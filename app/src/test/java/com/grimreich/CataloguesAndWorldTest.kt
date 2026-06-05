package com.grimreich

import com.grimreich.core.TerrainType
import com.grimreich.core.WorldMap
import com.grimreich.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.ArrayDeque

class CataloguesAndWorldTest {

    @Before
    fun reset() {
        CityCatalogue.clear()
        WorldMap.clear()
    }

    @Test
    fun `stage 1 city catalogue seeds eleven cities`() {
        CityCatalogue.seedSprint1()

        val cities = CityCatalogue.all()
        assertEquals(11, cities.size)
        assertTrue(cities.all { it.population > 0 })
        assertTrue(cities.all { it.events.size >= 2 })
        assertNotNull(CityCatalogue.get("wien"))
        assertNotNull(CityCatalogue.get("strasbourg"))
    }

    @Test
    fun `world map is fully connected from grimhold`() {
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()

        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add("grimhold")
        visited.add("grimhold")

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            WorldMap.neighbors(current)
                .map { if (it.fromCityId == current) it.toCityId else it.fromCityId }
                .filterNot { it in visited }
                .forEach {
                    visited += it
                    queue.add(it)
                }
        }

        assertEquals(CityCatalogue.all().map { it.id }.toSet(), visited)
    }

    @Test
    fun `terrain links are assigned for stage 1 routes`() {
        WorldMap.seedStage1()

        assertEquals(TerrainType.ROAD, WorldMap.terrainBetween("grimhold", "hamburg"))
        assertEquals(TerrainType.MOUNTAIN, WorldMap.terrainBetween("praha", "wien"))
        assertEquals(TerrainType.RIVER, WorldMap.terrainBetween("koln", "strasbourg"))
        assertFalse(WorldMap.isConnected("hamburg", "wien"))
    }
}
