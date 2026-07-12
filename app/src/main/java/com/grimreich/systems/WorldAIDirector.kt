package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldAIDirector @Inject constructor(
    private val gameRepository: GameRepository,
    private val stabilitySystem: StabilitySystem
) {
    fun onTick() {
        gameRepository.updateState { state ->
            onTickDirect(state)
        }
    }

    fun onTickDirect(state: GameState) {
        if (state.gold > 1000) {
            stabilitySystem.updateStabilityDirect(state, -1)
            // Project Cipher: Narrative link between wealth and instability
            if (state.world.day % 5 == 0) {
                state.logEntries.add("Ciężar zgromadzonego kruszcu przyciąga pęknięcia w paradygmacie...")
            }
        }
    }
}
