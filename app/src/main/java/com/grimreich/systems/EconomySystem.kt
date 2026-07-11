package com.grimreich.systems

import com.grimreich.core.GrimConstants
import com.grimreich.core.FactionReputationSystem
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EconomySystem @Inject constructor(
    private val factionReputationSystem: FactionReputationSystem,
    private val cityCatalogue: CityCatalogue
) : com.grimreich.core.EconomyCalculator {
    override fun priceInCity(cityId: String, basePrice: Int): Int {
        val city = cityCatalogue.get(cityId)
        val regionalModifier = city?.priceModifier ?: 1.0f
        val rep = factionReputationSystem.getReputation("MERCHANTS")
        val reputationModifier = FactionReputationSystem.buyModifier(rep)
        val finalPrice = (basePrice * regionalModifier * reputationModifier).toInt()
        return if (finalPrice < 1 && basePrice > 0) 1 else finalPrice
    }

    override fun calculateSellPrice(item: Item): Int {
        return (item.value * GrimConstants.Economy.SELL_PRICE_MULTIPLIER).toInt()
    }
}
