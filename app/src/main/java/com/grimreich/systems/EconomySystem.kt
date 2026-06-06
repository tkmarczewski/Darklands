package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item

object EconomySystem {
    
    fun priceInCity(cityId: String, basePrice: Int): Int {
        val g = GameRepository.state
        // Uproszczona logika regionalna: w Magdeburgu drożej, w innych taniej
        return if (cityId == "magdeburg") (basePrice * 1.2).toInt() else basePrice
    }
    
    fun sellItem(item: Item): Int {
        val sellPrice = (item.value * 0.4).toInt()
        GameRepository.state.gold += sellPrice
        GameRepository.state.inventory.remove(item)
        return sellPrice
    }
}
