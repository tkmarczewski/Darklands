package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository

object ReputationSystem {
    fun changeCity(city: String, delta: Int): String {
        val rep = GameRepository.state.reputation
        val current = rep.city.getOrDefault(city, 0)
        rep.city[city] = (current + delta).coerceIn(-100, 100)
        GameRepository.log("Reputacja w $city: ${rep.city[city]}")
        return "Reputacja w $city: ${rep.city[city]}"
    }
    fun getCityRep(city: String) = GameRepository.state.reputation.city.getOrDefault(city, 0)
    fun allCities() = GameRepository.state.reputation.city.toMap()
}
