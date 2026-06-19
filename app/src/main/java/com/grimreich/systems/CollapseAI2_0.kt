package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollapseAI2_0 @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem
) {
    fun processCollapse() {
        val state = gameRepository.currentState()
        if (state.world.collapseProgress > 0.9f) {
            chronicleSystem.record("Rzeczywistość pęka u podstaw.", 5)
        }
        gameRepository.persistCurrentState()
    }
}
