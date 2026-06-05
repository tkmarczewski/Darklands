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
        val title = when {
            faith >= 50 && sins <= 2 -> "Oczyszczenie"
            faith >= 20 -> "Gorzkie Zwycięstwo"
            sins >= 5 -> "Skażenie"
            else -> "Wędrówka trwa"
        }
        return """Finał: $title

Wiara: $faith
Grzechy: $sins""".trimIndent()
    }
}
