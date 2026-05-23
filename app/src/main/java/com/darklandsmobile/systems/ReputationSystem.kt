package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.FactionReputation.FactionCatalogue

object ReputationSystem {

    // --- reputacja per miasto ---
    fun changeCity(city: String, delta: Int): String {
        val rep = GameRepository.state.reputation
        rep.city[city] = (rep.city.getOrDefault(city, 0) + delta).coerceIn(-100, 100)
        GameRepository.log("Reputacja w $city: ${rep.city[city]}")
        return "Reputacja w $city: ${rep.city[city]}"
    }

    fun getCityRep(city: String): Int =
        GameRepository.state.reputation.city.getOrDefault(city, 0)

    fun allCities(): Map<String, Int> =
        GameRepository.state.reputation.city.toMap()

    // --- reputacja per frakcja ---
    fun changeFaction(factionId: String, delta: Int): String {
        val faction = FactionCatalogue.all().firstOrNull { it.id == factionId }
            ?: return "Nieznana frakcja: $factionId"
        val rep = GameRepository.state.reputation
        rep.faction[factionId] = (rep.faction.getOrDefault(factionId, 0) + delta).coerceIn(-100, 100)
        GameRepository.log("Reputacja u ${faction.name}: ${rep.faction[factionId]}")
        return "Reputacja u ${faction.name}: ${rep.faction[factionId]}"
    }

    fun getFactionRep(factionId: String): Int =
        GameRepository.state.reputation.faction.getOrDefault(factionId, 0)

    fun allFactions(): Map<String, Int> =
        GameRepository.state.reputation.faction.toMap()

    // --- efekty na ceny ---
    fun priceModifier(city: String): Float {
        val rep = getCityRep(city)
        return when {
            rep >= 50  -> 0.8f
            rep >= 0   -> 1.0f
            rep >= -50 -> 1.3f
            else       -> 2.0f
        }
    }
}
