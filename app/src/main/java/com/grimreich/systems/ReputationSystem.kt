package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

enum class CityFaction {
    KNIGHTS, MERCHANTS, CHURCH, COMMONERS
}

@Singleton
class ReputationSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private fun getCityFactions(cityId: String): MutableMap<String, Int> {
        val state = gameRepository.currentState()
        val normalizedId = cityId.lowercase().replace(" ", "_")
        return state.reputation.cityFactions.getOrPut(normalizedId) {
            mutableMapOf(
                CityFaction.KNIGHTS.name to 0,
                CityFaction.MERCHANTS.name to 0,
                CityFaction.CHURCH.name to 0,
                CityFaction.COMMONERS.name to 0
            )
        }
    }

    fun modify(cityId: String, faction: CityFaction, delta: Int): Int {
        val factions = getCityFactions(cityId)
        val current = factions[faction.name] ?: 0
        val next = (current + delta).coerceIn(-100, 100)
        factions[faction.name] = next
        gameRepository.persistCurrentState()
        return next
    }

    fun score(cityId: String, faction: CityFaction): Int {
        return getCityFactions(cityId)[faction.name] ?: 0
    }

    fun priceModifier(cityId: String, faction: CityFaction = CityFaction.MERCHANTS): Float {
        val rep = score(cityId, faction)
        return when {
            rep >= 50 -> 0.8f
            rep >= 20 -> 0.9f
            rep <= -50 -> 1.5f
            rep <= -20 -> 1.2f
            else -> 1.0f
        }
    }

    fun getCityRep(cityId: String): Int {
        return getCityFactions(cityId).values.average().toInt()
    }
}
