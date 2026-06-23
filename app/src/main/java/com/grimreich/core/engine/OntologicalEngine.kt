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
            var shift = Random.nextInt(-2, 3)
            
            // If expedition is active, stability drain is much harsher
            if (state.isExpeditionActive) {
                shift -= 5 // Constant drain
                if (Random.nextFloat() < 0.3f) shift -= 3 // Extra spikes
            }
            
            state.world.globalStability = (state.world.globalStability + shift).coerceIn(0, 100)
            
            if (state.world.globalStability < 30) {
                gameRepository.log("Rzeczywistość staje się niestabilna...")
            }
        }
    }

    fun isGlitchActive(): Boolean {
        val state = gameRepository.currentState()
        val stability = state.world.globalStability
        
        // Glitches are more common during expeditions even at higher stability
        val threshold = if (state.isExpeditionActive) 60 else 40
        val baseChance = (1.0f - stability / 100f)
        val finalChance = if (state.isExpeditionActive) baseChance * 1.5f else baseChance
        
        return stability < threshold && Random.nextFloat() < finalChance
    }
}
