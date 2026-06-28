package com.grimreich.core

import kotlin.math.max
import kotlin.random.Random

data class TravelPartyState(
    val fatigue: Int = 0,
    val totalHoursTraveled: Int = 0,
    val lastEncounterId: String? = null
)

data class TravelResult(
    val destinationCityId: String,
    val terrain: TerrainType,
    val hoursSpent: Int,
    val fatigueBefore: Int,
    val fatigueAfter: Int,
    val encounterTriggered: Boolean,
    val encounterId: String?
)

object TravelRules {
    fun computeSegmentHours(terrain: TerrainType, random: Random = Random.Default): Int {
        val range = terrain.travelHoursRange
        return random.nextInt(range.first, range.last + 1)
    }

    fun computeFatigueGain(terrain: TerrainType, hoursSpent: Int): Int {
        val terrainBase = when (terrain) {
            TerrainType.ROAD -> 1
            TerrainType.FOREST -> 2
            TerrainType.MOUNTAIN -> 3
            TerrainType.RIVER -> 2
            TerrainType.SWAMP -> 3
            TerrainType.TRAIL -> 2
        }
        return max(1, terrainBase + (hoursSpent / 3))
    }

    fun reduceFatigueWithRest(currentFatigue: Int, restHours: Int): Int {
        val recovered = max(1, restHours / 2)
        return max(0, currentFatigue - recovered)
    }

    fun encounterRoll(terrain: TerrainType, random: Random = Random.Default): Boolean {
        return random.nextFloat() < terrain.encounterChance
    }

    fun encounterForTerrain(terrain: TerrainType, random: Random = Random.Default): String? {
        val options = when (terrain) {
            TerrainType.ROAD -> listOf("combat_bandits", "combat_alley_thieves")
            TerrainType.FOREST -> listOf("combat_wolves", "combat_bandits", "combat_undead")
            TerrainType.MOUNTAIN -> listOf("combat_raubritter_scouts", "combat_undead", "combat_dragon")
            TerrainType.RIVER -> listOf("combat_bandits", "combat_wolves")
            TerrainType.SWAMP -> listOf("combat_undead", "combat_cultists")
            TerrainType.TRAIL -> listOf("combat_bandits", "combat_wolves")
        }
        return if (options.isEmpty()) null else options[random.nextInt(options.size)]
    }
}
