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
        gameRepository.updateState { state ->
            state.world.collapseProgress = (state.world.collapseProgress + 0.01f).coerceAtMost(1.0f)

            if (state.world.collapseProgress > 0.5f && state.world.collapseScenarioId == null) {
                val scenario = decideScenario(state.prayer.faith, state.world.globalStability)
                state.world.collapseScenarioId = scenario.name
            }

            val scenarioId = state.world.collapseScenarioId ?: return@updateState
            val scenario = try { CollapseScenario.valueOf(scenarioId) } catch (e: Exception) { null }

            scenario?.let { sc ->
                when (sc) {
                    CollapseScenario.MIST_OBLIVION -> {
                        state.world.echoIntensity = (state.world.echoIntensity + 0.02f).coerceAtMost(1.0f)
                    }
                    CollapseScenario.BLOOD_RUIN -> {
                        state.party.forEach { h -> h.hp = (h.hp - 1).coerceAtLeast(0) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun decideScenario(faith: Int, stability: Int): CollapseScenario {
        return when {
            faith > 70 -> CollapseScenario.FULLNESS_ASCENSION
            stability < 30 -> CollapseScenario.CHAOS_DOMINION
            else -> CollapseScenario.entries.toTypedArray().random()
        }
    }
}
