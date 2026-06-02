package com.darklandsmobile.systems

import com.darklandsmobile.world.CityCatalogue

object CityEventSystem {

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

    // Jeśli masz seedStage1Events/getEventsForCity w CityEventSystem, dopisz je tutaj
}