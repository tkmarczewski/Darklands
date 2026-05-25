package com.darklandsmobile.systems

import com.darklandsmobile.world.CityCatalogue

enum class CityEventFaction {
    KNIGHTS,
    MERCHANTS,
    CHURCH,
    COMMONERS,
    NONE
}

data class CityEvent(
    val id: String,
    val cityId: String,
    val title: String,
    val description: String,
    val affectedFaction: CityEventFaction = CityEventFaction.NONE,
    val requiredReputation: Int = Int.MIN_VALUE,
    val rewardGold: Int = 0
)

/**
 * Stage 1 city event integration.
 * Each major city gets a general event and a faction-gated local event.
 */
object CityEventSystem {
    private val eventsByCity = linkedMapOf<String, MutableList<CityEvent>>()

    fun seedStage1Events() {
        if (eventsByCity.isNotEmpty()) return
        CityCatalogue.seedSprint1()

        CityCatalogue.all().forEach { city ->
            register(
                CityEvent(
                    id = "${city.id}_general_event",
                    cityId = city.id,
                    title = "Wieści z ${city.name}",
                    description = "Na rynku w ${city.name} krążą plotki o napięciach i okazjach do zarobku.",
                    affectedFaction = CityEventFaction.NONE,
                    rewardGold = 10
                )
            )

            val factionEvent = when (city.id) {
                "koln" -> CityEvent(
                    id = "koln_guild_pressure",
                    cityId = city.id,
                    title = "Nacisk cechów",
                    description = "Starszy cechowy szuka ludzi do rozwiązania sporu handlowego.",
                    affectedFaction = CityEventFaction.MERCHANTS,
                    requiredReputation = 10,
                    rewardGold = 35
                )
                "praha" -> CityEvent(
                    id = "praha_pilgrim_shelter",
                    cityId = city.id,
                    title = "Pielgrzymi szukają schronienia",
                    description = "Kapłan prosi o wsparcie pielgrzymów przybywających do miasta.",
                    affectedFaction = CityEventFaction.CHURCH,
                    requiredReputation = 10,
                    rewardGold = 25
                )
                "nurnberg" -> CityEvent(
                    id = "nurnberg_crafts_dispute",
                    cityId = city.id,
                    title = "Spór rzemieślników",
                    description = "Lokalni mistrzowie warsztatów potrzebują mediatora i ochrony towaru.",
                    affectedFaction = CityEventFaction.MERCHANTS,
                    requiredReputation = 5,
                    rewardGold = 30
                )
                "hamburg" -> CityEvent(
                    id = "hamburg_harbor_watch",
                    cityId = city.id,
                    title = "Straż portowa",
                    description = "Portowi strażnicy szukają wsparcia przeciw przemytnikom.",
                    affectedFaction = CityEventFaction.KNIGHTS,
                    requiredReputation = 5,
                    rewardGold = 30
                )
                "wien" -> CityEvent(
                    id = "wien_court_favor",
                    cityId = city.id,
                    title = "Łaska dworu",
                    description = "Dworski urzędnik szuka zaufanych ludzi do delikatnej misji.",
                    affectedFaction = CityEventFaction.KNIGHTS,
                    requiredReputation = 15,
                    rewardGold = 50
                )
                else -> CityEvent(
                    id = "${city.id}_local_faction_event",
                    cityId = city.id,
                    title = "Lokalne napięcia",
                    description = "Wpływowa frakcja w ${city.name} szuka godnych zaufania awanturników.",
                    affectedFaction = CityEventFaction.COMMONERS,
                    requiredReputation = 5,
                    rewardGold = 20
                )
            }

            register(factionEvent)
        }
    }

    fun clear() = eventsByCity.clear()

    fun register(event: CityEvent) {
        val bucket = eventsByCity.getOrPut(event.cityId) { mutableListOf() }
        bucket += event
    }

    fun getEventsForCity(cityId: String): List<CityEvent> {
        seedStage1Events()
        return eventsByCity[cityId]?.toList().orEmpty()
    }

    fun getAvailableEventsForCity(cityId: String): List<CityEvent> {
        seedStage1Events()
        return getEventsForCity(cityId).filter { event ->
            when (event.affectedFaction) {
                CityEventFaction.NONE -> true
                CityEventFaction.KNIGHTS -> ReputationSystem.score(cityId, CityFaction.KNIGHTS) >= event.requiredReputation
                CityEventFaction.MERCHANTS -> ReputationSystem.score(cityId, CityFaction.MERCHANTS) >= event.requiredReputation
                CityEventFaction.CHURCH -> ReputationSystem.score(cityId, CityFaction.CHURCH) >= event.requiredReputation
                CityEventFaction.COMMONERS -> ReputationSystem.score(cityId, CityFaction.COMMONERS) >= event.requiredReputation
            }
        }
    }

    fun runCityEvent(cityId: String): String {
        seedStage1Events()
        val event = getAvailableEventsForCity(cityId).firstOrNull()
            ?: return "Brak dostępnych wydarzeń w mieście: $cityId"

        return "${event.title}: ${event.description} (nagroda: ${event.rewardGold} złota)"
    }
}