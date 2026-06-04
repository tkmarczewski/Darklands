package com.darklandsmobile.core

data class Segment(
    val id: Int,
    var monsterEncounterRate: Float = 0.0f,
    var monsterDifficultyMultiplier: Float = 1.0f,
    var infestationChance: Float = 0.0f
)

data class LegacyRegion(
    val id: Int,
    val name: String
)

data class WorldSegment(
    val regions: List<LegacyRegion> = emptyList(),
    val segments: List<Segment> = emptyList()
)

object WorldAI {
    const val CENTRAL_WORLD_ID = 0
    val currentSegments = listOf(WorldSegment())
    fun calculateBaseScale(region: LegacyRegion): Float = 1.0f
}

data class NPC(
    val id: Int,
    val name: String,
    val type: String,
    val regionId: Int
)
