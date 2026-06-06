package com.grimreich

import com.grimreich.world.CityCatalogue
import com.grimreich.core.WorldMap
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CataloguesAndWorldTest {

    @Before
    fun seed() {
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
    }

    @Test
    fun `CityCatalogue contains canonical regions`() {
        val all = CityCatalogue.all()
        assertTrue(all.any { it.id == "wybrzeze_polnocne" })
        assertTrue(all.any { it.id == "serce_krainy" })
        assertTrue(all.any { it.id == "rowniny_koronne" })
    }

    @Test
    fun `WorldMap connections use valid city IDs`() {
        val connections = WorldMap.allConnections()
        for (conn in connections) {
            assertNotNull("From city ${conn.fromCityId} should exist", CityCatalogue.get(conn.fromCityId))
            assertNotNull("To city ${conn.toCityId} should exist", CityCatalogue.get(conn.toCityId))
        }
    }

    @Test
    fun `CityData has lore attributes`() {
        val city = CityCatalogue.get("serce_krainy")!!
        assertTrue(city.phenomenon == "Odbicie")
        assertTrue(city.rulingFaction == "Trybunał")
    }
}
