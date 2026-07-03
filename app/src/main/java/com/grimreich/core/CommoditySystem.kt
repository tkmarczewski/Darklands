package com.grimreich.core

import com.grimreich.grimreich.v1.Item

enum class TradeGoodType {
    GRAIN,
    SALT,
    IRON_ORE,
    TIMBER,
    WOOL,
    CLOTH,
    WEAPONS,
    TOOLS,
    LEATHER_GOODS,
    SPICES,
    WINE,
    SILK,
    JEWELRY
}

data class TradeGood(
    val type: TradeGoodType,
    val name: String,
    val basePrice: Int,
    val weight: Int,
    val description: String
)

object TradeGoodCatalog {
    val goods = listOf(
        TradeGood(TradeGoodType.GRAIN, "Zboże", 5, 2, "Podstawowa żywność."),
        TradeGood(TradeGoodType.SALT, "Sól", 10, 1, "Białe złoto północy."),
        TradeGood(TradeGoodType.IRON_ORE, "Ruda Żelaza", 15, 3, "Surowiec dla kowali."),
        TradeGood(TradeGoodType.TIMBER, "Drewno", 8, 4, "Budulec i opał."),
        TradeGood(TradeGoodType.WOOL, "Wełna", 12, 1, "Ciepły materiał."),
        TradeGood(TradeGoodType.CLOTH, "Płótno", 20, 1, "Gotowa tkanina."),
        TradeGood(TradeGoodType.WEAPONS, "Broń", 50, 2, "Żelazo gotowe do walki."),
        TradeGood(TradeGoodType.TOOLS, "Narzędzia", 30, 2, "Niezbędne w rzemiośle."),
        TradeGood(TradeGoodType.LEATHER_GOODS, "Wyroby Skórzane", 25, 1, "Trwałe i lekkie."),
        TradeGood(TradeGoodType.SPICES, "Przyprawy", 100, 1, "Luksus z dalekich krain."),
        TradeGood(TradeGoodType.WINE, "Wino", 40, 2, "Napój bogaczy i mnichów."),
        TradeGood(TradeGoodType.SILK, "Jedwab", 150, 1, "Niezwykle rzadka tkanina."),
        TradeGood(TradeGoodType.JEWELRY, "Biżuteria", 300, 1, "Oznaka statusu.")
    )

    fun findByType(type: TradeGoodType) = goods.find { it.type == type }
}

data class CityMarket(
    val cityId: String,
    val priceModifiers: Map<TradeGoodType, Int>
) {
    fun getPrice(type: TradeGoodType): Int {
        val base = TradeGoodCatalog.findByType(type)?.basePrice ?: 10
        val mod = priceModifiers[type] ?: 0
        return (base + mod).coerceAtLeast(1)
    }
}

object CityMarketCatalog {
    private val markets = mapOf(
        "wybrzeze_polnocne" to CityMarket("wybrzeze_polnocne", mapOf(
            TradeGoodType.SALT to -3,
            TradeGoodType.SPICES to 20
        )),
        "twierdza_zelazna" to CityMarket("twierdza_zelazna", mapOf(
            TradeGoodType.IRON_ORE to -5,
            TradeGoodType.WEAPONS to -10,
            TradeGoodType.GRAIN to 5
        )),
        "port_mglisty" to CityMarket("port_mglisty", mapOf(
            TradeGoodType.SILK to -30,
            TradeGoodType.WINE to -5,
            TradeGoodType.TOOLS to 10
        )),
        "opactwo_ciszy" to CityMarket("opactwo_ciszy", mapOf(
            TradeGoodType.WINE to 15,
            TradeGoodType.CLOTH to -5
        ))
    )

    fun getMarket(cityId: String): CityMarket? = markets[cityId]
}

object TradingEngine {
    fun buyGood(state: GameState, cityId: String, type: TradeGoodType, qty: Int = 1): String {
        val market = CityMarketCatalog.getMarket(cityId) ?: return "Brak rynku w tej lokacji."
        val good = TradeGoodCatalog.findByType(type) ?: return "Nieznany towar."
        val safeQty = qty.coerceAtLeast(1)
        val totalCost = market.getPrice(type) * safeQty
        if (state.gold < totalCost) return "Brak złota. Potrzeba $totalCost G."
        state.gold -= totalCost
        repeat(safeQty) {
            state.inventory.add(Item(
                id = "trade_${type.name.lowercase()}", 
                name = good.name, 
                value = market.getPrice(type),
                type = "trade_good",
                weight = good.weight.toDouble(),
                rarity = "normal",
                lore = good.description,
                effects = emptyMap()
            ))
        }
        return "Kupiono ${good.name} x$safeQty za $totalCost G."
    }

    fun quoteBuy(cityId: String, type: TradeGoodType, qty: Int = 1): Int {
        val market = CityMarketCatalog.getMarket(cityId) ?: return 0
        val safeQty = qty.coerceAtLeast(1)
        return market.getPrice(type) * safeQty
    }

    fun quoteSell(item: Item): Int =
        (item.value * GrimConstants.Economy.SELL_PRICE_MULTIPLIER).toInt().coerceAtLeast(1)

    fun sellItem(state: GameState, itemId: String): String {
        val item = state.inventory.find { it.id == itemId } ?: return "Brak przedmiotu."
        val sellPrice = quoteSell(item)
        state.inventory.remove(item)
        state.gold += sellPrice
        return "Sprzedano ${item.name} za $sellPrice G."
    }

    fun buyWithFactionModifier(
        state: GameState,
        cityId: String,
        type: TradeGoodType,
        factionId: String,
        qty: Int = 1
    ): String {
        val market = CityMarketCatalog.getMarket(cityId) ?: return "Brak rynku w tej lokacji."
        val safeQty = qty.coerceAtLeast(1)
        val rep = state.reputation.globalFactions[factionId] ?: 0
        val modifier = FactionReputationSystem.buyModifier(rep)
        val unitPrice = (market.getPrice(type) * modifier).toInt().coerceAtLeast(1)
        val total = unitPrice * safeQty
        if (state.gold < total) return "Brak złota. Potrzeba $total G."
        val good = TradeGoodCatalog.findByType(type) ?: return "Błąd towaru."
        state.gold -= total
        repeat(safeQty) {
            state.inventory.add(Item(
                id = "trade_${type.name.lowercase()}_${state.world.day}", 
                name = good.name, 
                value = unitPrice,
                type = "trade_good",
                weight = good.weight.toDouble(),
                rarity = "normal",
                lore = good.description,
                effects = emptyMap()
            ))
        }
        return "Kupiono ${good.name} x$safeQty za $total G (zniżka frakcyjna)."
    }

    fun quoteBuyWithFactionModifier(cityId: String, type: TradeGoodType, factionId: String, qty: Int = 1, reputationValue: Int): Int {
        val market = CityMarketCatalog.getMarket(cityId) ?: return 0
        val safeQty = qty.coerceAtLeast(1)
        val modifier = FactionReputationSystem.buyModifier(reputationValue)
        return ((market.getPrice(type) * modifier).toInt().coerceAtLeast(1)) * safeQty
    }

    fun sellWithFactionModifier(
        state: GameState,
        itemId: String,
        factionId: String
    ): String {
        val item = state.inventory.find { it.id == itemId } ?: return "Brak przedmiotu."
        val rep = state.reputation.globalFactions[factionId] ?: 0
        val modifier = FactionReputationSystem.sellModifier(rep)
        val base = (item.value * GrimConstants.Economy.SELL_PRICE_MULTIPLIER).toInt().coerceAtLeast(1)
        val finalPrice = (base * modifier).toInt().coerceAtLeast(1)
        state.inventory.remove(item)
        state.gold += finalPrice
        return "Sprzedano ${item.name} za $finalPrice G."
    }
}
