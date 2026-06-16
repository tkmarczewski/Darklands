package com.grimreich.systems

import com.grimreich.contracts.WorldSnapshot
import com.grimreich.contracts.SimulationTickContext
import com.grimreich.domain.collapse.*
import kotlin.random.Random

/**
 * Program 3: Collapse AI 2.0.
 * Orchestrates the progression of world scenarios based on multi-layered cognition.
 */
object CollapseAI2_0 {

    fun processCollapse(snapshot: WorldSnapshot, context: SimulationTickContext) {
        val currentState = resolveCurrentState(snapshot)
        
        // 1. Vector Shift: Adjust cognitive layers based on world events
        val shiftedCognition = applyVectorShift(currentState.cognition, snapshot)
        
        // 2. Scenario Decision: Check if we need to transition
        val nextScenario = decideScenario(shiftedCognition)
        
        // 3. Pulse: Generate global events
        if (Random.nextFloat() < 0.1f) {
            triggerCollapsePulse(nextScenario)
        }
        
        android.util.Log.d("GrimCollapse", "Collapse Scenario: $nextScenario (Progress: ${snapshot.collapseState.progress})")
    }

    private fun resolveCurrentState(snapshot: WorldSnapshot): CollapseState {
        // Map contract state to full domain model
        return CollapseState(
            activeScenario = CollapseScenarioType.valueOf(snapshot.collapseState.activeScenario),
            progress = snapshot.collapseState.progress,
            cognition = CollapseCognition(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), // Prototype defaults
            currentVector = CollapseVector(CollapseScenarioType.MIST_OBLIVION, 0.1f, CognitionLayer.MIST_MIND),
            transitionHistory = emptyList()
        )
    }

    private fun applyVectorShift(cognition: CollapseCognition, snapshot: WorldSnapshot): CollapseCognition {
        // Logic to shift cognition based on party corruption, stability, etc.
        val stabilityFactor = (1.0f - snapshot.regionState.stability)
        return cognition.copy(
            chaosFlux = cognition.chaosFlux + (stabilityFactor * 0.1f),
            zeroHollow = cognition.zeroHollow + (if (snapshot.regionState.stability < 0.2f) 0.05f else 0.0f)
        )
    }

    private fun decideScenario(cognition: CollapseCognition): CollapseScenarioType {
        // Dominant layer determines scenario
        val layers = listOf(
            cognition.mistMind to CollapseScenarioType.MIST_OBLIVION,
            cognition.bloodBody to CollapseScenarioType.BLOOD_RUIN,
            cognition.reflectionSoul to CollapseScenarioType.REFLECTION_RECKONING,
            cognition.fullnessHeart to CollapseScenarioType.FULLNESS_ASCENSION,
            cognition.chaosFlux to CollapseScenarioType.CHAOS_DOMINION,
            cognition.zeroHollow to CollapseScenarioType.ZERO_END
        )
        return layers.maxByOrNull { it.first }?.second ?: CollapseScenarioType.MIST_OBLIVION
    }

    private fun triggerCollapsePulse(scenario: CollapseScenarioType) {
        val msg = when(scenario) {
            CollapseScenarioType.MIST_OBLIVION -> "Tętno Kolapsu: Mgła pożera krańce świata."
            CollapseScenarioType.BLOOD_RUIN -> "Tętno Kolapsu: Ziemia krwawi czarną mazią."
            CollapseScenarioType.CHAOS_DOMINION -> "Tętno Kolapsu: Formy przestają mieć znaczenie."
            else -> "Tętno Kolapsu: Rzeczywistość drży."
        }
        ChronicleSystem.record(msg, 2)
    }
}
