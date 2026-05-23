package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository

object ReligionSystem {
    private val saintsDb = listOf(
        "Sw. Hildegarda z Bingen", "Sw. Bonifacy", "Sw. Elzbieta Turyngska",
        "Sw. Michal Archaniol", "Sw. Benedykt z Nursji"
    )
    fun pray(location: String): String {
        val p = GameRepository.state.prayer
        val gain = when (location) {
            "town_chapel"   -> 5
            "forest_shrine" -> { p.shrineVisited = true; 10 }
            else -> 2
        }
        p.faith = minOf(100, p.faith + gain)
        p.blessings++
        GameRepository.log("Modlitwa w $location. Wiara: ${p.faith}")
        return "Modlitwa w $location. +$gain wiary. Laczna wiara: ${p.faith}"
    }
    fun sin(amount: Int = 1): String {
        val p = GameRepository.state.prayer
        p.sins += amount
        p.virtue = maxOf(0, p.virtue - amount * 2)
        return "Grzech popeniony. Grzechy: ${p.sins}, Cnota: ${p.virtue}"
    }
    fun saints() = saintsDb
}
