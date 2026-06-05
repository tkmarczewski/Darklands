package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState

enum class EndingType { GOOD, PRAGMATIC, CORRUPTED, REDEMPTION }
data class Ending(val type: EndingType, val title: String, val description: String)

object EndingSystem {
    fun resolveEnding(gameState: GameState): Ending {
        val faith = gameState.prayer.faith
        val virtue = gameState.prayer.virtue
        val cityRep = gameState.reputation.city.values.sum()
        val sins = gameState.prayer.sins
        val divineFavor = gameState.prayer.blessings
        return when {
            faith >= 60 && virtue >= 50 && cityRep >= 20 && sins <= 2 -> Ending(EndingType.GOOD, "Oczyszczenie", "Twoja wiara i cnota przyniosły pokój GrimReich.")
            faith >= 30 && cityRep >= 10 && sins <= 5 -> Ending(EndingType.PRAGMATIC, "Gorzkie Zwycięstwo", "Pokonałeś zagrożenie, lecz GrimReich pozostał poraniony.")
            divineFavor >= 5 && sins >= 6 -> Ending(EndingType.REDEMPTION, "Odkupienie", "Mimo ciężkich grzechów łaska nie opuściła cię.")
            else -> Ending(EndingType.CORRUPTED, "Skażenie", "Zbyt wiele złych wyborów. GrimReich pochłonął mrok.")
        }
    }

    fun finaleStatus(): String {
        val s = GameRepository.state
        val faith = s.prayer.faith
        val sins = s.prayer.sins
        val avgSanity = if (s.party.isNotEmpty()) s.party.map { it.sanity }.average().toInt() else 100
        val maxCorruption = if (s.party.isNotEmpty()) s.party.maxOf { it.corruption } else 0
        
        val title = when {
            faith >= 50 && sins <= 2 && avgSanity >= 70 -> "Święte Oczyszczenie"
            faith >= 20 && avgSanity >= 40 -> "Gorzkie Zwycięstwo"
            maxCorruption >= 50 -> "Upadek w Mrok"
            else -> "Wędrówka trwa"
        }
        
        return """
            === FINAŁ GRIMREICH ===
            Stan świata: $title
            
            Wiara: $faith
            Grzechy: $sins
            Średnia Poczytalność: $avgSanity%
            Najwyższa Korupcja: $maxCorruption%
            
            ${if (avgSanity < 30) "Družyna jest na krawędzi obłędu..." else ""}
        """.trimIndent()
    }
}
