package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.CombatRandomProvider
import com.grimreich.core.StepType
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestDefinition
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.EncounterSystem
import com.grimreich.systems.Encounter
import com.grimreich.systems.EncounterChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ExpeditionUiEvent {
    data class OnQuestClick(val questId: String) : ExpeditionUiEvent
    data class OnEncounterChoiceClick(val choice: EncounterChoice) : ExpeditionUiEvent
    data object OnDismissEncounter : ExpeditionUiEvent
    data object OnBackClick : ExpeditionUiEvent
}

sealed interface ExpeditionUiEffect {
    data object NavigateToCombat : ExpeditionUiEffect
    data object NavigateToDialogue : ExpeditionUiEffect
    data object NavigateBack : ExpeditionUiEffect
}

sealed interface ExpeditionContentState {
    data object Loading : ExpeditionContentState
    data class QuestList(val quests: List<QuestDefinition>) : ExpeditionContentState
    data class EncounterActive(val encounter: Encounter) : ExpeditionContentState
    data class EncounterLog(val message: String) : ExpeditionContentState
}

data class ExpeditionUiState(
    val regionName: String = "",
    val content: ExpeditionContentState = ExpeditionContentState.Loading,
    val canLeave: Boolean = true
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

    private val _uiEffect = MutableSharedFlow<ExpeditionUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var hasRolledForCurrentVisit = false

    init {
        gameRepository.gameState
            .onEach { state -> updateUiState(state) }
            .launchIn(viewModelScope)
            
        gameRepository.updateState { it.isExpeditionActive = true }
    }

    fun onEvent(event: ExpeditionUiEvent) {
        when (event) {
            is ExpeditionUiEvent.OnQuestClick -> startQuest(event.questId)
            is ExpeditionUiEvent.OnEncounterChoiceClick -> handleEncounterChoice(event.choice)
            ExpeditionUiEvent.OnDismissEncounter -> dismissEncounter()
            ExpeditionUiEvent.OnBackClick -> {
                gameRepository.updateState { it.isExpeditionActive = false }
                emitEffect(ExpeditionUiEffect.NavigateBack)
            }
        }
    }

    private fun emitEffect(effect: ExpeditionUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    private fun updateUiState(state: GameState) {
        val cityId = state.world.locationId
        val city = cityCatalogue.get(cityId)
        val quests = questEngine.getActiveQuestsForCity(cityId)
        val activeEncounter = encounterSystem.activeEncounter

        // Deterministic encounter roll logic
        if (!hasRolledForCurrentVisit && activeEncounter == null && _uiState.value.content !is ExpeditionContentState.EncounterLog) {
            hasRolledForCurrentVisit = true
            val rolled = encounterSystem.rollEncounter(random, state)
            if (rolled != null) {
                encounterSystem.selectEncounter(rolled)
                _uiState.update { 
                    it.copy(
                        regionName = city?.name ?: "Pogranicze",
                        content = ExpeditionContentState.EncounterActive(rolled),
                        canLeave = false
                    )
                }
                return
            }
        }

        val content = when {
            activeEncounter != null -> ExpeditionContentState.EncounterActive(activeEncounter)
            _uiState.value.content is ExpeditionContentState.EncounterLog -> _uiState.value.content
            else -> ExpeditionContentState.QuestList(quests)
        }

        _uiState.update { 
            it.copy(
                regionName = city?.name ?: "Pogranicze",
                content = content,
                canLeave = activeEncounter == null
            )
        }
    }

    private fun startQuest(questId: String) {
        val def = questEngine.getDefinition(questId) ?: return
        
        var shouldCombat = false
        var shouldDialogue = false
        var enemyType: com.grimreich.core.EnemyType? = null

        gameRepository.updateState { state ->
            val progress = state.quest.progress[questId] ?: return@updateState
            val step = def.steps.getOrNull(progress.currentStepIndex) ?: return@updateState
            
            when (step.type) {
                StepType.COMBAT -> {
                    state.pendingAction = com.grimreich.core.PendingWorldAction.QuestCombatWin(questId)
                    val tid = step.targetId.trim().uppercase()
                    enemyType = try {
                        com.grimreich.core.EnemyType.valueOf(tid)
                    } catch (e: Exception) {
                        com.grimreich.core.EnemyType.BANDIT
                    }
                    shouldCombat = true
                }
                StepType.DIALOGUE -> {
                    state.pendingAction = com.grimreich.core.PendingWorldAction.Dialogue(
                        npcName = "Kontakt",
                        npcRole = def.originNpcId,
                        nodeId = step.targetId,
                        relatedQuestId = questId
                    )
                    shouldDialogue = true
                }
                StepType.INVESTIGATION -> {
                    val currentCityId = state.world.locationId
                    if (currentCityId == def.cityId) {
                        questEngine.advanceStepDirect(state, questId)
                        val msg = "Zbadano cel: ${step.targetId}. Cel zadania został osiągnięty."
                        state.logEntries.add(msg)
                        _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog(msg)) }
                    } else {
                        val msg = "Zbyt daleko od celu. Musisz wrócić do ${def.cityId}."
                        state.logEntries.add(msg)
                        _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog(msg)) }
                    }
                }
                StepType.SOCIAL -> {
                    questEngine.advanceStepDirect(state, questId)
                    val msg = "Interakcja społeczna w ${step.targetId} zakończona sukcesem."
                    state.logEntries.add(msg)
                    _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog(msg)) }
                }
                StepType.META -> {
                    questEngine.advanceStepDirect(state, questId)
                    val msg = "Zrozumiano ontologiczny aspekt: ${step.targetId}. Ledger zaktualizowany."
                    state.logEntries.add(msg)
                    _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog(msg)) }
                }
                StepType.EXPEDITION -> {
                    val currentCityId = state.world.locationId
                    if (currentCityId == step.targetId) {
                        questEngine.advanceStepDirect(state, questId)
                        state.logEntries.add("Dotarto do celu ekspedycji: ${step.targetId}.")
                    } else {
                        val msg = "Musisz udać się do: ${step.targetId}, aby kontynuować to zadanie."
                        state.logEntries.add(msg)
                        _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog(msg)) }
                    }
                }
            }
        }
        
        if (shouldCombat) {
            enemyType?.let { type ->
                val enemy = com.grimreich.core.Bestiary.get(type)
                combatSystem.startCombat(enemy)
                emitEffect(ExpeditionUiEffect.NavigateToCombat)
            }
        } else if (shouldDialogue) {
            emitEffect(ExpeditionUiEffect.NavigateToDialogue)
        }
    }

    private fun dismissEncounter() {
        encounterSystem.activeEncounter = null
        _uiState.update { it.copy(content = ExpeditionContentState.QuestList(emptyList())) }
        // updateUiState will handle refreshing the quest list in the next flow emission
    }

    private fun handleEncounterChoice(choice: EncounterChoice) {
        var msg = ""
        gameRepository.updateState { state ->
            msg = choice.effect(state)
        }
        
        if (choice.combatEnemyType != null) {
            val enemy = com.grimreich.core.Bestiary.get(choice.combatEnemyType)
            combatSystem.startCombat(enemy)
            _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog("Rozpoczyna się starcie: ${enemy.name}!")) }
            emitEffect(ExpeditionUiEffect.NavigateToCombat)
        } else {
            encounterSystem.clearActiveEncounter()
            _uiState.update { it.copy(content = ExpeditionContentState.EncounterLog(msg)) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
