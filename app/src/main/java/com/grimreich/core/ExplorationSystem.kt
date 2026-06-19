package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExplorationSystem @Inject constructor(
    private val worldMap: WorldMap
) {
    fun calculateTravelCost(from: String, to: String): Int {
        val terrain = worldMap.terrainBetween(from, to) ?: TerrainType.ROAD
        return when (terrain) {
            TerrainType.ROAD -> 5
            TerrainType.FOREST -> 10
            TerrainType.MOUNTAIN -> 20
            TerrainType.RIVER -> 8
            TerrainType.SWAMP -> 15
        }
    }
}
