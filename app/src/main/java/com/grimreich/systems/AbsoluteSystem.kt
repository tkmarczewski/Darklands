package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.WeatherType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbsoluteSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem
) {
    fun lyssaWhisper(hero: Hero): String? {
        val g = gameRepository.currentState()
        return if (g.world.collapseProgress > 0.5f) {
            "Lyssa szepcze: 'Widziałam ten koniec już wiele razy...'"
        } else {
            null
        }
    }

    fun applyAbsoluteOverride() {
        val g = gameRepository.currentState()
        if (g.world.globalStability < 5) {
            g.world.weather = WeatherType.ECLIPSE
            chronicleSystem.record("Absolut przejmuje kontrolę nad pogodą.")
            gameRepository.persistCurrentState()
        }
    }
}
