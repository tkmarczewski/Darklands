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
            // BUG FIX: Add mechanical impact to investment
            val stabilityBonus = (amount / 20).coerceIn(1, 10)
            state.world.globalStability = (state.world.globalStability + stabilityBonus).coerceAtMost(100)
            
            state.logEntries.add("Zainwestowano $amount zł w miasto $cityId. Stabilność wzrosła o $stabilityBonus.")
            result = "Zainwestowano $amount zł w miasto $cityId."
        }
        return result
    }
}
