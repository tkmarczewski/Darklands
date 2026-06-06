package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue

enum class SocialEventType {
    DRINKING, GOSSIP, BRAWL, CEREMONY
}

object SocialEventSystem {
    
    fun cityAudience(cityId: String, saintId: String?): String {
        val city = CityCatalogue.get(cityId) ?: return "Miasto spowite mrokiem."
        return buildString {
            appendLine("=== AUDIENCJA: ${city.name.uppercase()} ===")
            appendLine("Główny fenomen: ${city.phenomenon}")
            appendLine("Rządząca frakcja: ${city.rulingFaction}")
            if (saintId != null) {
                appendLine("Obecny Prorok: $saintId")
            }
            appendLine()
            appendLine("Ludzie szepczą o nadchodzących pęknięciach rzeczywistości.")
        }
    }

    fun runTavernEvent(): String {
        val g = GameRepository.state
        if (g.gold < 10) return "Brak złota na wejście do karczmy."
        
        g.gold -= 10
        val eventType = SocialEventType.entries.random()
        
        return when(eventType) {
            SocialEventType.DRINKING -> {
                g.party.forEach { it.morale = (it.morale + 10).coerceAtMost(100) }
                "Picie z miejscowymi podniosło morale drużyny."
            }
            SocialEventType.GOSSIP -> "Usłyszeliście plotki o zbliżającej się mgle."
            SocialEventType.BRAWL -> {
                g.party.forEach { it.hp -= 2 }
                "Karczemna bójka! Kilka siniaków, ale respekt zdobyty."
            }
            SocialEventType.CEREMONY -> "W karczmie odbywa się dziwny, milczący rytuał."
        }
    }
}
