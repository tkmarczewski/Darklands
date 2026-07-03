package com.grimreich.systems

import com.grimreich.core.GrimConstants
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EconomySystem @Inject constructor(
    private val reputationSystem: ReputationSystem,
    private val cityCatalogue: CityCatalogue
) {
    fun priceInCity(cityId: String, basePrice: Int): Int {
        val city = cityCatalogue.get(cityId)
        val regionalModifier = city?.priceModifier ?: 1.0f
        val reputationModifier = reputationSystem.priceModifier(cityId)
        val finalPrice = (basePrice * regionalModifier * reputationModifier).toInt()
        return if (finalPrice < 1 && basePrice > 0) 1 else finalPrice
    }

    fun calculateSellPrice(item: Item): Int {
        return (item.value * GrimConstants.Economy.SELL_PRICE_MULTIPLIER).toInt()
    }
}
