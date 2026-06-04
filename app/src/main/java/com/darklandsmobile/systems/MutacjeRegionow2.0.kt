package com.darklandsmobile.systems

import com.darklandsmobile.core.GameState
import com.darklandsmobile.core.Segment
import com.darklandsmobile.core.WorldAI

/**
 * MutacjeRegionow2.0 - System mutacji regionów dla GrimReich.
 * Zarządza skalą mutacji, typem mutacji, cascadami i synchronizacją z WorldAI.
 */
data class RegionMutation(
    val regionId: Int,
    val baseScale: Float,
    var activeScale: Float,
    var mutationType: MutationType,
    val mutationIntensity: Float,
    val affectedSegments: List<Segment>,
    val cascadeTriggered: Boolean = false,
    val reinkarnationActive: Boolean = false
)

enum class MutationType {
    NONE,
    PEACEFUL,
    ANOMALY,
    HOSTILE,
    EXTREME,
    GRIMREICH
}

object MutacjeRegionow2_0 {

    private val mutations = mutableMapOf<Int, RegionMutation>()
    private var globalGrimReichFactor = 1.0f

    fun initialize(gameState: GameState) {
        mutations.clear()
        globalGrimReichFactor = 1.0f
        // Inicjalizacja wszystkich regionów z wpływów WorldAI
        WorldAI.currentSegments[0].regions.onEach { region ->
            val baseScale = WorldAI.calculateBaseScale(region)
            mutations[region.id] = RegionMutation(
                regionId = region.id,
                baseScale = baseScale,
                activeScale = baseScale,
                mutationType = MutationType.NONE,
                mutationIntensity = 0.0f,
                affectedSegments = listOf()
            )
        }
    }

    fun applyGrimReichFactor(factor: Float) {
        globalGrimReichFactor = factor.coerceIn(0.0f, 3.0f)
        mutations.forEach { (_, m) ->
            m.activeScale = calculateActiveScale(m, globalGrimReichFactor)
            m.mutationType = determineMutationType(m, globalGrimReichFactor)
        }
    }

    private fun calculateActiveScale(mutation: RegionMutation, factor: Float): Float {
        val grimReichMultiplier = 1.0f + (factor - 1.0f) * 0.5f
        return mutation.baseScale * grimReichMultiplier
    }

    private fun determineMutationType(mutation: RegionMutation, factor: Float): MutationType {
        return when {
            factor >= 2.5f -> MutationType.GRIMREICH
            factor >= 2.0f -> MutationType.EXTREME
            factor >= 1.5f -> MutationType.HOSTILE
            factor >= 1.2f -> MutationType.ANOMALY
            factor >= 1.0f -> MutationType.PEACEFUL
            else -> MutationType.NONE
        }
    }

    fun getCascadeRegions(regionId: Int): List<RegionMutation> {
        val mutation = mutations[regionId] ?: return emptyList()
        val baseType = mutation.mutationType
        val cascade = mutations.filter { (_, m) ->
            m.mutationType == baseType && m.regionId != regionId
        }
        return cascade.values.toList()
    }

    fun triggerCascade(regionId: Int) {
        val mutation = mutations[regionId] ?: return
        val cascadeRegions = getCascadeRegions(regionId)
        cascadeRegions.forEach { m ->
            m.activeScale *= 1.3f
        }
    }

    fun getRegionMutation(regionId: Int): RegionMutation? = mutations[regionId]
    fun getAllMutations(): Collection<RegionMutation> = mutations.values
    fun getMutationCount(): Int = mutations.size

    fun updateSegmentInfestation(gameState: GameState, segmentId: Int) {
        val segment = WorldAI.currentSegments[0].segments.find { it.id == segmentId } ?: return
        val totalInfestation = mutations.values
            .filter { m ->
                m.affectedSegments.any { s -> s.id == segmentId }
            }
            .sumOf { it.activeScale.toDouble() }.toFloat()
        segment.infestationChance = totalInfestation.coerceIn(0.0f, 100.0f)
    }
}
