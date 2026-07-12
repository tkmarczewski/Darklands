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
        gameRepository.updateState { state ->
            if (state.world.globalStability < 5) {
                state.world.weather = WeatherType.ECLIPSE
                chronicleSystem.record("Absolut przejmuje kontrolę nad pogodą.")
            }
        }
    }
}
