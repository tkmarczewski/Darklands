package com.grimreich.world

import kotlin.random.Random

enum class LocationType {
    RUINS, MONASTERY, RAUBRITTER_CASTLE, DUNGEON, SHRINE
}

data class ProceduralLocation(
    val id: String,
    val name: String,
    val type: LocationType,
    val nearestCityId: String,
    val rewardGold: Int
)

object ProceduralLocationGenerator {
    
    // Seeded generation of locations connected to valid CityCatalogue IDs
    fun generate(seed: Int, count: Int): List<ProceduralLocation> {
        val random = Random(seed)
        val cities = listOf("wybrzeze_polnocne", "serce_krainy", "rowniny_koronne", "pogranicze_stepowe", "poludniowe_ruiny", "gory_poludniowe", "ziemie_dzikie")
        
        return (0 until count).map { i ->
            val type = LocationType.entries.random(random)
            val city = cities.random(random)
            val name = generateName(type, random)
            ProceduralLocation(
                id = "loc_${city}_$i",
                name = name,
                type = type,
                nearestCityId = city,
                rewardGold = random.nextInt(50, 300)
            )
        }
    }

    private fun generateName(type: LocationType, random: Random): String {
        val prefixes = listOf("Mroczne", "Zapomniane", "Przeklęte", "Stare", "Krwawe")
        val ruins = listOf("Zgliszcza", "Kurhany", "Opactwo", "Lochy", "Mury")
        return "${prefixes.random(random)} ${ruins.random(random)}"
    }
}
