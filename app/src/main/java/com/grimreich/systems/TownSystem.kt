package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TownSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun invest(cityId: String, amount: Int): String {
        val state = gameRepository.currentState()
        if (state.gold < amount) return "Brak złota!"
        
        state.gold -= amount
        gameRepository.persistCurrentState()
        return "Zainwestowano $amount zł w miasto $cityId."
    }
}
