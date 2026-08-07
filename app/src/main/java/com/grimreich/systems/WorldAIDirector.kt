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
            // BUG FIX: Limit the frequency of stability drain based on world day 
            // to avoid draining it too fast if onTick is called multiple times per day.
            // Assuming one drain per day max if gold is high.
            if (state.world.lastEncounter != state.world.day.toLong()) {
                stabilitySystem.updateStabilityDirect(state, -1)
                state.world.lastEncounter = state.world.day.toLong()
                
                // Project Cipher: Narrative link between wealth and instability
                if (state.world.day % 5 == 0) {
                    state.logEntries.add("Ciężar zgromadzonego kruszcu przyciąga pęknięcia w paradygmacie...")
                }
            }
        }
    }
}
