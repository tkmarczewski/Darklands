package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState

enum class EndingType { GOOD, PRAGMATIC, CORRUPTED, REDEMPTION }

data class Ending(
    val type: EndingType,
    val title: String,
    val description: String
)

/**
 * Wybór zakończenia na podstawie wiary, cnoty, reputacji i grzechów gracza.
 * Grimreich 1.0 oferuje cztery zakończenia.
 */
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
                    "Twoja wiara i cnota przyniosły pokój. Kraina Grimreich odzyskała " +
                    "równowagę, a twoje imię zostało zapisane wśród jej obrońców."
                )
            faith >= 30 && cityRep >= 10 && sins <= 5 ->
                Ending(
                    EndingType.PRAGMATIC,
                    "Gorzkie Zwycięstwo",
                    "Pokonałeś zagrożenie, lecz Grimreich pozostał poraniony. " +
                    "Historia zapamięta cię jako skutecznego, nie nieskalanego."
                )
            divineFavor >= 5 && sins >= 6 ->
                Ending(
                    EndingType.REDEMPTION,
                    "Odkupienie",
                    "Mimo ciężkich grzechów łaska nie opuściła cię. " +
                    "Twoje odkupienie jest prawdziwe i kosztowne."
                )
            else ->
                Ending(
                    EndingType.CORRUPTED,
                    "Skażenie",
                    "Zbyt wiele złych wyborów. Grimreich pochłonął mrok, " +
                    "a ty stałeś się częścią ciemności, którą chciałeś pokonać."
                )
        }
    }

    fun finaleStatus(): String {
        val s = GameRepository.state
        val faith = s.prayer.faith
        val sins  = s.prayer.sins
        val title = when {
            faith >= 50 && sins <= 2 -> "Oczyszczenie"
            faith >= 20              -> "Gorzkie Zwycięstwo"
            sins  >= 5               -> "Skażenie"
            else                     -> "Wędrówka trwa"
        }
        return "Finał: $title\n\nWiara: $faith\nGrzechy: $sins"
    }
}
