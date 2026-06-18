package com.grimreich.systems

import android.content.Context
import com.grimreich.core.*
import java.util.Random

object TravelSystem {

    fun travel(
        fromCityId: String,
        toCityId: String,
        partyState: TravelPartyState,
        random: kotlin.random.Random = kotlin.random.Random.Default
    ): Pair<TravelPartyState, TravelResult> {
        val terrain = WorldMap.terrainBetween(fromCityId, toCityId) ?: TerrainType.ROAD
        val hoursSpent = terrain.travelHoursRange.random(random)
        val fatigueGain = hoursSpent * 2
        
        val updatedParty = partyState.copy(
            fatigue = (partyState.fatigue + fatigueGain).coerceAtMost(100),
            totalHoursTraveled = partyState.totalHoursTraveled + hoursSpent
        )
        
        val encounterTriggered = random.nextFloat() < terrain.encounterChance
        val encounterId = if (encounterTriggered) {
            EncounterSystem.rollEncounter(random)?.id
        } else null
        
        val result = TravelResult(
            destinationCityId = toCityId,
            terrain = terrain,
            hoursSpent = hoursSpent,
            fatigueBefore = partyState.fatigue,
            fatigueAfter = updatedParty.fatigue,
            encounterTriggered = encounterTriggered,
            encounterId = encounterId
        )
        
        return updatedParty to result
    }

    fun restInCity(partyState: TravelPartyState, hours: Int): TravelPartyState {
        val recovery = hours * 5
        return partyState.copy(
            fatigue = (partyState.fatigue - recovery).coerceAtLeast(0)
        )
    }

    fun rest(): String {
        val w = GameRepository.state.world
        w.fatigue = 0
        w.day += 1
        w.timeOfDay = "morning"
        advanceSeason()
        return "Wypoczynek zakończony. Siły zregenerowane."
    }

    fun advanceSeason() {
        val w = GameRepository.state.world
        w.season = when (w.day % 120) {
            in 0..29 -> Season.SPRING
            in 30..59 -> Season.SUMMER
            in 60..89 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    fun currentSeason(day: Int): Season {
        return when (day % 120) {
            in 0..29 -> Season.SPRING
            in 30..59 -> Season.SUMMER
            in 60..89 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    fun travelTo(regionId: String, context: Context? = null): String {
        val g = GameRepository.state
        val w = g.world
        val currentLoc = w.location.lowercase().replace(" ", "_")
        
        val (newParty, travelResult) = travel(currentLoc, regionId, g.party.firstOrNull()?.let { 
            TravelPartyState(w.fatigue, 0, w.lastEncounter) 
        } ?: TravelPartyState())
        
        w.location = com.grimreich.world.CityCatalogue.get(regionId)?.name ?: regionId
        g.grimCurrentRegion = regionId
        w.fatigue = newParty.fatigue
        w.day += (travelResult.hoursSpent / 12).coerceAtLeast(1)
        w.timeOfDay = if (newParty.totalHoursTraveled % 24 > 12) "evening" else "afternoon"
        
        if (travelResult.encounterTriggered) {
            val encounter = EncounterSystem.rollEncounter(kotlin.random.Random.Default)
            if (encounter != null) {
                EncounterSystem.activeEncounter = encounter
                // Set pending quest/encounter for Combat screen
                g.pendingQuestId = "encounter:${encounter.id}"
            }
        } else {
            // Narrative random event
            // RandomEventManager.triggerTravelEvent(context) // Needs Context or State update
        }
        
        return "Podróż do $regionId zakończona."
    }

    fun getSeasonDisplay(): String {
        val g = GameRepository.state
        return when (currentSeason(g.world.day)) {
            Season.SPRING -> "Wiosna"
            Season.SUMMER -> "Lato"
            Season.AUTUMN -> "Jesień"
            Season.WINTER -> "Zima"
        }
    }
}
