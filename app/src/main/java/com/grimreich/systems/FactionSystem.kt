package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

enum class FactionId {
    CHURCH, ALCHEMISTS, NOBILITY, COMMONERS
}

/**
 * Legacy faction support. Note: FactionId and CityFaction are slightly different.
 */
@Singleton
class FactionSystem @Inject constructor(
    private val reputationSystem: ReputationSystem
) {
    
    fun getReputation(faction: FactionId, cityId: String): Int {
        val cityFaction = mapToCityFaction(faction)
        return reputationSystem.score(cityId, cityFaction)
    }

    fun modifyReputation(faction: FactionId, cityId: String, delta: Int) {
        val cityFaction = mapToCityFaction(faction)
        reputationSystem.modify(cityId, cityFaction, delta)
    }

    private fun mapToCityFaction(faction: FactionId): CityFaction = when (faction) {
        FactionId.CHURCH -> CityFaction.CHURCH
        FactionId.COMMONERS -> CityFaction.COMMONERS
        FactionId.NOBILITY -> CityFaction.KNIGHTS
        FactionId.ALCHEMISTS -> CityFaction.MERCHANTS
    }
    
    fun getFactionLabel(faction: FactionId): String = when (faction) {
        FactionId.CHURCH -> "Święty Kościół"
        FactionId.ALCHEMISTS -> "Gildia Alchemików"
        FactionId.NOBILITY -> "Żelazna Szlachta"
        FactionId.COMMONERS -> "Pospólstwo"
    }
}
