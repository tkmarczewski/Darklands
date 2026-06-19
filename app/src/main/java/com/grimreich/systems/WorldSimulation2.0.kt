package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldSimulation2_0 @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun simulate() {
        val s = gameRepository.currentState()
        s.world.day += 1
        gameRepository.persistCurrentState()
    }
}
