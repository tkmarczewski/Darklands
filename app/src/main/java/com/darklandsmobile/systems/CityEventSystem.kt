package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.world.CityCatalogue

/**
 * Proste eventy miejskie uzależnione od reputacji lokalnej.
 */
object CityEventSystem {
    fun runCityEvent(cityId: String): String {
        val city = CityCatalogue.get(cityId) ?: return "Nieznane miasto: $cityId"
        val rep = ReputationSystem.getCityRep(city.name)
        val event = when {
            rep >= 50 -> "Mieszczanie witają drużynę z zaufaniem."
            rep >= 0 -> "Na rynku trwa zwyczajny dzień."
            rep >= -50 -> "Straż patrzy na was podejrzliwie."
            else -> "W mieście szepczą o was niechętnie."
        }
        city.events.add(event)
        GameRepository.log("$event @ ${city.name}")
        return event
    }
}