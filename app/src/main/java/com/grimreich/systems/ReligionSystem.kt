package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

data class Saint(val name: String, val power: String)

@Singleton
class ReligionSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun pray(heroId: String): String {
        var heroName = ""
        gameRepository.updateState { s ->
            val hero = s.party.find { it.id == heroId } ?: return@updateState
            heroName = hero.name
            s.prayer.faith = (s.prayer.faith + 5).coerceAtMost(100)
            hero.piety = (hero.piety + 2).coerceAtMost(99)
            s.logEntries.add("$heroName wznosi modły do Absolutu.")
        }
        return if (heroName.isNotEmpty()) "$heroName modli się gorliwie. Wiara wzrasta."
               else "Brak bohatera."
    }

    fun getBlessing(): String {
        val s = gameRepository.currentState().prayer
        return if (s.faith > 80) "Łaska Absolutu (+10% Atak)" else "Brak błogosławieństwa"
    }

    fun allSaints() = listOf(
        Saint("Święty Kael", "Ochrona przed mrozem"),
        Saint("Błogosławiona Elara", "Ukojenie umysłu"),
        Saint("Męczennik Thorne", "Siła w cierpieniu")
    )

    fun getFaith(): Int = gameRepository.currentState().prayer.faith

    fun getSaintsIntercession(): String {
        val faith = getFaith()
        return when {
            faith >= 90 -> "Cud: Pełna ochrona"
            faith >= 50 -> "Interwencja: Pomoc w walce"
            else -> "Cisza: Brak odpowiedzi"
        }
    }
}
