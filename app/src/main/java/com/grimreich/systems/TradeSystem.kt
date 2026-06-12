package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.TradeGoodCatalog
import com.grimreich.core.TradeGoodType
import com.grimreich.core.CityMarketCatalog
import com.grimreich.grimreich.v1.Item

object TradeSystem {
    
    fun buyGood(cityId: String, type: TradeGoodType): String {
        val g = GameRepository.state
        val market = CityMarketCatalog.getMarket(cityId) ?: return "Brak handlu w tym miejscu."
        val price = market.getPrice(type)
        
        if (g.gold < price) return "Brak złota! (Potrzeba $price)"
        
        g.gold -= price
        val good = TradeGoodCatalog.findByType(type)!!
        val item = Item(
            id = "trade_${type.name.lowercase()}",
            name = good.name,
            type = "trade_good",
            value = good.basePrice,
            weight = good.weight.toDouble() / 10.0
        )
        g.inventory.add(item)
        return "Kupiono ${good.name} za $price złota."
    }

    fun sellItem(item: Item, cityId: String): String {
        val g = GameRepository.state
        if (!g.inventory.contains(item)) return "Nie masz tego przedmiotu."
        
        val baseValue = item.value
        val sellPrice = (baseValue * com.grimreich.core.GrimConstants.Economy.SELL_PRICE_MULTIPLIER).toInt()
        
        g.gold += sellPrice
        g.inventory.remove(item)
        return "Sprzedano ${item.name} za $sellPrice złota."
    }
}
