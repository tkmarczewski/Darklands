package com.grimreich.systems

import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpandedContentSeeder @Inject constructor(
    private val cityCatalogue: CityCatalogue
) {
    fun seedAll() {
        cityCatalogue.seedCanonical()
        val all = cityCatalogue.all()
        all.forEach { city ->
            seedEventsFor(city.id)
        }
    }

    private fun seedEventsFor(cityId: String) {
        // Mock seeding for now
    }
}
