package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldSimulationCoordinator @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldSimulation2_0: WorldSimulation2_0,
    private val aiDirector: WorldAIDirector
) {
    fun executeTick() {
        worldSimulation2_0.simulate()
        aiDirector.onTick()
        gameRepository.persistCurrentState()
    }
}
