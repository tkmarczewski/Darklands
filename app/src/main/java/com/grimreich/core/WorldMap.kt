package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

enum class TerrainType(val encounterChance: Float, val travelHoursRange: IntRange) {
    ROAD(0.1f, 4..8),
    FOREST(0.3f, 8..14),
    MOUNTAIN(0.5f, 16..24),
    RIVER(0.2f, 6..12),
    SWAMP(0.4f, 12..20),
    TRAIL(0.25f, 10..18)
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
    val connections = mutableListOf<TravelConnection>()

    fun seedStage1(seed: Int = 1) {
        if (connections.isNotEmpty()) return
        
        val random = kotlin.random.Random(seed.toLong())
        
        // Canonical connections
        link("wybrzeze_polnocne", "twierdza_zelazna", TerrainType.ROAD)
        link("twierdza_zelazna", "port_mglisty", TerrainType.ROAD)
        link("port_mglisty", "opactwo_ciszy", TerrainType.TRAIL)
        
        // Dynamic connections based on seed
        if (random.nextFloat() < 0.5f) {
            link("wybrzeze_polnocne", "port_mglisty", TerrainType.SWAMP)
        }
        if (random.nextFloat() < 0.3f) {
            link("opactwo_ciszy", "twierdza_zelazna", TerrainType.MOUNTAIN)
        }
        
        // Link all to heart
        link("twierdza_zelazna", "serce_krainy", TerrainType.ROAD)
        link("port_mglisty", "serce_krainy", TerrainType.RIVER)
    }

    fun clear() {
        connections.clear()
    }

    fun allConnections(): List<TravelConnection> = connections

    fun neighbors(cityId: String): List<TravelConnection> =
        connections.filter { it.fromCityId == cityId || it.toCityId == cityId }

    fun terrainBetween(cityA: String, cityB: String): TerrainType? {
        return connections.firstOrNull { 
            (it.fromCityId == cityA && it.toCityId == cityB) ||
            (it.fromCityId == cityB && it.toCityId == cityA)
        }?.terrain
    }

    fun isConnected(cityA: String, cityB: String): Boolean = terrainBetween(cityA, cityB) != null

    fun link(id1: String, id2: String, terrain: TerrainType) {
        if (!isConnected(id1, id2)) {
            connections.add(TravelConnection(id1, id2, terrain))
        }
    }

    fun validateCityReferences(validCityIds: Set<String>): List<String> {
        val issues = mutableListOf<String>()
        connections.forEach {
            if (it.fromCityId !in validCityIds) issues.add("Brak miasta fromCityId=${it.fromCityId}")
            if (it.toCityId !in validCityIds) issues.add("Brak miasta toCityId=${it.toCityId}")
        }
        return issues.distinct()
    }
}
