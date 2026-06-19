package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val economySystem: EconomySystem
) {
    fun buyGood(cityId: String, item: Item): String {
        val state = gameRepository.currentState()
        val price = economySystem.priceInCity(cityId, item.value)
        
        if (state.gold < price) return "Brak złota!"
        
        state.gold -= price
        state.inventory.add(item)
        gameRepository.persistCurrentState()
        return "Kupiono ${item.name} za $price zł."
    }

    fun sellItem(item: Item, cityId: String): String {
        val sellPrice = economySystem.sellItem(item)
        return "Sprzedano ${item.name} za $sellPrice zł."
    }
}
