package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.core.Season
import com.grimreich.core.WorldMap
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
    fun rest(): String {
        gameRepository.updateState { s ->
            s.world.fatigue = 0
            s.world.day += 1
            s.world.timeOfDay = "morning"
            s.world.season = currentSeason(s.world.day)
            s.logEntries.add("Drużyna odpoczęła. Rozpoczyna się dzień ${s.world.day}.")
        }
        return "Drużyna odpoczęła. Zmęczenie zresetowane, nowy dzień."
    }

    private fun currentSeason(day: Int): Season {
        val cycle = day % 360
        return when {
            cycle < 90 -> Season.SPRING
            cycle < 180 -> Season.SUMMER
            cycle < 270 -> Season.AUTUMN
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
