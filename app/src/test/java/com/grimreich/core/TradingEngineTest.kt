package com.grimreich.core

import com.grimreich.grimreich.v1.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TradingEngineTest {

    private val mockCalculator = object : EconomyCalculator {
        override fun priceInCity(cityId: String, basePrice: Int): Int = basePrice
        override fun calculateSellPrice(cityId: String, item: Item): Int {
            val price = (item.value * 0.6).toInt()
            return if (price < 1 && item.value > 0) 1 else price
        }
    }

    private fun makeItem(instanceId: String = "x", name: String = "Test", baseValue: Int = 1): Item {
        return Item(
            instanceId = instanceId,
            templateId = "test_item",
            name = name,
            value = baseValue,
            type = "trade_good",
            weight = 1.0,
            rarity = "normal",
            lore = "",
            effects = emptyMap()
        )
    }

    init {
        TradingEngine.initialize(mockCalculator)
    }

    @Test
    fun quoteBuy_shouldUseSafeQty() {
        val quote1 = TradingEngine.quoteBuy("wybrzeze_polnocne", TradeGoodType.SALT, 0)
        val quote2 = TradingEngine.quoteBuy("wybrzeze_polnocne", TradeGoodType.SALT, 1)
        assertEquals("Quote for 0 and 1 should be same (clamped to 1)", quote2, quote1)
    }

    @Test
    fun sellQuote_shouldBeAtLeastOne() {
        val item = makeItem()
        assertTrue("Sell quote should be at least 1", TradingEngine.quoteSell("city", item) >= 1)
    }

    @Test
    fun buyGood_shouldDecreaseGoldAndAddInventory() {
        val state = GameState().apply { 
            gold = 10_000 
            world.locationId = "wybrzeze_polnocne"
        }

        val beforeGold = state.gold
        val result = TradingEngine.buyGood(state, "wybrzeze_polnocne", TradeGoodType.SALT, 2)

        assertTrue(result.contains("Kupiono"))
        assertTrue("Gold should decrease", state.gold < beforeGold)
        assertTrue("Inventory size should increase", state.inventory.size >= 2)
    }

    @Test
    fun sellItem_shouldIncreaseGoldAndRemoveInventory() {
        val item = makeItem(instanceId = "trade_test", name = "Towar", baseValue = 100)
        val state = GameState().apply {
            gold = 0
            inventory.add(item)
        }

        val result = TradingEngine.sellItem(state, "city", item.instanceId)

        assertTrue(result.contains("Sprzedano"))
        // Base value 100 * multiplier 0.6 = 60
        assertEquals("Gold should be 60", 60, state.gold)
        assertTrue("Item should be removed from inventory", state.inventory.none { it.instanceId == item.instanceId })
    }
}
