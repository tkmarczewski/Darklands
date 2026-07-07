package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TownSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun invest(cityId: String, amount: Int): String {
        var result = ""
        gameRepository.updateState { state ->
            if (state.gold < amount) {
                result = "Brak złota!"
                return@updateState
            }
            state.gold -= amount
            state.logEntries.add("Zainwestowano $amount zł w miasto $cityId.")
            result = "Zainwestowano $amount zł w miasto $cityId."
        }
        return result
    }
}
