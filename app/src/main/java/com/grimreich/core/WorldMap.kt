package com.grimreich.core

import com.grimreich.world.CityCatalogue
import com.grimreich.world.CityData

enum class TerrainType(
    val encounterChance: Float,
    val travelHoursRange: IntRange
) {
    ROAD(0.2f, 4..8),
    FOREST(0.4f, 6..12),
    MOUNTAIN(0.6f, 10..20),
    RIVER(0.3f, 3..6),
    SWAMP(0.7f, 12..24)
}

data class TravelConnection(
    val fromCityId: String,
    val toCityId: String,
    val terrain: TerrainType
)

data class CityNode(
    val city: CityData,
    val connections: List<String>,
    val region: String,
    val name: String,
    val x: Int = 0,
    val y: Int = 0
)

object WorldMap {
    private val connections = mutableListOf<TravelConnection>()

    fun seedStage1() {
        if (connections.isNotEmpty()) return
        CityCatalogue.seedSprint1()

        // Syncing with CityCatalogue IDs: 
        // grimhold, koln, nurnberg, frankfurt, praha, lubeck, hamburg, wien, breslau, augsburg, strasbourg
        link("grimhold", "breslau",   TerrainType.ROAD)
        link("grimhold", "nurnberg",  TerrainType.ROAD)
        link("nurnberg", "koln",      TerrainType.ROAD)
        link("koln",     "strasbourg", TerrainType.RIVER)
        link("praha",    "wien",       TerrainType.MOUNTAIN)
        link("praha",    "breslau",    TerrainType.FOREST)
        link("lubeck",   "hamburg",    TerrainType.ROAD)
        link("hamburg",  "koln",       TerrainType.ROAD)
        link("frankfurt", "augsburg",  TerrainType.ROAD)
        link("wien",     "augsburg",   TerrainType.ROAD)
    }

    fun clear() { connections.clear() }

    fun allConnections() = connections.toList()

    fun neighbors(cityId: String): List<TravelConnection> =
        connections.filter { it.fromCityId == cityId || it.toCityId == cityId }

    fun terrainBetween(c1: String, c2: String): TerrainType? {
        val conn = connections.firstOrNull { 
            (it.fromCityId == c1 && it.toCityId == c2) || (it.fromCityId == c2 && it.toCityId == c1)
        }
        return conn?.terrain
    }

    fun isConnected(c1: String, c2: String) = terrainBetween(c1, c2) != null

    fun link(c1: String, c2: String, terrain: TerrainType) {
        connections.add(TravelConnection(c1, c2, terrain))
    }

    fun all(): List<CityNode> {
        val allCities = CityCatalogue.all()
        return allCities.map { city ->
            val linked = neighbors(city.id).map { if (it.fromCityId == city.id) it.toCityId else it.fromCityId }
            CityNode(city, linked, city.region, city.name, x = city.hashCode() % 100, y = city.hashCode() / 100 % 100)
        }
    }

    fun get(id: String): CityNode? {
        val city = CityCatalogue.get(id) ?: return null
        val linked = neighbors(id).map { if (it.fromCityId == id) it.toCityId else it.fromCityId }
        return CityNode(city, linked, city.region, city.name, x = city.hashCode() % 100, y = city.hashCode() / 100 % 100)
    }
}
