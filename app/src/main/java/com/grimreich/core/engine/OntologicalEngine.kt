package com.grimreich.core.engine

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * The Ontological Layer (Engine)
 * Handles world stability, reality glitches, and background echo mechanics.
 */
@Singleton
class OntologicalEngine @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun processRealityShift() {
        gameRepository.updateState { state ->
            // Random fluctuations in stability
            val shift = Random.nextInt(-2, 3)
            state.world.globalStability = (state.world.globalStability + shift).coerceIn(0, 100)
            
            if (state.world.globalStability < 30) {
                gameRepository.log("Rzeczywistość staje się niestabilna...")
            }
        }
    }

    fun isGlitchActive(): Boolean {
        val stability = gameRepository.currentState().world.globalStability
        return stability < 40 && Random.nextFloat() < (1.0f - stability / 100f)
    }
}
