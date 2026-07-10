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
    private val questManifest: QuestManifest,
    private val travelSystem: TravelSystem,
    private val cityCatalogue: CityCatalogue,
    private val cityVisitCampaignSystem: CityVisitCampaignSystem,
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

            // Bug #10: resetujemy registry i seedujemy questy przy nowej grze,
            // zeby nie dziedziczyc stanu z poprzedniej sesji
            questEngine.clearRegistry()
            gameBootstrapper.bootstrapFreshWorld(seed)
            questManifest.seed()

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
        cityVisitCampaignSystem.onCityEntered(playerState.currentCityId)
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
        val questId = playerState.activeQuestId ?: error("Brak aktywnego zadania")
        val quest = questEngine.getDefinition(questId) ?: error("Nieznane zadanie: $questId")

        // Bug #3: jedz do celu aktualnego KROKU, nie do miasta rdowego questa.
        // quest.cityId = miasto, gdzie mozna quest przyjac (origin)
        // QuestStep.targetId = gdzie gracz powinien teraz pojechac
        val state = gameRepository.currentState()
        val progress = state.quest.progress[questId]
        val currentStep = quest.steps.getOrNull(progress?.currentStepIndex ?: 0)

        val destinationCity = if (
            currentStep != null &&
            currentStep.type == StepType.EXPEDITION &&
            currentStep.targetId.isNotBlank()
        ) {
            currentStep.targetId
        } else {
            // Fallback: jedz do oryginalnego miasta questa (np. do turn-in NPC)
            quest.cityId
        }

        Log.i(TAG, "Podroz do celu: $destinationCity (quest=$questId, step=${progress?.currentStepIndex ?: 0})")

        if (playerState.currentCityId != destinationCity) {
            travelSystem.travelTo(destinationCity)
        }

        val updatedPlayer = playerState.copy(currentCityId = destinationCity)

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

    fun resolveActiveQuest(playerState: PlayerState): PlayerState {
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
