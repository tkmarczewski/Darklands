package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhenomenaEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem
) {
    fun processPhenomena() {
        val state = gameRepository.currentState()
        if (state.world.globalStability < 20) {
            chronicleSystem.record("Zjawiska anomalne nasilają się.", 4)
        }
        gameRepository.persistCurrentState()
    }
}
