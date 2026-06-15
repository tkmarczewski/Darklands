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

data class CollapseState(
    val activeScenario: CollapseScenarioType,
    val progress: Float,
    val dominantLayer: CognitionLayer
)
