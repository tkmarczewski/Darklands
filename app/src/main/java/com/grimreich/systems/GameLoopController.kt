package com.grimreich.systems

import android.util.Log
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
    private val cityCatalogue: CityCatalogue,
    private val verdictIncidentsSystem: VerdictIncidentsSystem,
    private val metaObservationSystem: MetaObservationSystem
) {
    companion object {
        private const val TAG = "GameLoopController"
    }

    private var isBootstrapping = false

    suspend fun bootstrap(seed: Int = 1): PlayerState {
        if (isBootstrapping) {
            Log.w(TAG, "Bootstrap juz trwa, ignoruje powtorne wywolanie.")
            return PlayerState() 
        }
        isBootstrapping = true
        try {
            Log.i(TAG, "Rozpoczynam bootstrap swiata GrimReich (seed=$seed)")
            gameRepository.clearSessionAndReset()
            gameBootstrapper.bootstrapFreshWorld(seed)

            val startingCityId = cityCatalogue.startingCityId
            return PlayerState(currentCityId = startingCityId)
        } catch (e: Exception) {
            Log.e(TAG, "Blad krytyczny podczas bootstrapu", e)
            throw e
        } finally {
            isBootstrapping = false
        }
    }

    fun cityScreen(playerState: PlayerState): CityScreenState {
        verdictIncidentsSystem.onCityEntered(playerState.currentCityId)
        return CityScreenState(
            cityId = playerState.currentCityId,
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId
        )
    }

    fun acceptQuest(playerState: PlayerState, questId: String): PlayerState {
        Log.d(TAG, "Akceptacja zadania: $questId")
        questEngine.activateQuest(questId)
        return playerState.copy(activeQuestId = questId)
    }

    fun travelToQuest(playerState: PlayerState): Pair<PlayerState, TravelScreenState> {
        val state = gameRepository.currentState()
        val questId = playerState.activeQuestId ?: error("Brak aktywnego zadania")
        val quest = questEngine.getDefinition(questId) ?: error("Nieznane zadanie: $questId")
        val progress = state.quest.progress[questId]

        val destinationCity = if (progress != null && (progress.status == QuestStatus.ACTIVE || progress.status == QuestStatus.OBJECTIVE_MET)) {
            val step = quest.steps.getOrNull(progress.currentStepIndex)
            if (step != null && (step.type == StepType.EXPEDITION || step.type == StepType.DIALOGUE)) {
                step.targetId
            } else {
                quest.cityId
            }
        } else {
            quest.cityId
        }

        Log.i(TAG, "Podroz do celu zadania: $destinationCity (Quest: $questId)")

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
        val status = questEngine.getStatus(questId)
        
        if (status == QuestStatus.OBJECTIVE_MET) {
            Log.i(TAG, "Zadanie $questId gotowe do zakonczenia. Finalizacja...")
            questEngine.completeQuest(questId)
            metaObservationSystem.onQuestCompleted(questId)
            return playerState.copy(activeQuestId = null)
        }
        return playerState
    }
}
