package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.systems.CollapseEngine
import com.grimreich.systems.CollapseEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldSimulation2_0 @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldStabilitySystem: WorldStabilitySystem
) {
    fun simulate() {
        gameRepository.updateState { s -> 
            simulateDirect(s)
        }
    }

    fun simulateDirect(state: GameState) {
        worldStabilitySystem.advanceDayDirect(state, "Symulacja świata postępuje.")
    }
}
