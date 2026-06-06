package com.grimreich.systems

import com.grimreich.core.GameRepository

enum class FactionId {
    CHURCH, ALCHEMISTS, NOBILITY, COMMONERS
}

object FactionSystem {
    
    fun getReputation(faction: FactionId): Int {
        val g = GameRepository.state
        val factionKey = faction.name.lowercase()
        return g.reputation.city.getOrDefault(factionKey, 0)
    }

    fun modifyReputation(faction: FactionId, delta: Int) {
        val g = GameRepository.state
        val factionKey = faction.name.lowercase()
        val current = g.reputation.city.getOrDefault(factionKey, 0)
        g.reputation.city[factionKey] = current + delta
    }
    
    fun getFactionLabel(faction: FactionId): String = when (faction) {
        FactionId.CHURCH -> "Święty Kościół"
        FactionId.ALCHEMISTS -> "Gildia Alchemików"
        FactionId.NOBILITY -> "Żelazna Szlachta"
        FactionId.COMMONERS -> "Pospólstwo"
    }
}
