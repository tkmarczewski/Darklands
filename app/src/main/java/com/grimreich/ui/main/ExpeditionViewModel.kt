package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
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
import kotlin.random.Random

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
                
                // Logic fix: Roll for random encounters when entering or updating
                if (encounterSystem.activeEncounter == null && _uiState.value.encounterLog == null) {
                    val rolled = encounterSystem.rollEncounter(Random(System.currentTimeMillis()), state)
                    if (rolled != null) {
                        encounterSystem.selectEncounter(rolled)
                        _uiState.update { it.copy(activeEncounter = rolled) }
                    }
                }
            }
            .launchIn(viewModelScope)
            
        gameRepository.updateState { it.isExpeditionActive = true }
    }

    fun startQuest(questId: String, onCombat: () -> Unit) {
        val def = questEngine.getDefinition(questId) ?: return
        
        var shouldCombat = false
        gameRepository.updateState { state ->
            val progress = state.quest.progress[questId] ?: return@updateState
            val step = def.steps.getOrNull(progress.currentStepIndex) ?: return@updateState
            
            when (step.type) {
                StepType.COMBAT -> {
                    state.pendingQuestId = "COMBAT_WIN:$questId"
                    state.combat.active = true
                    state.combat.enemyName = step.targetId // Logic fix: Use step target
                    state.combat.enemyHp = 60
                    state.combat.enemyMaxHp = 60
                    shouldCombat = true
                }
                else -> {
                    questEngine.advanceStep(questId)
                    state.logEntries.add("Postęp w zadaniu: ${def.title}")
                }
            }
        }
        
        if (shouldCombat) onCombat()
    }

    fun dismissEncounter() {
        encounterSystem.activeEncounter = null
        _uiState.update { it.copy(activeEncounter = null, encounterLog = null) }
    }

    fun handleEncounterChoice(choice: EncounterChoice) {
        var msg = ""
        gameRepository.updateState { state ->
            msg = choice.effect(state)
        }
        
        // Logic fix: Parse POJEDYNEK string to trigger combat
        if (msg.startsWith("POJEDYNEK:")) {
            val parts = msg.split(":")
            if (parts.size >= 4) {
                gameRepository.updateState { state ->
                    state.combat.active = true
                    state.combat.enemyName = parts[1]
                    state.combat.enemyHp = parts[2].toIntOrNull() ?: 50
                    state.combat.enemyMaxHp = state.combat.enemyHp
                    state.combat.enemyAttack = parts[3].toIntOrNull() ?: 10
                }
                // Transition to combat will be handled by UI observer or explicit call
                // For now we set encounter log to notify the user
                _uiState.update { it.copy(encounterLog = "Rozpoczyna się starcie: ${parts[1]}!", activeEncounter = null) }
            }
        } else {
            encounterSystem.activeEncounter = null
            _uiState.update { it.copy(encounterLog = msg, activeEncounter = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
