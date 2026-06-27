package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.QuestStatus
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestDefinition
import com.grimreich.systems.StepType
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.EncounterSystem
import com.grimreich.systems.Encounter
import com.grimreich.systems.EncounterChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ExpeditionUiState(
    val regionName: String = "",
    val activeQuests: List<QuestDefinition> = emptyList(),
    val activeEncounter: Encounter? = null,
    val encounterLog: String? = null
)

@HiltViewModel
class ExpeditionViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val encounterSystem: EncounterSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpeditionUiState())
    val uiState: StateFlow<ExpeditionUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                val cityId = state.grimCurrentRegion
                val city = cityCatalogue.get(cityId)
                val quests = questEngine.getActiveQuestsForCity(cityId)

                _uiState.update { 
                    it.copy(
                        regionName = city?.name ?: "Pogranicze",
                        activeQuests = quests,
                        activeEncounter = encounterSystem.activeEncounter
                    )
                }
            }
            .launchIn(viewModelScope)
            
        gameRepository.updateState { it.isExpeditionActive = true }
    }

    fun startQuest(questId: String, onCombat: () -> Unit) {
        val def = questEngine.getDefinition(questId) ?: return
        val state = gameRepository.currentState()
        val progress = state.quest.progress[questId] ?: return
        
        val step = def.steps.getOrNull(progress.currentStepIndex) ?: return
        
        when (step.type) {
            com.grimreich.systems.StepType.COMBAT -> {
                gameRepository.updateState { s ->
                    s.pendingQuestId = "COMBAT_WIN:$questId"
                    s.combat.active = true
                    s.combat.enemyName = "Abominacja questa"
                    s.combat.enemyHp = 60
                    s.combat.enemyMaxHp = 60
                }
                onCombat()
            }
            else -> {
                questEngine.advanceStep(questId)
                gameRepository.log("Postęp w zadaniu: ${def.title}")
            }
        }
    }

    fun dismissEncounter() {
        encounterSystem.activeEncounter = null
        _uiState.update { it.copy(activeEncounter = null) }
    }

    fun handleEncounterChoice(choice: EncounterChoice) {
        val state = gameRepository.currentState()
        val msg = choice.effect(state)
        encounterSystem.activeEncounter = null
        _uiState.update { it.copy(encounterLog = msg) }
    }

    override fun onCleared() {
        super.onCleared()
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
