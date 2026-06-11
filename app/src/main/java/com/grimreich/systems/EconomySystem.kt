package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GrimConstants
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue

object EconomySystem {
    
    fun priceInCity(cityId: String, basePrice: Int): Int {
        // Canonical regional logic: price depends on CityCatalogue priceModifier
        val city = CityCatalogue.get(cityId)
        val regionalModifier = city?.priceModifier ?: 1.0f
        
        // Reputation integration
        val reputationModifier = ReputationSystem.priceModifier(cityId)
        
        val finalPrice = (basePrice * regionalModifier * reputationModifier).toInt()
        return if (finalPrice < 1 && basePrice > 0) 1 else finalPrice
    }
    
    fun sellItem(item: Item): Int {
        val sellPrice = (item.value * GrimConstants.Economy.SELL_PRICE_MULTIPLIER).toInt()
        GameRepository.state.gold += sellPrice
        GameRepository.state.inventory.remove(item)
        return sellPrice
    }
}
