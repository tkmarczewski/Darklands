package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldMap
import com.grimreich.core.Season
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldMap: WorldMap,
    private val cityCatalogue: CityCatalogue,
    private val encounterSystem: EncounterSystem
) {
    fun restDirect(state: GameState): String {
        state.world.fatigue = 0
        state.world.day += 1
        state.world.timeOfDay = "morning"
        state.world.season = currentSeason(state.world.day)
        state.logEntries.add("Drużyna odpoczęła. Rozpoczyna się dzień ${state.world.day}.")
        return "Drużyna odpoczęła. Zmęczenie zresetowane, nowy dzień."
    }

    fun rest(): String {
        var msg = ""
        gameRepository.updateState { s ->
            msg = restDirect(s)
        }
        return msg
    }

    fun currentSeason(day: Int): Season {
        return when ((day / 30) % 4) {
            0 -> Season.SPRING
            1 -> Season.SUMMER
            2 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    fun travelTo(destCityId: String) {
        val state = gameRepository.currentState()
        val current = state.grimCurrentRegion
        
        if (current == destCityId) return
        
        val terrain = worldMap.terrainBetween(current, destCityId)
        val fatigueCost = when (terrain?.name) {
            "ROAD" -> 10
            "FOREST" -> 20
            "MOUNTAIN" -> 35
            else -> 15
        }

        gameRepository.updateState { s ->
            s.grimCurrentRegion = destCityId
            s.world.location = destCityId
            s.world.fatigue = (s.world.fatigue + fatigueCost).coerceAtMost(100)
            
            // Time progression
            if (s.world.timeOfDay == "morning") s.world.timeOfDay = "evening"
            else {
                s.world.timeOfDay = "morning"
                s.world.day += 1
                s.world.season = currentSeason(s.world.day)
            }
            
            if (!s.world.discoveredLocations.contains(destCityId)) {
                s.world.discoveredLocations.add(destCityId)
            }
            
            s.logEntries.add("Podróż do ${cityCatalogue.get(destCityId)?.name ?: destCityId} zakończona.")
        }
    }

    fun getSeasonDisplay(): String {
        val s = gameRepository.currentState().world.season
        return when (s) {
            Season.SPRING -> "WIOSNA"
            Season.SUMMER -> "LATO"
            Season.AUTUMN -> "JESIEŃ"
            Season.WINTER -> "ZIMA"
        }
    }
}
