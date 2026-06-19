package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StabilitySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun updateStability(delta: Int) {
        val g = gameRepository.currentState()
        g.world.globalStability = (g.world.globalStability + delta).coerceIn(0, 100)
        gameRepository.persistCurrentState()
    }

    fun getStabilityModifier(): Float {
        val stability = gameRepository.currentState().world.globalStability
        return when {
            stability < 20 -> 1.5f
            stability < 50 -> 1.2f
            else -> 1.0f
        }
    }
}
