package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

enum class CollapseScenario {
    MIST_OBLIVION, BLOOD_RUIN, REFLECTION_RECKONING, FULLNESS_ASCENSION, CHAOS_DOMINION, ZERO_END
}

@Singleton
class CollapseEngine @Inject constructor(
    private val gameRepository: GameRepository
) {
    var activeScenario: CollapseScenario? = null

    fun tick() {
        val g = gameRepository.currentState()
        g.world.collapseProgress += 0.01f
        
        if (g.world.collapseProgress > 0.5f && activeScenario == null) {
            activeScenario = decideScenario()
        }
        
        applyScenarioEffects()
        gameRepository.persistCurrentState()
    }

    private fun decideScenario(): CollapseScenario {
        val s = gameRepository.currentState()
        val faith = s.prayer.faith
        return when {
            faith > 70 -> CollapseScenario.FULLNESS_ASCENSION
            s.world.globalStability < 30 -> CollapseScenario.CHAOS_DOMINION
            else -> CollapseScenario.values().random()
        }
    }

    private fun applyScenarioEffects() {
        activeScenario?.let {
            when (it) {
                CollapseScenario.MIST_OBLIVION -> gameRepository.currentState().world.echoIntensity += 0.02f
                CollapseScenario.BLOOD_RUIN -> gameRepository.currentState().party.forEach { h -> h.hp -= 1 }
                else -> {}
            }
        }
    }
}
