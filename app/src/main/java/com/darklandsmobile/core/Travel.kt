package com.darklandsmobile.core

import kotlin.random.Random

// ==================== SEASON ====================

enum class Season {
    SPRING, SUMMER, AUTUMN, WINTER;

    fun displayName(): String = when (this) {
        SPRING -> "Wiosna"
        SUMMER -> "Lato"
        AUTUMN -> "Jesień"
        WINTER -> "Zima"
    }

    fun travelModifier(): Float = when (this) {
        SPRING -> 1.0f
        SUMMER -> 0.9f
        AUTUMN -> 1.1f
        WINTER -> 1.4f
    }
}

// ==================== ENCOUNTER ====================

enum class EncounterType {
    AMBUSH, MERCHANT, PILGRIMS, PATROL, BEAST, RUINS, NOTHING
}

enum class EncounterOutcome {
    COMBAT, TRADE, BLESSING, BRIBE, FLEE, NONE
}

data class Encounter(
    val type: EncounterType,
    val title: String,
    val description: String,
    val possibleOutcomes: List<EncounterOutcome>,
    val goldDelta: Int = 0,
    val reputationDelta: Int = 0,
    val fatigueDelta: Int = 0,
    val faithDelta: Int = 0,
    val triggersCombat: Boolean = false
)

object EncounterSystem {

    private val FOREST_ENCOUNTERS = listOf(
        Encounter(
            type = EncounterType.AMBUSH,
            title = "Zasądzka!",
            description = "Rozbojnicy wyskakują z zarośli.",
            possibleOutcomes = listOf(EncounterOutcome.COMBAT, EncounterOutcome.FLEE, EncounterOutcome.BRIBE),
            goldDelta = -20, fatigueDelta = 3, triggersCombat = true
        ),
        Encounter(
            type = EncounterType.BEAST,
            title = "Dzikie zwierzę",
            description = "Wilk lub dzik przecina drogę.",
            possibleOutcomes = listOf(EncounterOutcome.COMBAT, EncounterOutcome.FLEE),
            fatigueDelta = 2, triggersCombat = true
        ),
        Encounter(
            type = EncounterType.RUINS,
            title = "Stare ruiny",
            description = "Znalazłeś porzucony klasztor.",
            possibleOutcomes = listOf(EncounterOutcome.BLESSING, EncounterOutcome.NONE),
            faithDelta = 1
        ),
        Encounter(
            type = EncounterType.NOTHING,
            title = "Spokojna droga",
            description = "Las jest cichy i spokojny.",
            possibleOutcomes = listOf(EncounterOutcome.NONE),
            fatigueDelta = -1
        )
    )

    private val ROAD_ENCOUNTERS = listOf(
        Encounter(
            type = EncounterType.MERCHANT,
            title = "Wędrowny kupiec",
            description = "Kupiec oferuje swoje towary.",
            possibleOutcomes = listOf(EncounterOutcome.TRADE, EncounterOutcome.NONE),
            goldDelta = 5
        ),
        Encounter(
            type = EncounterType.PILGRIMS,
            title = "Pielgrzymi",
            description = "Grupa pielgrzymów zmierza do katedry.",
            possibleOutcomes = listOf(EncounterOutcome.BLESSING, EncounterOutcome.NONE),
            faithDelta = 2, reputationDelta = 1
        ),
        Encounter(
            type = EncounterType.PATROL,
            title = "Patrolu rycerski",
            description = "Rycerze sprawdzają dokumenty podróżnych.",
            possibleOutcomes = listOf(EncounterOutcome.NONE, EncounterOutcome.BRIBE),
            reputationDelta = 1
        ),
        Encounter(
            type = EncounterType.AMBUSH,
            title = "Napad na trakcie",
            description = "Uzbrojeni bandyci blokują drogę.",
            possibleOutcomes = listOf(EncounterOutcome.COMBAT, EncounterOutcome.BRIBE, EncounterOutcome.FLEE),
            goldDelta = -15, fatigueDelta = 2, triggersCombat = true
        ),
        Encounter(
            type = EncounterType.NOTHING,
            title = "Pusta droga",
            description = "Trakt jest spokojny i bezpieczny.",
            possibleOutcomes = listOf(EncounterOutcome.NONE)
        )
    )

    fun rollEncounter(terrain: String): Encounter? {
        val roll = Random.nextInt(100)
        if (roll < 30) return null // 30% brak spotkania

        val pool = when (terrain.lowercase()) {
            "las", "forest" -> FOREST_ENCOUNTERS
            "trakt", "road" -> ROAD_ENCOUNTERS
            else -> ROAD_ENCOUNTERS
        }
        return pool.random()
    }
}

// ==================== TRAVEL SYSTEM ====================

data class TravelState(
    var currentLocationId: String = "town_start",
    var fatigue: Int = 0,
    var daysElapsed: Int = 0,
    var season: Season = Season.SPRING
)

data class TravelResult(
    val encounter: Encounter?,
    val fatigueDelta: Int,
    val daysSpent: Int,
    val message: String
)

object TravelSystem {

    fun travel(
        state: TravelState,
        destination: WorldMap,
        distanceDays: Int
    ): TravelResult {
        val modifier = state.season.travelModifier()
        val actualDays = (distanceDays * modifier).toInt().coerceAtLeast(1)
        val fatigueDelta = actualDays + (if (state.fatigue > 10) 2 else 0)

        state.fatigue = (state.fatigue + fatigueDelta).coerceAtMost(20)
        state.daysElapsed += actualDays
        state.currentLocationId = destination.id

        // Zmiana sezonu co 30 dni
        val seasonIndex = (state.daysElapsed / 30) % 4
        state.season = Season.values()[seasonIndex]

        val encounter = EncounterSystem.rollEncounter(destination.terrain)

        val msg = buildString {
            appendLine("Dotarłeś do ${destination.name} po $actualDays dniach.")
            appendLine("Sezon: ${state.season.displayName()} | Zmęczenie: ${state.fatigue}")
            if (encounter != null) appendLine("Spotkanie: ${encounter.title}")
        }

        return TravelResult(
            encounter = encounter,
            fatigueDelta = fatigueDelta,
            daysSpent = actualDays,
            message = msg
        )
    }

    fun rest(state: TravelState, days: Int = 1): String {
        val recovery = (days * 3).coerceAtMost(state.fatigue)
        state.fatigue -= recovery
        state.daysElapsed += days
        return "Odpoczywałeś $days dzi. Zmęczenie: ${state.fatigue}"
    }

    fun fatigueDescription(fatigue: Int): String = when {
        fatigue >= 18 -> "Wyczerpany"
        fatigue >= 12 -> "Zmęczony"
        fatigue >= 6 -> "Lekko zmęczony"
        else -> "Wypoczęty"
    }
}
