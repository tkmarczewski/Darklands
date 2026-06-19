package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MutationEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem
) {
    fun processMutations() {
        val state = gameRepository.currentState()
        if (state.world.echoIntensity > 0.6f) {
            chronicleSystem.record("Miejscowa fauna zaczyna mutować pod wpływem Echa.", 3)
        }
        gameRepository.persistCurrentState()
    }
}
