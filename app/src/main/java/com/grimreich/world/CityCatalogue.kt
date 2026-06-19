package com.grimreich.world

import javax.inject.Inject
import javax.inject.Singleton

data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val phenomenon: String = "Mgła",
    val rulingFaction: String = "Kościół",
    val priceModifier: Float = 1.0f,
    val backgroundDrawable: String = "bg_city_default",
    val loreDescription: String = "",
    val primaryArtifact: String = "",
    val events: MutableList<String> = mutableListOf(),
    val prophet: String? = null
)

@Singleton
class CityCatalogue @Inject constructor() {
    val startingCityId = "wybrzeze_polnocne"
    private val cities = linkedMapOf<String, CityData>()

    fun register(city: CityData) {
        cities[city.id] = city
    }

    fun get(id: String?): CityData? = cities[id ?: ""]

    fun all(): List<CityData> = cities.values.toList()

    fun clear() {
        cities.clear()
    }

    fun seedCanonical() {
        if (cities.isNotEmpty()) return
        
        register(CityData(
            id = "wybrzeze_polnocne",
            name = "Wybrzeże Północne",
            region = "North",
            phenomenon = "Echo Przeszłości",
            priceModifier = 1.0f,
            backgroundDrawable = "bg_region_north_coast",
            loreDescription = "Miejsce, gdzie zaczyna się mgła."
        ))
        
        register(CityData(
            id = "twierdza_zelazna",
            name = "Twierdza Żelazna",
            region = "North",
            phenomenon = "Zamarznięty Czas",
            priceModifier = 1.2f,
            backgroundDrawable = "bg_region_iron_fortress",
            loreDescription = "Ostatni bastion ludzkości."
        ))
    }
}
