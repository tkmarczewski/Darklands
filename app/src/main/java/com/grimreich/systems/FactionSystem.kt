package com.grimreich.systems

import com.grimreich.core.FactionReputationSystem
import javax.inject.Inject
import javax.inject.Singleton

enum class FactionId {
    CHURCH, ALCHEMISTS, NOBILITY, COMMONERS, MILITARY, MERCHANTS
}

/**
 * Legacy faction support. Note: FactionId and CityFaction are slightly different.
 */
@Singleton
class FactionSystem @Inject constructor(
    private val reputationSystem: FactionReputationSystem
) {
    
    fun getReputation(faction: FactionId, cityId: String): Int {
        val factionId = mapToFactionId(faction)
        return reputationSystem.getReputation(factionId)
    }

    fun modifyReputation(faction: FactionId, cityId: String, delta: Int) {
        val factionId = mapToFactionId(faction)
        reputationSystem.changeReputation(factionId, delta)
    }

    private fun mapToFactionId(faction: FactionId): String = when (faction) {
        FactionId.CHURCH -> "CHURCH"
        FactionId.COMMONERS -> "PEASANTS"
        FactionId.NOBILITY -> "NOBILITY"
        FactionId.ALCHEMISTS -> "SCHOLARS"
        FactionId.MILITARY -> "MILITARY"
        FactionId.MERCHANTS -> "MERCHANTS"
    }
    
    fun getFactionLabel(faction: FactionId): String = when (faction) {
        FactionId.CHURCH -> "Święty Kościół"
        FactionId.ALCHEMISTS -> "Gildia Alchemików"
        FactionId.NOBILITY -> "Żelazna Szlachta"
        FactionId.COMMONERS -> "Pospólstwo"
        FactionId.MILITARY -> "Wojsko Boreas"
        FactionId.MERCHANTS -> "Gildia Kupiecka"
    }
}
