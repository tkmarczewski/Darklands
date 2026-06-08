package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.Item

object EconomySystem {
    
    fun priceInCity(cityId: String, basePrice: Int): Int {
        // Uproszczona logika regionalna: w Grimhold drożej (stolica), w innych standardowo
        val regionalModifier = if (cityId == "grimhold") 1.2f else 1.0f
        
        // Integracja z reputacją
        val reputationModifier = ReputationSystem.priceModifier(cityId)
        
        val finalPrice = (basePrice * regionalModifier * reputationModifier).toInt()
        return if (finalPrice < 1 && basePrice > 0) 1 else finalPrice
    }
    
    fun sellItem(item: Item): Int {
        val sellPrice = (item.value * 0.4).toInt()
        GameRepository.state.gold += sellPrice
        GameRepository.state.inventory.remove(item)
        return sellPrice
    }
}
