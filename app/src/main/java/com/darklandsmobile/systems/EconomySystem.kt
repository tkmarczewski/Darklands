package com.darklandsmobile.systems

import com.darklandsmobile.world.CityCatalogue

object EconomySystem {
    fun priceInCity(cityId: String, basePrice: Int): Int {
        val city = CityCatalogue.get(cityId) ?: return basePrice
        return (basePrice * city.priceModifier).toInt().coerceAtLeast(1)
    }
}