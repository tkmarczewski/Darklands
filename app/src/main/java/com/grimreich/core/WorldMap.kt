package com.grimreich.core

import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

enum class TerrainType(val encounterChance: Float, val travelHoursRange: IntRange) {
    ROAD(0.1f, 4..8),
    FOREST(0.25f, 6..12),
    MOUNTAIN(0.4f, 12..24),
    RIVER(0.15f, 2..4),
    SWAMP(0.5f, 10..20)
}

data class TravelConnection(
    val fromCityId: String,
    val toCityId: String,
    val terrain: TerrainType
)

data class CityNode(
    val cityId: String,
    val connections: List<String>,
    val name: String,
    val x: Int,
    val y: Int
)

@Singleton
class WorldMap @Inject constructor() {
    private val connections = mutableListOf<TravelConnection>()

    fun seedStage1() {
        if (connections.isNotEmpty()) return
        link("wybrzeze_polnocne", "twierdza_zelazna", TerrainType.ROAD)
        link("twierdza_zelazna", "port_mglisty", TerrainType.FOREST)
        link("port_mglisty", "opactwo_ciszy", TerrainType.MOUNTAIN)
        link("opactwo_ciszy", "wybrzeze_polnocne", TerrainType.ROAD)
    }

    fun clear() {
        connections.clear()
    }

    fun allConnections(): List<TravelConnection> = connections

    fun neighbors(cityId: String): List<TravelConnection> =
        connections.filter { it.fromCityId == cityId || it.toCityId == cityId }

    fun terrainBetween(fromCityId: String, toCityId: String): TerrainType? {
        return connections.find {
            (it.fromCityId == fromCityId && it.toCityId == toCityId) ||
            (it.fromCityId == toCityId && it.toCityId == fromCityId)
        }?.terrain
    }

    fun isConnected(from: String, to: String) = terrainBetween(from, to) != null

    fun link(from: String, to: String, terrain: TerrainType) {
        if (!isConnected(from, to)) {
            connections.add(TravelConnection(from, to, terrain))
        }
    }
}
