package com.grimreich.systems

import com.grimreich.core.TravelPartyState
import com.grimreich.core.TravelResult
import com.grimreich.core.TravelRules
import com.grimreich.core.WorldMap
import com.grimreich.core.GameRepository
import com.grimreich.core.Season
import kotlin.random.Random

/**
 * TODO[travel] Integrate party food, weight and UI presentation.
 * systems/ orchestrates gameplay rules defined in core.
 */
object TravelSystem {

    fun travel(
        fromCityId: String,
        toCityId: String,
        partyState: TravelPartyState,
        random: Random = Random.Default
    ): Pair<TravelPartyState, TravelResult> {
        WorldMap.seedStage1()
        val terrain = WorldMap.terrainBetween(fromCityId, toCityId)
            ?: error("Cities are not directly connected: $fromCityId -> $toCityId")

        val hoursSpent = TravelRules.computeSegmentHours(terrain, random)
        val fatigueGain = TravelRules.computeFatigueGain(terrain, hoursSpent)
        val encounterTriggered = TravelRules.encounterRoll(terrain, random)
        val encounterId = if (encounterTriggered) TravelRules.encounterForTerrain(terrain, random) else null

        val updatedState = partyState.copy(
            fatigue = minOf(partyState.fatigue + fatigueGain, 100),
            totalHoursTraveled = partyState.totalHoursTraveled + hoursSpent,
            lastEncounterId = encounterId
        )

        val result = TravelResult(
            destinationCityId = toCityId,
            terrain = terrain,
            hoursSpent = hoursSpent,
            fatigueBefore = partyState.fatigue,
            fatigueAfter = updatedState.fatigue,
            encounterTriggered = encounterTriggered,
            encounterId = encounterId
        )
        return updatedState to result
    }

    fun restInCity(partyState: TravelPartyState, restHours: Int = 8): TravelPartyState {
        return partyState.copy(
            fatigue = TravelRules.reduceFatigueWithRest(partyState.fatigue, restHours)
        )
    }

    fun rest(): String {
        val w = GameRepository.state.world
        val newFatigue = (w.fatigue - 20).coerceAtLeast(0)
        w.fatigue = newFatigue
        w.day += 1
        w.timeOfDay = "morning"

        val msg = "Druzyna odpoczela i odzyskala sily."
        GameRepository.log(msg)
        return msg
    }

    fun advanceSeason() {
        val w = GameRepository.state.world
        w.season = when (w.season) {
            Season.SPRING -> Season.SUMMER
            Season.SUMMER -> Season.AUTUMN
            Season.AUTUMN -> Season.WINTER
            Season.WINTER -> Season.SPRING
        }
    }

    fun currentSeason(day: Int): Season {
        val slot = (day / 30) % 4
        return when (slot) {
            0 -> Season.SPRING
            1 -> Season.SUMMER
            2 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    fun travelTo(region: String, context: android.content.Context? = null): String {
        val w = GameRepository.state.world
        w.region = region
        w.location = region.replaceFirstChar { it.uppercase() }
        w.day += 1
        w.timeOfDay = when (w.timeOfDay) {
            "morning"   -> "afternoon"
            "afternoon" -> "evening"
            "evening"   -> "night"
            else        -> "morning"
        }
        w.fatigue = minOf(w.fatigue + 1, 100)
        
        val encounter = EncounterSystem.rollEncounter(kotlin.random.Random.Default)
        val encounterMsg = if (encounter != null) {
            EncounterSystem.activeEncounter = encounter
            if (context != null) {
                context.startActivity(android.content.Intent(context, com.grimreich.ui.EncounterActivity::class.java))
            }
            "\nSpotkanie: ${encounter.title}"
        } else {
            ""
        }
        
        GameRepository.log("Podroz do regionu: $region")
        return "Podroz do $region zakonczona.$encounterMsg"
    }

    fun getSeasonDisplay(): String {
        val w = GameRepository.state.world
        val season = currentSeason(w.day)
        return when (season) {
            Season.SPRING -> "Wiosna"
            Season.SUMMER -> "Lato"
            Season.AUTUMN -> "Jesien"
            Season.WINTER -> "Zima"
        }
    }
}