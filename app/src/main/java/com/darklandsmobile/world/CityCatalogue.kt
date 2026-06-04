package com.darklandsmobile.world

/**
 * TODO conventions for world content:
 * - TODO[city] add new city pack / local content
 * - TODO[event] attach richer city events and NPC hooks
 * - TODO[map] tune connectivity and region routes
 *
 * world/ contains canonical static world data used by core and systems layers.
 */
data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val type: String,
    val population: Int,
    val priceModifier: Float,
    val events: MutableList<String> = mutableListOf()
)

/**
 * Central city catalogue for map, economy and event systems.
 *
 * Sprint coverage:
 * - Stage 0: canonical data format for CityData
 * - Stage 1 / Sprint 4-5: 11 connected cities (Magdeburg + 10 target cities)
 */
object CityCatalogue {
    const val startingCityId = "magdeburg"
    private val cities = linkedMapOf<String, CityData>()

    fun register(city: CityData) {
        cities[city.id] = city
    }

    fun get(id: String): CityData? = cities[id]

    fun all(): List<CityData> = cities.values.toList()

    fun clear() = cities.clear()

    /**
     * Seeds Stage 1 world cities.
     * The method name is preserved for compatibility with existing callers.
     */
    fun seedSprint1() {
        if (cities.isNotEmpty()) return

        register(
            CityData(
                id = "magdeburg",
                name = "Magdeburg",
                region = "central",
                type = "city",
                population = 32000,
                priceModifier = 1.00f,
                events = mutableListOf("market_day", "church_rumor")
            )
        )
        register(
            CityData(
                id = "koln",
                name = "Köln",
                region = "west",
                type = "metropolis",
                population = 50000,
                priceModifier = 0.95f,
                events = mutableListOf("trade_fair", "guild_reaction")
            )
        )
        register(
            CityData(
                id = "nurnberg",
                name = "Nürnberg",
                region = "south",
                type = "city",
                population = 42000,
                priceModifier = 1.00f,
                events = mutableListOf("craftsman_news", "church_reaction")
            )
        )
        register(
            CityData(
                id = "frankfurt",
                name = "Frankfurt",
                region = "central_west",
                type = "city",
                population = 46000,
                priceModifier = 1.10f,
                events = mutableListOf("bank_notice", "merchant_reaction")
            )
        )
        register(
            CityData(
                id = "praha",
                name = "Praha",
                region = "east_south",
                type = "city",
                population = 38000,
                priceModifier = 1.05f,
                events = mutableListOf("night_watch", "church_reaction")
            )
        )
        register(
            CityData(
                id = "lubeck",
                name = "Lübeck",
                region = "north",
                type = "port",
                population = 30000,
                priceModifier = 0.90f,
                events = mutableListOf("harbor_news", "merchant_reaction")
            )
        )
        register(
            CityData(
                id = "hamburg",
                name = "Hamburg",
                region = "north_west",
                type = "port",
                population = 34000,
                priceModifier = 0.92f,
                events = mutableListOf("dockside_brawl", "benon_pilgrims")
            )
        )
        register(
            CityData(
                id = "wien",
                name = "Wien",
                region = "south_east",
                type = "capital",
                population = 41000,
                priceModifier = 1.08f,
                events = mutableListOf("court_petition", "cathedral_procession")
            )
        )
        register(
            CityData(
                id = "breslau",
                name = "Breslau",
                region = "east",
                type = "city",
                population = 29000,
                priceModifier = 0.98f,
                events = mutableListOf("river_tolls", "guild_reaction")
            )
        )
        register(
            CityData(
                id = "augsburg",
                name = "Augsburg",
                region = "south_west",
                type = "city",
                population = 36000,
                priceModifier = 1.03f,
                events = mutableListOf("banker_patron", "church_reaction")
            )
        )
        register(
            CityData(
                id = "strasbourg",
                name = "Strasbourg",
                region = "far_west",
                type = "border_city",
                population = 33000,
                priceModifier = 1.01f,
                events = mutableListOf("bridge_tax", "market_day")
            )
        )
    }
}
