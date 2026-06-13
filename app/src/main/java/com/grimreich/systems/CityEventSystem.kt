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
        events.clear() // Force clear to ensure new quests are added
        CityCatalogue.seedCanonical()
        
        // Seed events based on CityCatalogue IDs
        CityCatalogue.all().forEach { city ->
            events.add(CityEvent(
                id = "${city.id}_general_event",
                title = "General Event for ${city.name}",
                description = "Something is happening in ${city.name}...",
                cityId = city.id
            ))
            
            // Canonical specific events
            if (city.id == "wybrzeze_polnocne") {
                events.add(CityEvent("north_mist_vision", "Wizje we Mgle", "Aelion przemawia przez gęstą mgłę, żądając ofiary z pamięci.", "wybrzeze_polnocne", 75))
                events.add(CityEvent("north_lost_echo", "Zaginione Echo", "Ktoś zgubił swój cień na brzegu. Znajdź go, zanim pochłonie go nicość.", "wybrzeze_polnocne", 100))
            }
            if (city.id == "rowniny_koronne") {
                events.add(CityEvent("crown_blood_toll", "Podatek Krwi", "Xyrel żąda dowodu istnienia. Przynieś krew pokonanych wrogów.", "rowniny_koronne", 120))
                events.add(CityEvent("crown_iron_forge", "Kuźnia Przetrwania", "Pomóż Ferrunowi wykuć oręż, który nie wyparuje w świetle Absolutu.", "rowniny_koronne", 90))
            }
            if (city.id == "serce_krainy") {
                events.add(CityEvent("heart_mirror_truth", "Lustro Prawdy", "Spójrz w gładką taflę jeziora i zmierz się ze swoim odbiciem.", "serce_krainy", 150))
            }
        }
    }

    fun getEventsForCity(cityId: String): List<CityEvent> =
        events.filter { it.cityId == cityId }

    fun clear() = events.clear()
}
