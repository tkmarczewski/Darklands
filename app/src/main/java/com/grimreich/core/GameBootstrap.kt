package com.grimreich.core

import com.grimreich.grimreich.v1.*

object GameBootstrap {
    fun init(state: GameState = GameState()): GameState {
        // Zamiast seedGrimWorld(state.grimEngine), zasilamy czysty GameState
        seedWorldState(state)
        return state
    }

    private fun seedWorldState(state: GameState) {
        val regions = GrimRegionCatalogue.regions
        for (entry in regions) {
            // Bezpośrednia mutacja stanu świata, zamiast przez engine
            state.world.discoveredLocations.add(entry.regionName)
        }
        // Inne inicjalizacje zostaną przeniesione do odpowiednich managerów
    }
}
