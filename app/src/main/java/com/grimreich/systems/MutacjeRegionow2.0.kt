package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

enum class MutationType { NONE, PEACEFUL, ANOMALY, HOSTILE, EXTREME, GRIMREICH }

data class RegionMutation(
    val regionId: Int,
    var baseScale: Float,
    var activeScale: Float,
    var mutationType: MutationType,
    var mutationIntensity: Float = 0f
)

@Singleton
class MutacjeRegionow2_0 @Inject constructor() {
    private val mutations = mutableMapOf<Int, RegionMutation>()

    fun initialize() {
        mutations.clear()
    }

    fun getRegionMutation(id: Int): RegionMutation? = mutations[id]
}
