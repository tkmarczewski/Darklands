package com.grimreich.domain.collapse

enum class CollapseScenarioType {
    MIST_OBLIVION,
    BLOOD_RUIN,
    REFLECTION_RECKONING,
    FULLNESS_ASCENSION,
    CHAOS_DOMINION,
    ZERO_END
}

enum class CognitionLayer {
    MIST_MIND,
    BLOOD_BODY,
    REFLECTION_SOUL,
    FULLNESS_HEART,
    CHAOS_FLUX,
    ZERO_HOLLOW
}

data class CollapseCognition(
    val mistMind: Float,       // Perception & Memory
    val bloodBody: Float,      // Physical Form & Vitality
    val reflectionSoul: Float, // Identity & Truth
    val fullnessHeart: Float,  // Emotion & Connections
    val chaosFlux: Float,      // Transformation & Randomness
    val zeroHollow: Float      // Erasure & Silence
)

data class CollapseVector(
    val targetScenario: CollapseScenarioType,
    val velocity: Float,
    val dominantLayer: CognitionLayer
)

data class CollapseState(
    val activeScenario: CollapseScenarioType,
    val progress: Float,
    val cognition: CollapseCognition,
    val currentVector: CollapseVector,
    val transitionHistory: List<String>
)
