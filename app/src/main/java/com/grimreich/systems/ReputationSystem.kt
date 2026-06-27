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
    fun modify(cityId: String, faction: CityFaction, delta: Int): Int {
        val normalizedId = cityId.lowercase().replace(" ", "_")
        var result = 0
        gameRepository.updateState { s ->
            val factions = s.reputation.cityFactions.getOrPut(normalizedId) {
                mutableMapOf(
                    CityFaction.KNIGHTS.name to 0,
                    CityFaction.MERCHANTS.name to 0,
                    CityFaction.CHURCH.name to 0,
                    CityFaction.COMMONERS.name to 0
                )
            }
            val current = factions[faction.name] ?: 0
            val next = (current + delta).coerceIn(-100, 100)
            factions[faction.name] = next
            result = next
        }
        return result
    }

    fun score(cityId: String, faction: CityFaction): Int {
        val normalizedId = cityId.lowercase().replace(" ", "_")
        return gameRepository.currentState()
            .reputation.cityFactions[normalizedId]
            ?.get(faction.name) ?: 0
    }

    fun priceModifier(cityId: String, faction: CityFaction = CityFaction.MERCHANTS): Float {
        val rep = score(cityId, faction)
        return when {
            rep >= 50  -> 0.8f
            rep >= 20  -> 0.9f
            rep <= -50 -> 1.5f
            rep <= -20 -> 1.2f
            else       -> 1.0f
        }
    }

    fun getCityRep(cityId: String): Int {
        val normalizedId = cityId.lowercase().replace(" ", "_")
        return gameRepository.currentState()
            .reputation.cityFactions[normalizedId]
            ?.values?.average()?.toInt() ?: 0
    }
}
