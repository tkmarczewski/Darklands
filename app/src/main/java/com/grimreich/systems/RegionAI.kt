package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionAI @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun tickRegion(cityId: String) {
        gameRepository.updateState { state ->
            tickRegionDirect(state, cityId)
        }
    }

    fun tickRegionDirect(state: GameState, cityId: String) {
        val intensity = state.world.echoIntensity
        if (intensity > 0.8f) {
            // Log only at critical intensity to avoid spam
            state.logEntries.add("Region $cityId wibruje echem kolapsu.")
        }
    }
}
