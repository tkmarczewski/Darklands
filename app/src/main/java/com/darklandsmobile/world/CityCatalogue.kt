package com.darklandsmobile.world

/**
 * Dane miasta: identyfikator, region, typ, populacja i lokalny mnożnik cen.
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
 * Centralny katalog miast dla mapy, ekonomii i eventów.
 */
object CityCatalogue {
    private val cities = mutableMapOf<String, CityData>()

    fun register(city: CityData) { cities[city.id] = city }
    fun get(id: String): CityData? = cities[id]
    fun all(): List<CityData> = cities.values.toList()

    /**
     * Minimalny zestaw miast do sprintu 6.
     */
    fun seedSprint1() {
        if (cities.isNotEmpty()) return
        register(CityData("magdeburg", "Magdeburg", "central", "city", 32000, 1.0f, mutableListOf("market_day", "church_rumor")))
        register(CityData("koln", "Köln", "west", "city", 50000, 0.95f, mutableListOf("trade_fair", "guild_reaction")))
        register(CityData("nurnberg", "Nürnberg", "south", "city", 42000, 1.0f, mutableListOf("craftsman_news", "church_reaction")))
        register(CityData("frankfurt", "Frankfurt", "central_west", "city", 46000, 1.1f, mutableListOf("bank_notice", "merchant_reaction")))
        register(CityData("praha", "Praha", "east_south", "city", 38000, 1.05f, mutableListOf("night_watch", "church_reaction")))
        register(CityData("lubeck", "Lübeck", "north", "port", 30000, 0.9f, mutableListOf("harbor_news", "merchant_reaction")))
    }
}