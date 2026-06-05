package com.grimreich.world

import com.grimreich.core.EnemyType
import kotlin.random.Random

enum class LocationType {
    RUINS,
    RAUBRITTER_CASTLE,
    MONASTERY,
    DUNGEON,
    HAMLET
}

data class ProceduralLocation(
    val id: String,
    val name: String,
    val type: LocationType,
    val region: String,
    val nearestCityId: String,
    val enemies: List<EnemyType>,
    val rewardGold: Int,
    val linkedEventId: String? = null
)

/**
 * TODO[procgen] Expand templates with relics, named NPCs and quest hooks.
 * world/ keeps deterministic content generators and static content registries.
 */
object ProceduralLocationGenerator {
    private val defaultCount = 5

    fun generate(seed: Int, count: Int = defaultCount): List<ProceduralLocation> {
        CityCatalogue.seedSprint1()
        val random = Random(seed)
        val cities = CityCatalogue.all().sortedBy { it.id }

        return (0 until count).map { index ->
            val city = cities[random.nextInt(cities.size)]
            val type = LocationType.entries[random.nextInt(LocationType.entries.size)]
            createLocation(index = index, type = type, city = city, random = random)
        }
    }

    private fun createLocation(
        index: Int,
        type: LocationType,
        city: CityData,
        random: Random
    ): ProceduralLocation {
        return when (type) {
            LocationType.RUINS -> ProceduralLocation(
                id = "ruins_${city.id}_$index",
                name = "Ruiny pod ${city.name}",
                type = type,
                region = city.region,
                nearestCityId = city.id,
                enemies = listOf(EnemyType.BANDIT, EnemyType.SKELETON),
                rewardGold = 40 + random.nextInt(70),
                linkedEventId = "ruins_discovery"
            )
            LocationType.RAUBRITTER_CASTLE -> ProceduralLocation(
                id = "castle_${city.id}_$index",
                name = "Zamek raubrittera koło ${city.name}",
                type = type,
                region = city.region,
                nearestCityId = city.id,
                enemies = listOf(EnemyType.RAUBRITTER_SOLDIER, EnemyType.RAUBRITTER_KNIGHT),
                rewardGold = 100 + random.nextInt(180),
                linkedEventId = "raubritter_scout_report"
            )
            LocationType.MONASTERY -> ProceduralLocation(
                id = "monastery_${city.id}_$index",
                name = "Klasztor niedaleko ${city.name}",
                type = type,
                region = city.region,
                nearestCityId = city.id,
                enemies = emptyList(),
                rewardGold = 20 + random.nextInt(40),
                linkedEventId = "pilgrim_request"
            )
            LocationType.DUNGEON -> ProceduralLocation(
                id = "dungeon_${city.id}_$index",
                name = "Lochy pod ${city.name}",
                type = type,
                region = city.region,
                nearestCityId = city.id,
                enemies = listOf(EnemyType.SKELETON_WARRIOR, EnemyType.GHOST, EnemyType.CULTIST),
                rewardGold = 80 + random.nextInt(140),
                linkedEventId = "dungeon_whispers"
            )
            LocationType.HAMLET -> ProceduralLocation(
                id = "hamlet_${city.id}_$index",
                name = "Osada przy ${city.name}",
                type = type,
                region = city.region,
                nearestCityId = city.id,
                enemies = listOf(EnemyType.WOLF),
                rewardGold = 15 + random.nextInt(35),
                linkedEventId = "hamlet_trouble"
            )
        }
    }
}
