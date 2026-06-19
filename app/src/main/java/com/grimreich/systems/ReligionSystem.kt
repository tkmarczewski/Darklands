package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import javax.inject.Inject
import javax.inject.Singleton

data class Saint(val name: String, val power: String)

@Singleton
class ReligionSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun pray(hero: Hero): String {
        val s = gameRepository.currentState().prayer
        s.faith += 5
        hero.piety += 2
        gameRepository.persistCurrentState()
        return "${hero.name} modli się gorliwie. Wiara wzrasta."
    }

    fun getBlessing(): String {
        val s = gameRepository.currentState().prayer
        return if (s.faith > 50) "Łaska Proroków spływa na drużynę." else "Cisza w niebiosach."
    }

    fun allSaints(): List<Saint> = listOf(
        Saint("Aelion", "Wizje Mroku"),
        Saint("Malleus", "Młot na Czarownice"),
        Saint("Sophia", "Światło Wiedzy")
    )

    fun getFaith() = gameRepository.currentState().prayer.faith

    fun getSaintsIntercession(): String {
        val faith = getFaith()
        return when {
            faith > 80 -> "Prorocy krzyczą Twoje imię!"
            faith > 40 -> "Słyszysz odległe psalmy."
            else -> "Tylko szum mgły..."
        }
    }
}
