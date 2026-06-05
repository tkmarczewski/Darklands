package com.grimreich.systems

import com.grimreich.world.CityCatalogue

enum class CityFaction {
    KNIGHTS,
    MERCHANTS,
    CHURCH,
    COMMONERS
}

data class CityReputation(
    val cityId: String,
    val factionScores: MutableMap<CityFaction, Int> = mutableMapOf(
        CityFaction.KNIGHTS to 0,
        CityFaction.MERCHANTS to 0,
        CityFaction.CHURCH to 0,
        CityFaction.COMMONERS to 0
    )
) {
    fun score(faction: CityFaction): Int = factionScores[faction] ?: 0
}

/**
 * Stage 1 local reputation support.
 * Reputation is isolated per city, so actions in one city do not leak into another.
 */
object ReputationSystem {
    private val localReputations = mutableMapOf<String, CityReputation>()

    fun clear() = localReputations.clear()

    fun ensureCity(cityId: String): CityReputation =
        localReputations.getOrPut(cityId) { CityReputation(cityId = cityId) }

    fun modify(cityId: String, faction: CityFaction, delta: Int): Int {
        val cityRep = ensureCity(cityId)
        val updated = (cityRep.factionScores[faction] ?: 0) + delta
        cityRep.factionScores[faction] = updated
        return updated
    }

    fun score(cityId: String, faction: CityFaction): Int = ensureCity(cityId).score(faction)

    fun priceModifier(cityId: String, faction: CityFaction = CityFaction.MERCHANTS): Float {
        val score = score(cityId, faction)
        return when {
            score >= 50  -> 0.85f
            score >= 20  -> 0.90f
            score <= -50 -> 1.20f
            score <= -20 -> 1.10f
            else         -> 1.00f
        }
    }

    fun snapshot(): Map<String, Map<CityFaction, Int>> =
        localReputations.mapValues { (_, rep) -> rep.factionScores.toMap() }

    fun getCityRep(cityId: String): Int =
        ensureCity(cityId).factionScores.values.sum()

    fun allCities(): Map<String, Int> =
        localReputations.mapValues { (_, rep) -> rep.factionScores.values.sum() }

    fun changeCity(cityId: String, delta: Int): String {
        val city = ensureCity(cityId)

        val currentTotal = city.factionScores.values.sum()
        val newTotal = (currentTotal + delta).coerceIn(-100, 100)

        val diff = newTotal - currentTotal
        if (diff != 0) {
            val currentCommoners = city.factionScores[CityFaction.COMMONERS] ?: 0
            city.factionScores[CityFaction.COMMONERS] = currentCommoners + diff
        }

        return "Reputacja miasta $cityId zmieniona o $delta (nowa suma: $newTotal)."
    }

    fun changeFaction(factionId: String, delta: Int, cityId: String = CityCatalogue.startingCityId): String {
        val faction = when (factionId.lowercase()) {
            "knights"   -> CityFaction.KNIGHTS
            "merchants" -> CityFaction.MERCHANTS
            "church"    -> CityFaction.CHURCH
            "commoners" -> CityFaction.COMMONERS
            else        -> return "Nieznana frakcja: $factionId"
        }

        val cityRep = ensureCity(cityId)
        val current = cityRep.factionScores[faction] ?: 0
        val updated = (current + delta).coerceIn(-100, 100)
        cityRep.factionScores[faction] = updated

        return "Reputacja frakcji $factionId w miescie $cityId zmieniona o $delta (nowa: $updated)."
    }

    fun getFactionRep(factionId: String, cityId: String = CityCatalogue.startingCityId): Int {
        val faction = when (factionId.lowercase()) {
            "knights"   -> CityFaction.KNIGHTS
            "merchants" -> CityFaction.MERCHANTS
            "church"    -> CityFaction.CHURCH
            "commoners" -> CityFaction.COMMONERS
            else        -> return 0
        }
        val cityRep = ensureCity(cityId)
        return cityRep.factionScores[faction] ?: 0
    }

    fun priceModifier(cityId: String): Float {
        val total = getCityRep(cityId)
        return when {
            total >= 60  -> 0.8f
            total >= 10  -> 1.0f
            total <= -60 -> 2.0f
            total <= -10 -> 1.3f
            else         -> 1.0f
        }
    }
}