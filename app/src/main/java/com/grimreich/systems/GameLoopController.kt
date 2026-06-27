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

        if (playerState.currentCityId != destinationCity) {
            travelSystem.travelTo(destinationCity)
        }

        val updatedPlayer = playerState.copy(
            currentCityId = destinationCity
        )

        val worldState = gameRepository.currentState().world
        val travelScreen = TravelScreenState(
            fromCityId = playerState.currentCityId,
            toCityId = destinationCity,
            totalHoursTraveled = 0,
            fatigue = worldState.fatigue,
            lastEncounterId = null
        )

        return updatedPlayer to travelScreen
    }

    fun resolveActiveQuest(
        playerState: PlayerState
    ): PlayerState {
        val questId = playerState.activeQuestId ?: return playerState
        if (questEngine.getStatus(questId) != QuestStatus.OBJECTIVE_MET) return playerState
        questEngine.completeQuest(questId)
        return playerState.copy(activeQuestId = null)
    }
}
