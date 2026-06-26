package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameLoopController @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameBootstrapper: GameBootstrapper,
    private val questSystem: QuestSystem,
    private val questResolutionSystem: QuestResolutionSystem,
    private val travelSystem: TravelSystem,
    private val cityCatalogue: CityCatalogue
) {
    suspend fun bootstrap(seed: Int = 1): PlayerState {
        gameRepository.clearSessionAndReset()
        gameBootstrapper.bootstrapFreshWorld(seed)

        val startingCityId = cityCatalogue.startingCityId
        return PlayerState(currentCityId = startingCityId)
    }

    fun cityScreen(playerState: PlayerState): CityScreenState {
        val quests = questSystem.availableForCity(playerState.currentCityId)
        return CityScreenState(
            cityId = playerState.currentCityId,
            availableQuests = quests,
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId
        )
    }

    fun acceptQuest(playerState: PlayerState, questId: String): PlayerState {
                if (questSystem.getQuest(questId) != null) questSystem.activate(questId)
        return playerState.copy(activeQuestId = questId)
    }

    fun travelToQuest(playerState: PlayerState): Pair<PlayerState, TravelScreenState> {
        val questId = playerState.activeQuestId ?: error("Brak aktywnego zadania")
        val quest = questSystem.all().find { it.id == questId } ?: error("Nieznane zadanie: $questId")

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
        playerState: PlayerState,
        faction: CityFaction = CityFaction.COMMONERS
    ): Pair<PlayerState, ResolutionScreenState>? {
        val questId = playerState.activeQuestId ?: return null
        val goldBefore = playerState.gold

        val reward = questResolutionSystem.completeQuestWithRewards(
            questId = questId,
            partyState = playerState.travelState,
            faction = faction,
            reputationDelta = 5
        )

        val updatedPlayer = playerState.copy(
            gold = playerState.gold + reward.goldAwarded,
            activeQuestId = null,
            completedQuestIds = playerState.completedQuestIds + questId,
            travelState = reward.updatedPartyState
        )

        val itemMsg = if (reward.itemsAwarded.isNotEmpty()) {
            "\nZnalezione artefakty: " + reward.itemsAwarded.joinToString { it.name }
        } else {
            ""
        }

        val resolutionState = ResolutionScreenState(
            questId = reward.questId,
            cityId = reward.cityId,
            goldBefore = goldBefore,
            goldAfter = updatedPlayer.gold,
            reputationAfter = reward.updatedReputation,
            summary = "Misja zakończona w ${reward.cityId}: +${reward.goldAwarded} złota, reputacja ${reward.updatedReputation}.$itemMsg"
        )

        return updatedPlayer to resolutionState
    }
}
