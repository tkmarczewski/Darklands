package com.darklandsmobile.systems

import com.darklandsmobile.core.TravelPartyState
import com.darklandsmobile.core.TravelResult
import com.darklandsmobile.core.TravelRules
import com.darklandsmobile.core.WorldMap
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
            fatigue = partyState.fatigue + fatigueGain,
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
        return partyState.copy(fatigue = TravelRules.reduceFatigueWithRest(partyState.fatigue, restHours))
    }
}
