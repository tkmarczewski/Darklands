package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StabilitySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun updateStability(delta: Int) {
        gameRepository.updateState { state ->
            state.world.globalStability = (state.world.globalStability + delta).coerceIn(0, 100)
            if (delta < 0) state.logEntries.add("Stabilność rzeczywistości słabnie...")
        }
    }

    fun getStabilityModifier(): Float {
        val stability = gameRepository.currentState().world.globalStability
        return when {
            stability >= 90 -> 1.0f
            stability >= 50 -> 0.8f
            stability >= 20 -> 0.5f
            else -> 0.2f
        }
    }
}
