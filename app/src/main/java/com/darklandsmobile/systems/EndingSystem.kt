package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.GameState

enum class EndingType { GOOD, PRAGMATIC, CORRUPTED, REDEMPTION }

data class Ending(
    val type: EndingType,
    val title: String,
    val description: String
)

// Wybor zakonczenia liczony z stanu modlitwy (faith/virtue/sins/blessings) i sumy reputacji w miastach.
object EndingSystem {
    fun resolveEnding(gameState: GameState): Ending {
        val faith = gameState.prayer.faith
        val virtue = gameState.prayer.virtue
        val cityRep = gameState.reputation.city.values.sum()
        val sins = gameState.prayer.sins
        val divineFavor = gameState.prayer.blessings

        return when {
            faith >= 60 && virtue >= 50 && cityRep >= 20 && sins <= 2 ->
                Ending(
                    EndingType.GOOD,
                    "Oczyszczenie",
                    "Twoja wiara i cnota przyniosly pokoj. Swiat odzyskal rownowage, " +
                    "a twoje imie zostalo zapisane wsrod swietych obroncow."
                )
            faith >= 30 && cityRep >= 10 && sins <= 5 ->
                Ending(
                    EndingType.PRAGMATIC,
                    "Gorzkie Zwyciestwo",
                    "Pokonales zagrozenie, ale swiat pozostal poraniony. " +
                    "Historia zapamieta cie jako skutecznego, lecz nie czystego."
                )
            divineFavor >= 5 && sins >= 6 ->
                Ending(
                    EndingType.REDEMPTION,
                    "Odkupienie",
                    "Mimo ciezkich strat i grzechow, laska boska nie opuscila cie. " +
                    "Twoje odkupienie jest prawdziwe i kosztowne."
                )
            else ->
                Ending(
                    EndingType.CORRUPTED,
                    "Skazenie",
                    "Zbyt wiele zlych wyborow. Swiat pochlonelo skazenie, " +
                    "a ty stales sie czescia ciemnosci, ktora chciales pokonac."
                )
        }
    }

    // Sprint 17: tekstowy status finalu (Baphomet / koniec gry) dla BaphometActivity.
    fun finaleStatus(): String {
        val s = GameRepository.state
        val faith = s.prayer.faith
        val sins  = s.prayer.sins
        val title = when {
            faith >= 50 && sins <= 2 -> "Oczyszczenie"
            faith >= 20              -> "Gorzkie Zwyciestwo"
            sins  >= 5               -> "Skazenie"
            else                     -> "Pielgrzymka trwa"
        }
        return "Finale: $title\n\nWiara: $faith\nGrzechy: $sins"
    }
}
