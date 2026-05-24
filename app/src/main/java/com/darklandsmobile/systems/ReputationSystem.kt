package com.darklandsmobile.systems

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
            score >= 50 -> 0.85f
            score >= 20 -> 0.90f
            score <= -50 -> 1.20f
            score <= -20 -> 1.10f
            else -> 1.00f
        }
    }

    fun snapshot(): Map<String, Map<CityFaction, Int>> =
        localReputations.mapValues { (_, rep) -> rep.factionScores.toMap() }
}
