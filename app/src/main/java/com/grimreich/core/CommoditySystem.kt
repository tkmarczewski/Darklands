package com.grimreich.core

import com.grimreich.grimreich.v1.Item

interface EconomyCalculator {
    fun priceInCity(cityId: String, basePrice: Int): Int
    fun calculateSellPrice(cityId: String, item: Item): Int
}

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

object TradingEngine {
    private const val MAX_TRADE_QUANTITY = 99
    private var calculator: EconomyCalculator? = null
    
    fun initialize(calc: EconomyCalculator) { 
        calculator = calc 
    }

    private fun getCalculator(): EconomyCalculator = 
        calculator ?: error("TradingEngine must be initialized with an EconomyCalculator before use.")

    fun buyGood(state: GameState, cityId: String, type: TradeGoodType, qty: Int = 1): String {
        if (cityId != state.grimCurrentRegion) {
            return "Nie znajdujesz się w tej lokacji."
        }

        val good = TradeGoodCatalog.findByType(type) ?: return "Nieznany towar."
        val safeQty = qty.coerceIn(1, MAX_TRADE_QUANTITY)
        
        val unitPrice = getCalculator().priceInCity(cityId, good.basePrice)
        val totalCost = unitPrice.toLong() * safeQty.toLong()
        
        if (state.gold < totalCost) {
            return "Brak złota. Potrzeba $totalCost G."
        }
        
        state.gold -= totalCost.toInt()
        repeat(safeQty) {
            state.inventory.add(Item(
                instanceId = "trade_${type.name.lowercase()}_${java.util.UUID.randomUUID()}",
                templateId = "trade_${type.name.lowercase()}", 
                name = good.name, 
                value = good.basePrice,
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
        val good = TradeGoodCatalog.findByType(type) ?: return 0
        val safeQty = qty.coerceIn(1, MAX_TRADE_QUANTITY)
        val unitPrice = getCalculator().priceInCity(cityId, good.basePrice)
        return (unitPrice.toLong() * safeQty.toLong()).toInt()
    }

    fun quoteSell(cityId: String, item: Item): Int =
        getCalculator().calculateSellPrice(cityId, item)

    fun sellItem(state: GameState, cityId: String, itemId: String): String {
        val item = state.inventory.find { it.instanceId == itemId } ?: return "Brak przedmiotu."
        val sellPrice = quoteSell(cityId, item)
        state.inventory.remove(item)
        state.gold += sellPrice
        return "Sprzedano ${item.name} za $sellPrice G."
    }
}
