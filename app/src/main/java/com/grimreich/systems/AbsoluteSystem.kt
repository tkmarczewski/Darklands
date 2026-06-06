package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero

object AbsoluteSystem {
    
    fun lyssaWhisper(hero: Hero): String? {
        val g = GameRepository.state
        if (g.world.collapseProgress > 0.5f) {
            return "Lyssa szepcze: 'Widziałam ten koniec już wiele razy...'"
        }
        return null
    }
    
    fun applyAbsoluteOverride() {
        // High priority override logic
        val g = GameRepository.state
        if (g.world.globalStability < 5) {
            g.world.weather = com.grimreich.core.WeatherType.ECLIPSE
            ChronicleSystem.record("Absolut przejmuje kontrolę nad pogodą.")
        }
    }
}
