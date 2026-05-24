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


// ==================== DAY / NIGHT SYSTEM ====================

enum class TimeOfDay(val label: String, val hour: Int) {
    DAWN     ("Swit",      5),
    MORNING  ("Rano",      8),
    MIDDAY   ("Poludnie",  12),
    AFTERNOON("Popoldnie", 15),
    DUSK     ("Zmierzch",  18),
    EVENING  ("Wieczor",   20),
    MIDNIGHT ("Polnoc",    0),
    DEEP_NIGHT("Noc",      2);

    fun isNight(): Boolean = this == EVENING || this == MIDNIGHT || this == DEEP_NIGHT
    fun isDusk():  Boolean = this == DUSK

    companion object {
        fun fromHour(hour: Int): TimeOfDay = values().minByOrNull {
            val diff = Math.abs(it.hour - (hour % 24))
            if (diff > 12) 24 - diff else diff
        } ?: MIDDAY
    }
}

data class DayNightState(
    var hour: Int = 8,         // 0-23
    var daysPassed: Int = 0
)

object DayNightSystem {

    // Advance time by given hours; wraps around midnight
    fun advanceHours(state: DayNightState, hours: Int): TimeOfDay {
        state.hour = (state.hour + hours) % 24
        if (state.hour < 0) state.hour += 24
        if (hours >= 24) state.daysPassed += hours / 24
        return TimeOfDay.fromHour(state.hour)
    }

    // Encounter chance modifier based on time of day
    // Night is more dangerous (higher encounter rate, bandits active)
    fun encounterChanceModifier(time: TimeOfDay): Float = when {
        time.isNight()  -> 1.8f   // 80% more encounters at night
        time.isDusk()   -> 1.4f   // 40% more at dusk
        time == TimeOfDay.DAWN -> 1.2f
        else            -> 1.0f   // Normal during day
    }

    // Night travel increases fatigue faster
    fun fatigueMod(time: TimeOfDay): Float = when {
        time.isNight() -> 1.5f   // Night travel more tiring
        time.isDusk()  -> 1.2f
        else           -> 1.0f
    }

    // Narrative description of the time/environment
    fun travelNarrative(time: TimeOfDay, season: Season): String {
        val timeStr = time.label
        val seasonStr = season.displayName()
        return when {
            time.isNight() && season == Season.WINTER ->
                "$timeStr, $seasonStr - mrozna, ciemna noc. Droga jest prawie niewidoczna."
            time.isNight() ->
                "$timeStr, $seasonStr - podrozujesz w ciemnosci. Niebezpieczenstwo czai sie w cieniu."
            time == TimeOfDay.DAWN ->
                "$timeStr, $seasonStr - pierwsze promienie slonca oswietlaja trakt."
            time == TimeOfDay.DUSK ->
                "$timeStr, $seasonStr - slonce zachodzi. Lepiej znalezc schronienie przed noca."
            else ->
                "$timeStr, $seasonStr - podroz przebiega normalnie."
        }
    }
}
