package com.grimreich.contracts

/**
 * Base contract for NPC state in GrimReich 2.0.
 */
data class NpcStateContract(
    val id: String,
    val name: String,
    val cognitiveLayer: String, // e.g., "MistMind"
    val emotionalState: Map<String, Float>,
    val memories: List<String>,
    val activeMutations: List<String>,
    val version: String // AbsoluteVersion, etc.
)

/**
 * Base contract for Region state in GrimReich 2.0.
 */
data class RegionStateContract(
    val id: String,
    val personality: String,
    val stability: Float,
    val activePhenomena: List<String>,
    val geometricLayer: String, // "Material", "Absolute", etc.
    val localHistory: List<String>
)

/**
 * Base contract for Collapse/Scenario state.
 */
data class ScenarioStateContract(
    val activeScenario: String,
    val progress: Float,
    val dominantCognition: String,
    val transitionHistory: List<String>
)

/**
 * Base contract for History/Timeline state.
 */
data class HistoryStateContract(
    val activeTimelineId: String,
    val openParadoxes: Int,
    val isAbsoluteHistory: Boolean
)

/**
 * Base contract for Mutation engine state.
 */
data class MutationStateContract(
    val activeGlobalMutations: List<String>,
    val mutationIntensity: Float
)

/**
 * Base contract for Phenomena engine state.
 */
data class PhenomenaStateContract(
    val activePhenomena: Map<String, Float> // Name to intensity
)

/**
 * Base contract for Absolute Layer overrides.
 */
data class AbsoluteLayerContract(
    val activeOverrides: List<String>,
    val isAbsoluteOverrideActive: Boolean
)
