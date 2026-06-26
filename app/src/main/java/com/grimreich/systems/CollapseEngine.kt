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
        // Fix: clamp collapseProgress to [0.0, 1.0] to prevent overflow
        g.world.collapseProgress = (g.world.collapseProgress + 0.01f).coerceAtMost(1.0f)

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
                CollapseScenario.MIST_OBLIVION -> {
                    val state = gameRepository.currentState()
                    // Fix: clamp echoIntensity to [0.0, 1.0] to prevent unbounded growth
                    state.world.echoIntensity = (state.world.echoIntensity + 0.02f).coerceAtMost(1.0f)
                }
                CollapseScenario.BLOOD_RUIN -> {
                    val state = gameRepository.currentState()
                    // Fix: clamp hero HP to >= 0 on each tick
                    state.party.forEach { h -> h.hp = (h.hp - 1).coerceAtLeast(0) }
                }
                else -> {}
            }
        }
    }
}
