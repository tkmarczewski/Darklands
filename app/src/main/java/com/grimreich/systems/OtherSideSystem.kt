package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtherSideSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun enterOtherSide() {
        val g = gameRepository.currentState()
        g.world.echoIntensity += 0.1f
        gameRepository.persistCurrentState()
    }
}
