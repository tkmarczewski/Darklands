package com.darklandsmobile.core

import com.darklandsmobile.world.CityCatalogue

enum class TerrainType(
    val encounterChance: Float,
    val travelHoursRange: IntRange
) {
    ROAD(encounterChance = 0.10f, travelHoursRange = 2..3),
    FOREST(encounterChance = 0.30f, travelHoursRange = 3..5),
    MOUNTAIN(encounterChance = 0.40f, travelHoursRange = 4..6),
    RIVER(encounterChance = 0.20f, travelHoursRange = 3..4),
    SWAMP(encounterChance = 0.35f, travelHoursRange = 4..6)
}

data class TravelConnection(
    val fromCityId: String,
    val toCityId: String,
    val terrain: TerrainType
)

/**
 * TODO[map] Fine tune historical routes and distances.
 * core/ contains pure world traversal rules; world/ contains the actual content data.
 */
object WorldMap {
    private val connections = mutableListOf<TravelConnection>()

    fun seedStage1() {
        if (connections.isNotEmpty()) return
        CityCatalogue.seedSprint1()

        link("magdeburg", "hamburg", TerrainType.ROAD)
        link("magdeburg", "lubeck", TerrainType.FOREST)
        link("magdeburg", "frankfurt", TerrainType.ROAD)
        link("magdeburg", "breslau", TerrainType.ROAD)
        link("hamburg", "lubeck", TerrainType.RIVER)
        link("koln", "frankfurt", TerrainType.ROAD)
        link("koln", "strasbourg", TerrainType.RIVER)
        link("frankfurt", "nurnberg", TerrainType.FOREST)
        link("frankfurt", "strasbourg", TerrainType.ROAD)
        link("nurnberg", "augsburg", TerrainType.ROAD)
        link("nurnberg", "praha", TerrainType.FOREST)
        link("nurnberg", "wien", TerrainType.MOUNTAIN)
        link("praha", "breslau", TerrainType.ROAD)
        link("praha", "wien", TerrainType.MOUNTAIN)
        link("augsburg", "wien", TerrainType.MOUNTAIN)
        link("augsburg", "strasbourg", TerrainType.ROAD)
        link("breslau", "wien", TerrainType.ROAD)
    }

    fun clear() = connections.clear()

    fun allConnections(): List<TravelConnection> = connections.toList()

    fun neighbors(cityId: String): List<TravelConnection> = connections.filter {
        it.fromCityId == cityId || it.toCityId == cityId
    }

    fun terrainBetween(cityA: String, cityB: String): TerrainType? =
        connections.firstOrNull {
            (it.fromCityId == cityA && it.toCityId == cityB) ||
                (it.fromCityId == cityB && it.toCityId == cityA)
        }?.terrain

    fun isConnected(cityA: String, cityB: String): Boolean = terrainBetween(cityA, cityB) != null

    private fun link(a: String, b: String, terrain: TerrainType) {
        connections += TravelConnection(a, b, terrain)
    }
}
