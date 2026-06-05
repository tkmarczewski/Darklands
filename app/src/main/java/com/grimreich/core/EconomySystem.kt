package com.grimreich.core

/**
 * System ekonomiczny — towary, handel, ceny w miastach.
 * Każde miasto może mieć inne ceny towarów w zależności od produkcji lokalnej.
 */

// ==================== TOWAR ====================
enum class TradeGoodType {
    // Surowce
    GRAIN, SALT, IRON_ORE, TIMBER, WOOL,
    // Rzemiosło
    CLOTH, WEAPONS, TOOLS, LEATHER_GOODS,
    // Luksus
    SPICES, WINE, SILK, JEWELRY
}

data class TradeGood(
    val type: TradeGoodType,
    val name: String,
    val basePrice: Int,          // floreny za jednostkę
    val weight: Int = 1,
    val description: String = ""
)

object TradeGoodCatalog {
    val goods = listOf(
        // SUROWCE
        TradeGood(
            type = TradeGoodType.GRAIN,
            name = "Zboże",
            basePrice = 5,
            weight = 2,
            description = "Podstawowe pożywienie."
        ),
        TradeGood(
            type = TradeGoodType.SALT,
            name = "Sól",
            basePrice = 10,
            weight = 1,
            description = "Do konserwacji i przypraw."
        ),
        TradeGood(
            type = TradeGoodType.IRON_ORE,
            name = "Ruda żelaza",
            basePrice = 15,
            weight = 3,
            description = "Surowiec do produkcji broni."
        ),
        TradeGood(
            type = TradeGoodType.TIMBER,
            name = "Drewno",
            basePrice = 8,
            weight = 5,
            description = "Materiał budowlany."
        ),
        TradeGood(
            type = TradeGoodType.WOOL,
            name = "Wełna",
            basePrice = 12,
            weight = 1,
            description = "Do produkcji sukna."
        ),
        // RZEMIOSŁO
        TradeGood(
            type = TradeGoodType.CLOTH,
            name = "Sukno",
            basePrice = 25,
            weight = 1,
            description = "Materiał na odzież."
        ),
        TradeGood(
            type = TradeGoodType.WEAPONS,
            name = "Broń",
            basePrice = 50,
            weight = 2,
            description = "Miecze, topory, lance."
        ),
        TradeGood(
            type = TradeGoodType.TOOLS,
            name = "Narzędzia",
            basePrice = 20,
            weight = 2,
            description = "Młoty, piły, lemiesze."
        ),
        TradeGood(
            type = TradeGoodType.LEATHER_GOODS,
            name = "Wyroby skórzane",
            basePrice = 30,
            weight = 1,
            description = "Obuwie, pasy, sakwy."
        ),
        // LUKSUS
        TradeGood(
            type = TradeGoodType.SPICES,
            name = "Przyprawy",
            basePrice = 80,
            weight = 1,
            description = "Pieprz, goździki, cynamon."
        ),
        TradeGood(
            type = TradeGoodType.WINE,
            name = "Wino",
            basePrice = 40,
            weight = 2,
            description = "Reńskie i węgierskie."
        ),
        TradeGood(
            type = TradeGoodType.SILK,
            name = "Jedwab",
            basePrice = 120,
            weight = 1,
            description = "Luksusowa tkanina."
        ),
        TradeGood(
            type = TradeGoodType.JEWELRY,
            name = "Biżuteria",
            basePrice = 200,
            weight = 1,
            description = "Złoto i kamienie szlachetne."
        )
    )

    fun findByType(type: TradeGoodType): TradeGood? = goods.firstOrNull { it.type == type }
}

// ==================== CENY W MIASTACH ====================
/**
 * Modyfikator ceny w danym mieście (100 = cena bazowa, 120 = +20%, 80 = -20%)
 */
data class CityPriceModifier(
    val goodType: TradeGoodType,
    val pricePercent: Int
)

data class CityMarket(
    val cityId: String,
    val priceModifiers: Map<TradeGoodType, Int>  // % modyfikator ceny
) {
    fun getPrice(goodType: TradeGoodType): Int {
        val good = TradeGoodCatalog.findByType(goodType) ?: return 0
        val modifier = priceModifiers[goodType] ?: 100
        return (good.basePrice * modifier) / 100
    }
}

// ==================== PRZYKŁADOWE RYNKI ====================
object CityMarketCatalog {
    val markets = mapOf(
        "augsburg" to CityMarket(
            cityId = "augsburg",
            priceModifiers = mapOf(
                TradeGoodType.WEAPONS to 90,      // tańsza broń
                TradeGoodType.SPICES to 110,
                TradeGoodType.JEWELRY to 120
            )
        ),
        "koln" to CityMarket(
            cityId = "koln",
            priceModifiers = mapOf(
                TradeGoodType.WINE to 80,          // tańsze wino
                TradeGoodType.CLOTH to 85,
                TradeGoodType.SPICES to 130        // drogie przyprawy
            )
        ),
        "nurnberg" to CityMarket(
            cityId = "nurnberg",
            priceModifiers = mapOf(
                TradeGoodType.TOOLS to 90,
                TradeGoodType.IRON_ORE to 110
            )
        ),
        "hamburg" to CityMarket(
            cityId = "hamburg",
            priceModifiers = mapOf(
                TradeGoodType.TIMBER to 75,        // port = tanie drewno
                TradeGoodType.SALT to 80,
                TradeGoodType.SPICES to 120
            )
        ),
        "wien" to CityMarket(
            cityId = "wien",
            priceModifiers = mapOf(
                TradeGoodType.WINE to 70,
                TradeGoodType.SILK to 90,
                TradeGoodType.JEWELRY to 85
            )
        )
    )

    fun getMarket(cityId: String): CityMarket? = markets[cityId]
}
