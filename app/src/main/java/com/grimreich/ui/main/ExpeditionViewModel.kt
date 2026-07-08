package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.CombatRandomProvider
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
    private val encounterSystem: EncounterSystem,
    private val combatSystem: com.grimreich.systems.CombatSystem,
    private val random: CombatRandomProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpeditionUiState())
    val uiState: StateFlow<ExpeditionUiState> = _uiState.asStateFlow()

    private var hasRolledForCurrentVisit = false

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
                
                // Deterministic and controlled encounter roll
                if (!hasRolledForCurrentVisit && encounterSystem.activeEncounter == null && _uiState.value.encounterLog == null) {
                    hasRolledForCurrentVisit = true
                    val rolled = encounterSystem.rollEncounter(random, state)
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
        var enemyType: com.grimreich.core.EnemyType? = null

        gameRepository.updateState { state ->
            val progress = state.quest.progress[questId] ?: return@updateState
            val step = def.steps.getOrNull(progress.currentStepIndex) ?: return@updateState
            
            when (step.type) {
                StepType.COMBAT -> {
                    state.pendingQuestId = "COMBAT_WIN:$questId"
                    try {
                        enemyType = com.grimreich.core.EnemyType.valueOf(step.targetId)
                        shouldCombat = true
                    } catch (e: Exception) {
                        // Fallback if targetId is not a valid enum name
                        state.combat.active = true
                        state.combat.enemyName = step.targetId
                        state.combat.enemyHp = 60
                        state.combat.enemyMaxHp = 60
                        shouldCombat = true
                    }
                }
                else -> {
                    questEngine.advanceStepDirect(state, questId)
                    state.logEntries.add("Postęp w zadaniu: ${def.title}")
                }
            }
        }
        
        if (shouldCombat) {
            enemyType?.let { type ->
                val enemy = com.grimreich.core.Bestiary.get(type)
                combatSystem.startCombat(enemy)
            }
            onCombat()
        }
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
        
        if (choice.combatEnemyType != null) {
            val enemy = com.grimreich.core.Bestiary.get(choice.combatEnemyType)
            combatSystem.startCombat(enemy)
            _uiState.update { it.copy(encounterLog = "Rozpoczyna się starcie: ${enemy.name}!", activeEncounter = null) }
        } else if (msg.startsWith("POJEDYNEK:")) {
            // Legacy handling for any remaining string-based combat triggers
            val parts = msg.split(":")
            if (parts.size >= 2) {
                _uiState.update { it.copy(encounterLog = "Rozpoczyna się starcie: ${parts[1]}!", activeEncounter = null) }
            }
        } else {
            encounterSystem.clearActiveEncounter()
            _uiState.update { it.copy(encounterLog = msg, activeEncounter = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
