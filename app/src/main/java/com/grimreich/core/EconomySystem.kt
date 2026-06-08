package com.grimreich.core

// ==================== TRADE GOOD TYPES ====================

enum class TradeGoodType {
    // Basics
    GRAIN, SALT, IRON_ORE, TIMBER, WOOL,
    // Crafted
    CLOTH, WEAPONS, TOOLS, LEATHER_GOODS,
    // Luxuries
    SPICES, WINE, SILK, JEWELRY
}

// ==================== TRADE GOOD MODEL ====================

data class TradeGood(
    val type: TradeGoodType,
    val name: String,
    val basePrice: Int,
    val weight: Int, // in dkg
    val description: String
)

// ==================== TRADE GOOD CATALOG ====================

object TradeGoodCatalog {
    val goods = listOf(
        TradeGood(TradeGoodType.GRAIN, "Zboże", 10, 100, "Podstawowe pożywienie."),
        TradeGood(TradeGoodType.SALT, "Sól", 25, 50, "Białe złoto północy."),
        TradeGood(TradeGoodType.IRON_ORE, "Ruda żelaza", 40, 200, "Surowiec do wyrobu broni."),
        TradeGood(TradeGoodType.TIMBER, "Drewno", 15, 250, "Materiał budowlany."),
        TradeGood(TradeGoodType.WOOL, "Wełna", 20, 80, "Surowiec na ubrania."),
        TradeGood(TradeGoodType.CLOTH, "Sukno", 50, 60, "Wytworny materiał."),
        TradeGood(TradeGoodType.WEAPONS, "Broń", 150, 150, "Miecze i topory."),
        TradeGood(TradeGoodType.TOOLS, "Narzędzia", 80, 120, "Niezbędne w rzemiośle."),
        TradeGood(TradeGoodType.LEATHER_GOODS, "Wyroby skórzane", 60, 90, "Buty i pasy."),
        TradeGood(TradeGoodType.SPICES, "Przyprawy", 300, 10, "Egzotyczne aromaty."),
        TradeGood(TradeGoodType.WINE, "Wino", 120, 100, "Trunek dla szlachty."),
        TradeGood(TradeGoodType.SILK, "Jedwab", 500, 20, "Najdroższy materiał."),
        TradeGood(TradeGoodType.JEWELRY, "Biżuteria", 800, 5, "Złoto i klejnoty.")
    )

    fun findByType(type: TradeGoodType) = goods.firstOrNull { it.type == type }
}

// ==================== CITY MARKET LOGIC ====================

data class CityMarket(
    val cityId: String,
    val priceModifiers: Map<TradeGoodType, Int> // percent of base price, e.g. 120 means 120%
) {
    fun getPrice(type: TradeGoodType): Int {
        val base = TradeGoodCatalog.findByType(type)?.basePrice ?: 0
        val mod = priceModifiers[type] ?: 100
        return (base * mod) / 100
    }
}

object CityMarketCatalog {
    val markets: Map<String, CityMarket> = mapOf(
        "wybrzeze_polnocne" to CityMarket(
            cityId = "wybrzeze_polnocne",
            priceModifiers = mapOf(
                TradeGoodType.SALT to 70, // Plenty of salt from the sea
                TradeGoodType.GRAIN to 130, // Hard to grow in the mist
                TradeGoodType.SPICES to 150
            )
        ),
        "serce_krainy" to CityMarket(
            cityId = "serce_krainy",
            priceModifiers = mapOf(
                TradeGoodType.WINE to 80,
                TradeGoodType.SILK to 90,
                TradeGoodType.IRON_ORE to 120
            )
        ),
        "rowniny_koronne" to CityMarket(
            cityId = "rowniny_koronne",
            priceModifiers = mapOf(
                TradeGoodType.GRAIN to 70, // Fertile lands
                TradeGoodType.WOOL to 80,
                TradeGoodType.WEAPONS to 130
            )
        ),
        "pogranicze_stepowe" to CityMarket(
            cityId = "pogranicze_stepowe",
            priceModifiers = mapOf(
                TradeGoodType.WEAPONS to 90,
                TradeGoodType.LEATHER_GOODS to 70,
                TradeGoodType.WINE to 140
            )
        ),
        "poludniowe_ruiny" to CityMarket(
            cityId = "poludniowe_ruiny",
            priceModifiers = mapOf(
                TradeGoodType.TOOLS to 120,
                TradeGoodType.CLOTH to 110,
                TradeGoodType.SALT to 130
            )
        ),
        "gory_poludniowe" to CityMarket(
            cityId = "gory_poludniowe",
            priceModifiers = mapOf(
                TradeGoodType.IRON_ORE to 70, // Mining region
                TradeGoodType.JEWELRY to 80,
                TradeGoodType.GRAIN to 160
            )
        ),
        "ziemie_dzikie" to CityMarket(
            cityId = "ziemie_dzikie",
            priceModifiers = mapOf(
                TradeGoodType.TIMBER to 50, // Massive forests
                TradeGoodType.LEATHER_GOODS to 80,
                TradeGoodType.SILK to 200
            )
        )
    )

    fun getMarket(cityId: String) = markets[cityId]
}
