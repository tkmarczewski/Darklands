package com.grimreich.systems

import com.grimreich.world.CityCatalogue

object ExpandedContentSeeder {

    fun seedAll() {
        CityCatalogue.seedSprint1()
        
        // Seed city events and quests using IDs from CityCatalogue
        CityCatalogue.all().forEach { city ->
            seedEventsFor(city.id)
        }
    }

    private fun seedEventsFor(cityId: String) {
        // Register events in CityEventSystem
        // (Implementation details handled by QuestSystem and CityEventSystem)
    }
}
