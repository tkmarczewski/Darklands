package com.grimreich.world

import kotlin.random.Random

enum class LocationType {
    ZGLISZCZA,        // dawne RUINS
    MROCZNY_ZAKON,    // dawne MONASTERY
    TWIERDZA_CIENIA,  // dawne RAUBRITTER_CASTLE
    KATAKUMBY_MROKU,  // dawne DUNGEON
    KAPLICZKA_KRWI    // dawne SHRINE
}

data class ProceduralLocation(
    val id: String,
    val name: String,
    val type: LocationType,
    val nearestCityId: String,
    val rewardGold: Int
)

object ProceduralLocationGenerator {
    
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
        val prefixes = listOf("Przeklęte", "Zimne", "Krwawe", "Cieniste", "Milczące")
        val nouns = when(type) {
            LocationType.ZGLISZCZA -> listOf("Zgliszcza", "Kurhany", "Pustkowia")
            LocationType.MROCZNY_ZAKON -> listOf("Opactwo", "Klasztorzysko", "Sanktuarium")
            LocationType.TWIERDZA_CIENIA -> listOf("Zamczysko", "Fortecę", "Bastion")
            LocationType.KATAKUMBY_MROKU -> listOf("Lochy", "Kryptę", "Czeluść")
            LocationType.KAPLICZKA_KRWI -> listOf("Kapliczkę", "Ołtarz", "Miejsce Ofiar")
        }
        return "${prefixes.random(random)} ${nouns.random(random)}"
    }
}
