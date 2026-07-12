package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.WorldMap
import com.grimreich.core.Season
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.EncounterSystem
import com.grimreich.systems.CollapseEngine
import com.grimreich.systems.CollapseEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldMap: WorldMap,
    private val cityCatalogue: CityCatalogue,
    private val encounterSystem: EncounterSystem,
    private val worldStabilitySystem: WorldStabilitySystem,
    private val collapseEngine: CollapseEngine
) {
    fun restDirect(state: GameState): String {
        state.world.fatigue = 0
        worldStabilitySystem.advanceDayDirect(state, "Drużyna odpoczęła.")
        state.world.timeOfDay = "morning"
        state.world.season = currentSeason(state.world.day)
        
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
        val daysSpent = when (terrain?.name) {
            "ROAD" -> 7
            "FOREST" -> 14
            "MOUNTAIN" -> 21
            else -> 10
        }

        gameRepository.updateState { s ->
            s.grimCurrentRegion = destCityId
            s.world.locationId = destCityId
            s.world.day += daysSpent
            
            // Advance career years (1 year = 365 days)
            val yearsPassed = daysSpent.toFloat() / 365f
            s.party.forEach { hero ->
                val entry = hero.careerHistory.find { it.career == hero.currentCareer }
                if (entry != null) {
                    val updated = entry.copy(yearsServed = entry.yearsServed + yearsPassed)
                    val index = hero.careerHistory.indexOf(entry)
                    hero.careerHistory[index] = updated
                }
            }

            s.world.fatigue = (s.world.fatigue + daysSpent * 2).coerceAtMost(100)
            
            // Travel also contributes to collapse
            collapseEngine.processCollapseEventDirect(s, CollapseEvent.TravelCompleted(daysSpent * 2))
            
            if (!s.world.discoveredLocations.contains(destCityId)) {
                s.world.discoveredLocations.add(destCityId)
            }
            
            s.logEntries.add("Podróż do ${cityCatalogue.get(destCityId)?.name ?: destCityId} trwała $daysSpent dni.")
            s.normalizeState()
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
