package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem
) {
    fun processHistory() {
        val state = gameRepository.currentState()
        if (state.world.day % 10 == 0) {
            chronicleSystem.record("Kolejna dekada mroku za nami.", 2)
        }
        gameRepository.persistCurrentState()
    }
}
