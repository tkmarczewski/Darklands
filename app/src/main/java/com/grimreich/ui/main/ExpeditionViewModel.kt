package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.EncounterSystem
import com.grimreich.systems.Encounter
import com.grimreich.systems.EncounterChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ExpeditionUiState(
    val regionName: String = "",
    val outsideQuests: List<QuestEntry> = emptyList(),
    val activeEncounter: Encounter? = null,
    val encounterLog: String? = null
)

@HiltViewModel
class ExpeditionViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val cityCatalogue: CityCatalogue,
    private val expeditionManager: com.grimreich.systems.ExpeditionManager,
    private val encounterSystem: EncounterSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpeditionUiState())
    val uiState: StateFlow<ExpeditionUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                val currentCityId = state.grimCurrentRegion
                val city = cityCatalogue.get(currentCityId)
                val activeOutside = state.quest.activeQuests
                    .mapNotNull { questSystem.getQuest(it) }
                    .filter { it.cityId == currentCityId && it.isOutsideCity }

                _uiState.update { 
                    it.copy(
                        regionName = city?.name ?: "Nieznana okolica",
                        outsideQuests = activeOutside,
                        activeEncounter = encounterSystem.activeEncounter
                    )
                }
            }
            .launchIn(viewModelScope)
            
        // When entering expedition screen, mark as active
        enterExpedition()
    }

    private fun enterExpedition() {
        gameRepository.updateState { it.isExpeditionActive = true }
        // Roll for random encounter on enter
        val random = kotlin.random.Random.Default
        if (random.nextFloat() < 0.4f) {
            val encounter = encounterSystem.rollEncounter(random)
            encounterSystem.activeEncounter = encounter
        }
    }

    fun handleEncounterChoice(choice: EncounterChoice) {
        val state = gameRepository.currentState()
        
        // Requirement check
        val hero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull()
        if (choice.requiredAttribute != null && hero != null) {
            val value = when(choice.requiredAttribute) {
                "perception" -> hero.perception
                "intelligence" -> hero.intelligence
                "strength" -> hero.strength
                else -> 0
            }
            if (value < choice.requiredValue) {
                _uiState.update { it.copy(encounterLog = "Wymagane ${choice.requiredAttribute} ${choice.requiredValue}. Twój wynik: $value") }
                return
            }
        }

        val msg = choice.effect(state)
        gameRepository.log("Wydarzenie: ${choice.description} -> $msg")
        encounterSystem.activeEncounter = null
        gameRepository.persistCurrentState()
        
        _uiState.update { it.copy(activeEncounter = null, encounterLog = msg) }
    }

    fun dismissEncounter() {
        encounterSystem.activeEncounter = null
        _uiState.update { it.copy(activeEncounter = null) }
    }

    fun exitExpedition(onBack: () -> Unit) {
        gameRepository.updateState { it.isExpeditionActive = false }
        onBack()
    }

    fun startQuestCombat(quest: QuestEntry, onStart: () -> Unit) {
        val result = expeditionManager.startQuest(quest.id)
        if (result is com.grimreich.systems.ExpeditionResult.StartCombat) {
            gameRepository.updateState { 
                it.pendingQuestId = "COMBAT_WIN:${quest.id}"
            }
            onStart()
        }
    }

    fun completeNonCombatQuest(quest: QuestEntry, onComplete: () -> Unit) {
        expeditionManager.startQuest(quest.id)
        val result = expeditionManager.onStepFinished(quest.id, success = true)
        if (result is com.grimreich.systems.ExpeditionResult.QuestCompleted) {
            gameRepository.log("Ukończono zadanie na wyprawie: ${quest.title}")
        }
        // Force refresh by notifying repository if needed, though updateState should do it
        gameRepository.persistCurrentState()
        onComplete()
    }

    fun questHasCombat(quest: QuestEntry): Boolean = quest.hasCombat

    override fun onCleared() {
        super.onCleared()
        // Defensive cleanup if user leaves via other means
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
