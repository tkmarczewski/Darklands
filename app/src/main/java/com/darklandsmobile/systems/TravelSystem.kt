package com.darklandsmobile.systems

import com.darklandsmobile.core.EncounterSystem
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.Season
import com.darklandsmobile.core.WorldMap

object TravelSystem {

    fun travelTo(regionOrNodeId: String): String {
        val w    = GameRepository.state.world
        val node = WorldMap.all().firstOrNull { it.id == regionOrNodeId || it.region == regionOrNodeId }
            ?: return "Nieznane miejsce: $regionOrNodeId"

        val season = currentSeason(w.day)
        val fatigueCost = (5 * season.travelModifier()).toInt().coerceAtLeast(1)

        w.region       = node.region
        w.location     = node.name
        w.day         += 1
        w.fatigue     += fatigueCost
        w.timeOfDay    = when (w.day % 3) { 0 -> "night"; 1 -> "morning"; else -> "afternoon" }

        val encounter = EncounterSystem.rollEncounter(node.region)
        w.lastEncounter = encounter?.type?.name?.lowercase() ?: "none"

        // Apply encounter side-effects to world state
        if (encounter != null) {
            w.fatigue = (w.fatigue + encounter.fatigueDelta).coerceAtLeast(0)
        }

        if (w.fatigue >= 80) GameRepository.log("Druzyna jest wyczerpana!")

        val encounterLine = if (encounter != null)
            " Spotkanie: ${encounter.title} — ${encounter.description}" else ""
        val msg = "Podroz do ${node.name} (dzien ${w.day}, ${season.displayName()})." + encounterLine
        GameRepository.log(msg)
        return msg
    }

    fun rest(): String {
        val w         = GameRepository.state.world
        val recovered = minOf(w.fatigue, 20)
        w.fatigue    -= recovered
        w.day        += 1
        w.timeOfDay   = "morning"
        val msg = "Odpoczynek. Zmeczenie: ${w.fatigue}"
        GameRepository.log(msg)
        return "Druzyna odpoczela. Zmeczenie: ${w.fatigue}"
    }

    fun advanceSeason(): String {
        val w = GameRepository.state.world
        w.season = when (w.season) {
            Season.SPRING -> Season.SUMMER
            Season.SUMMER -> Season.AUTUMN
            Season.AUTUMN -> Season.WINTER
            Season.WINTER -> Season.SPRING
        }
        GameRepository.log("Zmiana pory roku: ${w.season}")
        return "Nowa pora roku: ${w.season.displayName()}"
    }

    fun currentSeason(day: Int): Season {
        return Season.values()[(day / 30) % 4]
    }

    fun getSeasonDisplay(): String {
        val w = GameRepository.state.world
        return currentSeason(w.day).displayName()
    }
}
