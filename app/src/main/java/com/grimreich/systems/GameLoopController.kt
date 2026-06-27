package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameLoopController @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameBootstrapper: GameBootstrapper,
    private val questEngine: QuestEngine,
    private val travelSystem: TravelSystem,
    private val cityCatalogue: CityCatalogue
) {
    private var isBootstrapping = false

    suspend fun bootstrap(seed: Int = 1): PlayerState {
        if (isBootstrapping) return PlayerState() 
        isBootstrapping = true
        try {
            gameRepository.clearSessionAndReset()
            gameBootstrapper.bootstrapFreshWorld(seed)

            val startingCityId = cityCatalogue.startingCityId
            return PlayerState(currentCityId = startingCityId)
        } finally {
            isBootstrapping = false
        }
    }

    fun cityScreen(playerState: PlayerState): CityScreenState {
        return CityScreenState(
            cityId = playerState.currentCityId,
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId
        )
    }

    fun acceptQuest(playerState: PlayerState, questId: String): PlayerState {
        questEngine.activateQuest(questId)
        return playerState.copy(activeQuestId = questId)
    }

    fun travelToQuest(playerState: PlayerState): Pair<PlayerState, TravelScreenState> {
        val questId = playerState.activeQuestId ?: error("Brak aktywnego zadania")
        val quest = questEngine.getDefinition(questId) ?: error("Nieznane zadanie: $questId")

        val destinationCity = quest.cityId

        val traveledState = if (playerState.currentCityId != destinationCity) {
            travelSystem.travel(playerState.currentCityId, destinationCity, playerState.travelState).first
        } else {
            playerState.travelState
        }

        val updatedPlayer = playerState.copy(
            currentCityId = destinationCity,
            travelState = traveledState
        )

        val travelScreen = TravelScreenState(
            fromCityId = playerState.currentCityId,
            toCityId = destinationCity,
            totalHoursTraveled = traveledState.totalHoursTraveled,
            fatigue = traveledState.fatigue,
            lastEncounterId = traveledState.lastEncounterId
        )

        return updatedPlayer to travelScreen
    }

    fun resolveActiveQuest(
        playerState: PlayerState
    ): PlayerState {
        val questId = playerState.activeQuestId ?: return playerState
        questEngine.completeQuest(questId)
        return playerState.copy(activeQuestId = null)
    }
}
