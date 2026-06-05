package com.grimreich.systems

import com.grimreich.world.CityCatalogue

/**
 * Liczy ceny lokalne na podstawie mnożnika miasta.
 */
object EconomySystem {
    fun priceInCity(cityId: String, basePrice: Int): Int {
        val city = CityCatalogue.get(cityId) ?: return basePrice
        return (basePrice * city.priceModifier).toInt().coerceAtLeast(1)
    }
}