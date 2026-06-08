package com.grimreich.systems

import com.grimreich.core.CityScreenState
import com.grimreich.core.GameRepository
import com.grimreich.core.PlayerState
import com.grimreich.core.ResolutionScreenState
import com.grimreich.core.TravelScreenState
import com.grimreich.core.WorldMap
import com.grimreich.world.CityCatalogue

object GameLoopController {
    fun bootstrap(seed: Int = 1): PlayerState {
        GameRepository.seed()
        
        CityCatalogue.clear()
        WorldMap.clear()
        QuestSystem.clear()
        
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
        CityEventSystem.seedStage1Events()
        QuestSystem.seedIntegratedContent(seed)
        
        val startingCityId = CityCatalogue.startingCityId
        GameRepository.state.world.location = startingCityId
        
        return PlayerState(currentCityId = startingCityId)
    }

    fun cityScreen(playerState: PlayerState): CityScreenState {
        val quests = QuestSystem.availableForCity(playerState.currentCityId)
        return CityScreenState(
            cityId = playerState.currentCityId,
            availableQuests = quests,
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId
        )
    }

    fun acceptQuest(playerState: PlayerState, questId: String): PlayerState {
        QuestSystem.activate(questId)
        return playerState.copy(activeQuestId = questId)
    }

    fun travelToQuest(playerState: PlayerState): Pair<PlayerState, TravelScreenState> {
        val questId = playerState.activeQuestId ?: error("Brak aktywnego zadania")
        val quest = QuestSystem.all().find { it.id == questId } ?: error("Nieznane zadanie: $questId")

        val destinationCity = quest.cityId

        val traveledState = if (playerState.currentCityId != destinationCity) {
            TravelSystem.travel(playerState.currentCityId, destinationCity, playerState.travelState).first
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
    ): Pair<PlayerState, ResolutionScreenState> {
        val questId = playerState.activeQuestId ?: error("Brak zadania do wykonania")
        val goldBefore = playerState.gold
        val reward = QuestResolutionSystem.completeQuestWithRewards(
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

        val itemMsg = if (reward.itemsAwarded.isNotEmpty()) 
            "\nZnalezione artefakty: " + reward.itemsAwarded.joinToString { it.name }
        else ""

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
