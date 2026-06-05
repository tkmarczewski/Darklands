package com.grimreich.systems

import com.grimreich.core.CityScreenState
import com.grimreich.core.PlayerState
import com.grimreich.core.ResolutionScreenState
import com.grimreich.core.TravelScreenState
import com.grimreich.core.WorldMap
import com.grimreich.world.CityCatalogue

/**
 * UI-facing façade for a minimal playable loop:
 * city -> quest list -> travel -> complete -> updated player state.
 */
object GameLoopController {
    fun bootstrap(seed: Int = 1): PlayerState {
        CityCatalogue.seedSprint1()
        WorldMap.seedStage1()
        CityEventSystem.seedStage1Events()
        QuestSystem.seedIntegratedContent(seed)
        return PlayerState()
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
        val questId = playerState.activeQuestId ?: error("No active quest")
        val quest = QuestSystem.all().firstOrNull { it.id == questId } ?: error("Unknown quest: $questId")

        val traveledState = if (playerState.currentCityId != quest.cityId) {
            TravelSystem.travel(playerState.currentCityId, quest.cityId, playerState.travelState).first
        } else {
            playerState.travelState
        }

        val updatedPlayer = playerState.copy(
            currentCityId = quest.cityId,
            travelState = traveledState
        )

        val travelScreen = TravelScreenState(
            fromCityId = playerState.currentCityId,
            toCityId = quest.cityId,
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
        val questId = playerState.activeQuestId ?: error("No active quest")
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
            "\nFound items: " + reward.itemsAwarded.joinToString { it.name }
        else ""

        val resolutionState = ResolutionScreenState(
            questId = reward.questId,
            cityId = reward.cityId,
            goldBefore = goldBefore,
            goldAfter = updatedPlayer.gold,
            reputationAfter = reward.updatedReputation,
            summary = "Quest completed in ${reward.cityId}: +${reward.goldAwarded} gold, reputation ${reward.updatedReputation}.$itemMsg"
        )

        return updatedPlayer to resolutionState
    }
}