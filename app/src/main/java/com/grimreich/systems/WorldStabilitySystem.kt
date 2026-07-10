package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldStabilitySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    /**
     * Zmienia stabilność świata. 
     * Wartość jest automatycznie ograniczana do zakresu 0-100 przez normalizeState().
     */
    fun changeStability(delta: Int, reason: String) {
        gameRepository.updateState { state ->
            val before = state.world.globalStability
            state.world.globalStability += delta
            state.normalizeState()
            val after = state.world.globalStability
            
            if (before != after) {
                state.logEntries.add("Stabilność: $after (${if (delta > 0) "+" else ""}$delta). Powód: $reason")
            }
        }
    }

    /**
     * Zmienia intensywność echa (0.0 - 1.0).
     */
    fun changeEcho(delta: Float, reason: String) {
        gameRepository.updateState { state ->
            val before = state.world.echoIntensity
            state.world.echoIntensity += delta
            state.normalizeState()
            val after = state.world.echoIntensity
            
            if (before != after) {
                state.logEntries.add("Echa: ${"%.2f".format(after)}. Powód: $reason")
            }
        }
    }

    /**
     * Przesuwa postęp upadku rzeczywistości.
     */
    fun advanceCollapse(delta: Float, reason: String) {
        gameRepository.updateState { state ->
            val before = state.world.collapseProgress
            state.world.collapseProgress += delta
            state.normalizeState()
            val after = state.world.collapseProgress
            
            if (before != after) {
                state.logEntries.add("Upadek: ${"%.1f".format(after * 100)}%. Powód: $reason")
            }
        }
    }
}
