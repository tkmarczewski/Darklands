package com.grimreich.systems

import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

data class CityEvent(
    val id: String,
    val title: String,
    val description: String,
    val cityId: String,
    val rewardGold: Int
)

@Singleton
class CityEventSystem @Inject constructor(
    private val cityCatalogue: CityCatalogue
) {
    private val events = mutableListOf<CityEvent>()

    fun seedStage1Events() {
        cityCatalogue.seedCanonical()
        val allCities = cityCatalogue.all()
        
        allCities.forEach { city ->
            events.add(CityEvent(
                id = "evt_${city.id}_01",
                title = "Wieści z ${city.name}",
                description = "Mieszkańcy ${city.name} szeptają o Twoim przybyciu.",
                cityId = city.id,
                rewardGold = 10
            ))
        }
    }

    fun getEventsForCity(cityId: String): List<CityEvent> =
        events.filter { it.cityId == cityId }

    fun clear() {
        events.clear()
    }
}
