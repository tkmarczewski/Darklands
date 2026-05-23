package com.darklandsmobile.core

// ==================== CITY DISTRICT ====================

enum class DistrictType {
    MARKET, CHURCH, INN, BLACKSMITH, ALCHEMIST, GUILDHALL, CASTLE, SLUMS
}

data class CityDistrict(
    val id: String,
    val name: String,
    val type: DistrictType,
    val description: String = "",
    val isAvailable: Boolean = true
)

// ==================== WORLD MAP ====================

object WorldMap {
    data class Node(
        val id: String,
        val name: String,
        val region: String,
        val neighbors: List<String>,
        val districts: List<CityDistrict> = emptyList()
    )

    private val nodes = listOf(
        Node(
            "magdeburg", "Magdeburg", "town",
            listOf("road_north", "road_south"),
            listOf(
                CityDistrict("mag_market",    "Targ",            DistrictType.MARKET,      "Kupcy i handlarze"),
                CityDistrict("mag_church",    "Katedra",         DistrictType.CHURCH,      "Dom modlitwy"),
                CityDistrict("mag_inn",       "Gospoda",         DistrictType.INN,         "Odpoczynek i plotki"),
                CityDistrict("mag_smith",     "Kuznia",          DistrictType.BLACKSMITH,  "Bron i zbroja"),
                CityDistrict("mag_alch",      "Apteka",          DistrictType.ALCHEMIST,   "Sklep alchemiczny"),
                CityDistrict("mag_guild",     "Gildia",          DistrictType.GUILDHALL,   "Kontrakty i zlecenia"),
                CityDistrict("mag_castle",    "Zamek",           DistrictType.CASTLE,      "Siedziba wladzy"),
                CityDistrict("mag_slums",     "Przedmiescia",    DistrictType.SLUMS,       "Ciemne uliczki")
            )
        ),
        Node("road_north",  "Trakt Polnocny",  "road",   listOf("magdeburg", "forest_deep")),
        Node("road_south",  "Trakt Poludniowy","road",   listOf("magdeburg", "forest_dark")),
        Node("forest_deep", "Gleboki Las",     "forest", listOf("road_north")),
        Node("forest_dark", "Mroczny Las",     "forest", listOf("road_south"))
    )

    fun getNode(id: String) = nodes.firstOrNull { it.id == id }
    fun all() = nodes
    fun towns() = nodes.filter { it.region == "town" }
    fun getDistricts(nodeId: String) = getNode(nodeId)?.districts ?: emptyList()
}
