package com.grimreich.domain.phenomena

enum class PhenomenonType {
    MIST,
    BLOOD,
    REFLECTION,
    FULLNESS,
    CHAOS,
    ZERO,
    ABSOLUT
}

data class PhenomenonState(
    val type: PhenomenonType,
    val intensity: Float, // 0.0 to 1.0
    val stabilityImpact: Float
)
