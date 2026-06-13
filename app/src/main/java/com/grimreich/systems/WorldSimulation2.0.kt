package com.grimreich.systems

import com.grimreich.contracts.*
import com.grimreich.core.GameRepository

/**
 * Prototype implementation of the WorldSnapshotProvider for GrimReich 2.0.
 * This bridges the 1.5 GameState into the 2.0 Contract models.
 */
object WorldSimulationProviderPrototype : WorldSnapshotProvider {
    
    override fun captureSnapshot(): WorldSnapshot {
        val s = GameRepository.state
        
        return WorldSnapshot(
            timestamp = System.currentTimeMillis(),
            worldSeed = 42, // Fixed seed for prototype
            regionState = RegionStateContract(
                id = s.world.location,
                personality = "Unknown",
                stability = s.world.globalStability / 100f,
                activePhenomena = emptyList(),
                geometricLayer = s.world.ontologicalLevel.name,
                localHistory = emptyList()
            ),
            npcStates = s.party.map { hero ->
                NpcStateContract(
                    id = hero.id,
                    name = hero.name,
                    cognitiveLayer = "Base",
                    emotionalState = emptyMap(),
                    memories = emptyList(),
                    activeMutations = emptyList(),
                    version = "1.5"
                )
            },
            collapseState = ScenarioStateContract(
                activeScenario = CollapseEngine.activeScenario?.name ?: "NONE",
                progress = s.world.collapseProgress,
                dominantCognition = "None",
                transitionHistory = emptyList()
            ),
            historyState = HistoryStateContract(
                activeTimelineId = "Prime",
                openParadoxes = 0,
                isAbsoluteHistory = false
            ),
            mutationState = MutationStateContract(
                activeGlobalMutations = emptyList(),
                mutationIntensity = 0f
            ),
            phenomenaState = PhenomenaStateContract(
                activePhenomena = emptyMap()
            ),
            absoluteState = AbsoluteLayerContract(
                activeOverrides = emptyList(),
                isAbsoluteOverrideActive = false
            )
        )
    }
}
