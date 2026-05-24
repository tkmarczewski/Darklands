package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.GameState

enum class EndingType { GOOD, PRAGMATIC, CORRUPTED, REDEMPTION }

data class Ending(
    val type: EndingType,
    val title: String,
    val description: String
)

object EndingSystem {
    fun resolveEnding(gameState: GameState): Ending {
        val faith = gameState.religion.faith
        val virtue = gameState.religion.virtue
        val cityRep = gameState.reputation.cityReputation
        val sins = gameState.religion.sins
        val divineFavor = gameState.religion.divineFavor

        return when {
            faith >= 8 && virtue >= 6 && cityRep >= 5 && sins <= 2 ->
                Ending(
                    EndingType.GOOD,
                    "Oczyszczenie",
                    "Twoja wiara i cnota przyniosły pokój. Świat odzyskał równowagę, " +
                    "a twoje imię zostało zapisane wśród świętych obrońców."
                )
            faith >= 5 && cityRep >= 3 && sins <= 5 ->
                Ending(
                    EndingType.PRAGMATIC,
                    "Gorzkie Zwycięstwo",
                    "Pokonałeś zagrożenie, ale świat pozostał poranny. " +
                    "Historia zapamięta cię jako skutecznego, lecz nie czystego."
                )
            divineFavor >= 10 && sins >= 6 ->
                Ending(
                    EndingType.REDEMPTION,
                    "Odkupienie",
                    "Mimo ciężkich strat i grzechów, łaska boska nie opuściła cię. " +
                    "Twoje odkupienie jest prawdziwe i kosztowne."
                )
            else ->
                Ending(
                    EndingType.CORRUPTED,
                    "Skażenie",
                    "Zbyt wiele złych wyborów. Świat pochłonęło skażenie, " +
                    "a ty stałeś się częścią ciemności, którą chciałeś pokonać."
                )
        }
    }

    // Sprint 17: tekstowy status finalu (Baphomet / koniec gry) dla BaphometActivity (UI sprintu 12+).
    // Nie wywoluje resolveEnding bezposrednio, bo wymaga to pol stanu z resztki repo;
    // zwraca tekst opisujacy biezacy etap finalu w oparciu o stan modlitwy/reputacji.
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
