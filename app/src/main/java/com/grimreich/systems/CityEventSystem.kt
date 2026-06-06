package com.grimreich.systems

import com.grimreich.world.CityCatalogue

data class CityEvent(
    val id: String,
    val title: String,
    val description: String,
    val cityId: String,
    val rewardGold: Int = 50
)

object CityEventSystem {
    private val events = mutableListOf<CityEvent>()

    fun seedStage1Events() {
        if (events.isNotEmpty()) return
        CityCatalogue.seedSprint1()
        
        // Seed events based on CityCatalogue IDs
        CityCatalogue.all().forEach { city ->
            events.add(CityEvent(
                id = "${city.id}_general_event",
                title = "General Event for ${city.name}",
                description = "Something is happening in ${city.name}...",
                cityId = city.id
            ))
            
            // Add specific events based on city type or specific IDs
            if (city.id == "grimhold") {
                events.add(CityEvent("grimhold_guild_pressure", "Guild Pressure in Grimhold", "The guilds are demanding answers.", "grimhold", 75))
            }
        }
    }

    fun getEventsForCity(cityId: String): List<CityEvent> =
        events.filter { it.cityId == cityId }

    fun clear() = events.clear()
}
