package com.grimreich.core

import kotlin.random.Random

/**
 * Manages world exploration, movement progress, and environmental triggers.
 */
object ExplorationSystem {

    fun tick(state: GameState) {
        // Advance time
        DayNightSystem.advanceHours(DayNightState(state.world.day, 12), 1) // Simple tick
        
        // Random events
        if (Random.nextFloat() < 0.05f) {
            state.logEntries.add("Dostrzeżono coś niepokojącego na horyzoncie...")
        }
    }

    fun calculateTravelCost(from: String, to: String): Int {
        val terrain = WorldMap.terrainBetween(from, to) ?: TerrainType.ROAD
        return TravelRules.computeSegmentHours(terrain)
    }
}
