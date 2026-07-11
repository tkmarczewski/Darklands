package com.grimreich.systems

import com.grimreich.contracts.CollapseRandomProvider
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.CollapseScenario
import com.grimreich.systems.CollapseEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollapseEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldStabilitySystem: WorldStabilitySystem,
    private val collapseRandomProvider: CollapseRandomProvider
) {

    fun processCollapseEvent(event: CollapseEvent) {
        gameRepository.updateState { state ->
            processCollapseEventDirect(state, event)
        }
    }

    fun processCollapseEventDirect(state: GameState, event: CollapseEvent) {
        val progressBefore = state.world.collapseProgress
        
        worldStabilitySystem.advanceCollapseDirect(state, event)
        
        val progressAfter = state.world.collapseProgress

        if (progressBefore <= 0.5f && progressAfter > 0.5f && state.world.collapseScenarioId == null) {
            val scenario = decideScenario(state.prayer.faith, state.world.globalStability)
            state.world.collapseScenarioId = scenario.name
        }

        applyThresholdEffectsDirect(state, progressBefore, progressAfter)
    }

    private fun applyThresholdEffectsDirect(state: GameState, before: Float, after: Float) {
        val thresholds = listOf(0.6f, 0.75f, 0.9f, 1.0f)
        
        thresholds.forEach { threshold ->
            if (before < threshold && after >= threshold) {
                if (state.world.reachedThresholds.add(threshold)) {
                    triggerEffectDirect(state, threshold)
                }
            }
        }
    }

    private fun triggerEffectDirect(state: GameState, threshold: Float) {
        val scenarioId = state.world.collapseScenarioId ?: return
        val scenario = try { CollapseScenario.valueOf(scenarioId) } catch (e: Exception) { return }

        state.logEntries.add("KRYZYS: Przekroczono próg upadku ${(threshold * 100).toInt()}%!")

        when (scenario) {
            CollapseScenario.MIST_OBLIVION -> {
                worldStabilitySystem.changeEchoDirect(state, 0.15f, "Próg Upadku")
            }
            CollapseScenario.BLOOD_RUIN -> {
                state.party.forEach { h -> h.hp = (h.hp - (threshold * 10).toInt()).coerceAtLeast(0) }
            }
            else -> {}
        }
        
        if (threshold >= 1.0f) {
            state.logEntries.add("KONIEC: Rzeczywistość przestała istnieć.")
        }
    }

    private fun decideScenario(faith: Int, stability: Int): CollapseScenario {
        val availableScenarios = CollapseScenario.entries.filter { it != CollapseScenario.ZERO_END }.toList()
        return when {
            faith > 70 -> CollapseScenario.FULLNESS_ASCENSION
            stability < 30 -> CollapseScenario.CHAOS_DOMINION
            else -> collapseRandomProvider.chooseScenario(availableScenarios)
        }
    }
}
