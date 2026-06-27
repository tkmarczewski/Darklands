package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChurchSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun pray(heroId: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            
            // Stability affects faith efficacy
            val stability = state.world.globalStability
            val gain = if (stability > 50) 10 else 5
            
            hero.piety = (hero.piety + 1).coerceAtMost(99)
            state.prayer.faith = (state.prayer.faith + gain).coerceAtMost(100)
            
            result = "Modlitwa zakończona. Wiara wzmocniona (+${gain})."
            state.logEntries.add("${hero.name} modli się przed ołtarzem.")
            
            if (stability < 20) {
                state.logEntries.add("Słyszysz jedynie statyczny szum w odpowiedzi na modlitwę.")
            }
        }
        return result
    }

    fun makeOffering(amount: Int): String {
        var result = ""
        gameRepository.updateState { state ->
            if (state.gold >= amount) {
                state.gold -= amount
                state.prayer.virtue += amount / 10
                result = "Złożono ofiarę w wysokości $amount G."
                state.logEntries.add(result)
            } else {
                result = "Niewystarczająca ilość złota."
            }
        }
        return result
    }

    fun cleanseRelic(itemId: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val item = state.inventory.find { it.id == itemId } ?: return@updateState
            if (state.prayer.faith >= 30) {
                state.prayer.faith -= 30
                // For now, cleansing just logs success
                result = "Relikwia ${item.name} została oczyszczona z wpływów echa."
                state.logEntries.add(result)
                state.world.globalStability = (state.world.globalStability + 5).coerceAtMost(100)
            } else {
                result = "Zbyt słaba wiara, by przeprowadzić rytuał oczyszczenia."
            }
        }
        return result
    }
}
