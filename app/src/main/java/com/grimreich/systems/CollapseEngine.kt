package com.grimreich.systems

import com.grimreich.core.GameRepository
import kotlin.random.Random

enum class CollapseScenario {
    MIST_OBLIVION, BLOOD_RUIN, REFLECTION_RECKONING, FULLNESS_ASCENSION, CHAOS_DOMINION, ZERO_END
}

object CollapseEngine {
    
    var activeScenario: CollapseScenario? = null
    
    fun tick() {
        val g = GameRepository.state
        if (g.world.collapseProgress <= 0.0f) return
        
        // Decide scenario if not set
        if (activeScenario == null) {
            activeScenario = decideScenario()
            ChronicleSystem.record("Początek Kolapsu: ${activeScenario?.name}")
        }
        
        applyScenarioEffects()
    }
    
    private fun decideScenario(): CollapseScenario {
        val s = GameRepository.state
        val faith = s.prayer.faith
        val corruption = s.party.asSequence().map { it.corruption }.average()
        
        return when {
            faith > 60 -> CollapseScenario.FULLNESS_ASCENSION
            corruption > 70 -> CollapseScenario.BLOOD_RUIN
            Random.nextBoolean() -> CollapseScenario.MIST_OBLIVION
            else -> CollapseScenario.CHAOS_DOMINION
        }
    }
    
    private fun applyScenarioEffects() {
        when (activeScenario) {
            CollapseScenario.MIST_OBLIVION -> {
                // Mist grows
            }
            CollapseScenario.BLOOD_RUIN -> {
                // Organic growth
            }
            else -> {}
        }
    }
}
