package com.grimreich.systems

import com.grimreich.core.GameRepository

data class TownStatus(
    val id: String,
    var name: String,
    var developmentLevel: Int = 1, // 1-5
    var investment: Int = 0
)

object TownSystem {
    private val townStates = mutableMapOf<String, TownStatus>()

    fun getTown(id: String): TownStatus {
        return townStates.getOrPut(id) { TownStatus(id, id.replaceFirstChar { it.uppercase() }) }
    }

    fun invest(cityId: String, amount: Int): String {
        val g = GameRepository.state
        if (g.gold < amount) return "Brak wystarczającej ilości złota!"
        
        g.gold -= amount
        val town = getTown(cityId)
        town.investment += amount
        
        val nextLevelCost = town.developmentLevel * 500
        return if (town.investment >= nextLevelCost) {
            town.developmentLevel++
            town.investment -= nextLevelCost
            ChronicleSystem.record("Odbudowano część miasta ${town.name}. Poziom: ${town.developmentLevel}")
            "Poziom rozwoju ${town.name} wzrósł do ${town.developmentLevel}!"
        } else {
            "Zainwestowano $amount złota w ${town.name}."
        }
    }

    fun getPriceModifier(cityId: String): Float {
        val level = getTown(cityId).developmentLevel
        return 1.0f - (level - 1) * 0.1f // 10% discount per level
    }
}
