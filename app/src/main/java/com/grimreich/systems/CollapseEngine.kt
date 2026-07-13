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
        // Project Anchor: The Breath - End of Simulation
        if (state.world.ontologicalLevel == com.grimreich.grimreich.v1.OntologicalLevel.ABSOLUTE && state.world.globalStability >= 100) {
            state.logEntries.add("TRIBUNAL_LOG: Nie ma już błędów. Możesz przestać być Kotwicą. Świat oddycha sam.")
            return // Simulation frozen in perfection
        }

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
                state.party.forEach { h -> 
                    h.hp = (h.hp - (threshold * 10).toInt()).coerceAtLeast(0) 
                    h.normalize()
                }
                state.logEntries.add("Krew wrze w żyłach wędrowców...")
            }
            CollapseScenario.REFLECTION_RECKONING -> {
                state.party.forEach { h -> 
                    h.morale = (h.morale - (threshold * 20).toInt()).coerceAtLeast(0)
                }
                state.logEntries.add("Cienie przeszłości oskarżają Twoją duszę.")
            }
            CollapseScenario.FULLNESS_ASCENSION -> {
                // Double edged sword: Buff stats but massive echo increase
                state.party.forEach { h -> 
                    h.strength += 1
                    h.intelligence += 1
                    h.normalize()
                }
                worldStabilitySystem.changeEchoDirect(state, 0.25f, "Wzniesienie Pełni")
                state.logEntries.add("Czujesz boską potęgę, ale świat staje się nierealny.")
            }
            CollapseScenario.CHAOS_DOMINION -> {
                val effects = com.grimreich.core.StatusEffectType.entries
                state.party.forEach { h -> 
                    // Non-existent in combatant state directly, but we can log or apply corruption
                    h.corruption += 5
                    h.sanity -= 10
                    h.normalize()
                }
                state.logEntries.add("Chaos przejmuje władzę nad ciałem i umysłem.")
            }
            else -> {}
        }
        
        if (threshold >= 0.999f) {
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
