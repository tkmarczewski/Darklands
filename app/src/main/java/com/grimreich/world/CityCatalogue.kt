package com.grimreich.world

/**
 * Central city and region catalogue for GrimReich.
 * Synchronized with GrimLoreCatalogues to ensure lore consistency.
 */
data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val phenomenon: String,
    val rulingFaction: String,
    val priceModifier: Float,
    val events: MutableList<String> = mutableListOf()
)

object CityCatalogue {
    const val startingCityId = "wybrzeze_polnocne"
    private val cities = linkedMapOf<String, CityData>()

    fun register(city: CityData) {
        cities[city.id] = city
    }

    fun get(id: String): CityData? = cities[id]

    fun all(): List<CityData> = cities.values.toList()

    fun clear() = cities.clear()

    /**
     * Seeds GrimReich lore-compliant locations.
     */
    fun seedSprint1() {
        if (cities.isNotEmpty()) return

        register(
            CityData(
                id = "wybrzeze_polnocne",
                name = "Wybrzeże Północne",
                region = "north",
                phenomenon = "Mgła",
                rulingFaction = "Zakon Świtu",
                priceModifier = 1.0f,
                events = mutableListOf("mist_vision", "lighthouse_call")
            )
        )
        register(
            CityData(
                id = "serce_krainy",
                name = "Serce Krainy",
                region = "central",
                phenomenon = "Odbicie",
                rulingFaction = "Trybunał",
                priceModifier = 1.2f,
                events = mutableListOf("mirror_judgment", "court_decree")
            )
        )
        register(
            CityData(
                id = "rowniny_koronne",
                name = "Równiny Koronne",
                region = "east",
                phenomenon = "Krew",
                rulingFaction = "Gildia",
                priceModifier = 0.9f,
                events = mutableListOf("blood_harvest", "guild_tax")
            )
        )
        register(
            CityData(
                id = "pogranicze_stepowe",
                name = "Pogranicze Stepowe",
                region = "west",
                phenomenon = "Rozdarcie",
                rulingFaction = "Bractwo Cienia",
                priceModifier = 1.1f,
                events = mutableListOf("rift_leak", "shadow_raid")
            )
        )
        register(
            CityData(
                id = "poludniowe_ruiny",
                name = "Południowe Ruiny",
                region = "south",
                phenomenon = "Pełnia",
                rulingFaction = "Zakon Świtu",
                priceModifier = 1.05f,
                events = mutableListOf("ash_rain", "hymn_echo")
            )
        )
        register(
            CityData(
                id = "gory_poludniowe",
                name = "Góry Południowe",
                region = "far_south",
                phenomenon = "Absolut",
                rulingFaction = "Trybunał",
                priceModifier = 1.3f,
                events = mutableListOf("absolute_silence", "summit_whisper")
            )
        )
        register(
            CityData(
                id = "ziemie_dzikie",
                name = "Ziemie Dzikie",
                region = "untamed",
                phenomenon = "Mgła",
                rulingFaction = "None",
                priceModifier = 0.85f,
                events = mutableListOf("wild_hunt", "primal_hunger")
            )
        )
    }
}
