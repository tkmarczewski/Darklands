package com.grimreich.systems

import com.grimreich.world.CityCatalogue

data class CityEvent(
    val id: String,
    val cityId: String,
    val title: String,
    val description: String,
    val rewardGold: Int = 50,
    val minReputation: Int = 0
)

object CityEventSystem {
    private val events = mutableListOf<CityEvent>()

    fun clear() {
        events.clear()
    }

    fun register(event: CityEvent) {
        events.add(event)
    }

    fun getEventsForCity(cityId: String): List<CityEvent> =
        events.filter { it.cityId == cityId }

    fun getAvailableEventsForCity(cityId: String): List<CityEvent> {
        val currentRep = ReputationSystem.getCityRep(cityId)
        return events.filter { it.cityId == cityId && currentRep >= it.minReputation }
    }

    fun seedStage1Events() {
        if (events.isNotEmpty()) return
        CityCatalogue.all().forEach { city ->
            register(CityEvent("${city.id}_general_event", city.id, "General Event for ${city.name}", "Something happens."))
            register(CityEvent("${city.id}_guild_pressure", city.id, "Guild Pressure in ${city.name}", "The guild is restless.", minReputation = 5))
        }
    }

    fun runCityEvent(cityId: String): String {
        val city = CityCatalogue.get(cityId)
            ?: return "Nieznane miasto: $cityId"

        val rep = ReputationSystem.getCityRep(city.name)

        val msg = when {
            rep >= 60 -> "Mieszczanie witaja Was z zaufaniem w swoim mieście."
            rep <= -20 -> "Mieszczanie patrza na Was podejrzliwie tego dnia."
            else -> "To zwyczajny dzien w miescie."
        }

        city.events.add(msg)
        return msg
    }
}
