package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldAIDirector @Inject constructor(
    private val gameRepository: GameRepository,
    private val stabilitySystem: StabilitySystem
) {
    fun onTick() {
        val state = gameRepository.currentState()
        if (state.gold > 1000) {
            stabilitySystem.updateStability(-1)
        }
    }
}
