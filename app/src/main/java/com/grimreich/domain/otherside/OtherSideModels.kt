package com.grimreich.domain.otherside

enum class OtherSideLayerType {
    MIST_SIDE,
    BLOOD_SIDE,
    REFLECTION_SIDE,
    FULLNESS_SIDE,
    CHAOS_SIDE,
    ZERO_SIDE,
    ABSOLUTE_SIDE
}

data class OtherSideState(
    val activeLayers: List<OtherSideLayerType>,
    val manifestationIntensity: Float
)
