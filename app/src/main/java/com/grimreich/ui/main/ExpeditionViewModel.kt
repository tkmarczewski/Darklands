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
    val encounterLog: String? = null,
    val raidCombatData: Pair<String, Triple<String, Int, Int>>? = null,
    val activeStepInfo: String? = null
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
                        activeEncounter = encounterSystem.activeEncounter,
                        activeStepInfo = state.pendingQuestId?.let { qId ->
                            if (qId.startsWith("COMBAT_WIN:")) {
                                val actualQId = qId.removePrefix("COMBAT_WIN:")
                                expeditionManager.getStepInfo(actualQId)
                            } else null
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
            
        // When entering expedition screen, mark as active
        enterExpedition()
    }

    private fun enterExpedition() {
        val currentS = gameRepository.currentState()
        // Ensure activeHeroId is set
        if (currentS.activeHeroId == null) {
            val firstAlive = currentS.party.firstOrNull { !it.isDead }
            if (firstAlive != null) {
                gameRepository.updateState { it.activeHeroId = firstAlive.id }
            }
        }

        gameRepository.updateState { it.isExpeditionActive = true }
        // Roll for random encounter on enter
        val random = kotlin.random.Random.Default
        val stateForRoll = gameRepository.currentState()
        if (random.nextFloat() < 0.4f) {
            val encounter = encounterSystem.rollEncounter(random, stateForRoll)
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
                "charisma" -> hero.charisma
                else -> 0
            }
            if (value < choice.requiredValue) {
                _uiState.update { it.copy(encounterLog = "Wymagane ${choice.requiredAttribute} ${choice.requiredValue}. Twój wynik: $value") }
                return
            }
        }

        val msg = choice.effect(state)
        
        // Special case for raids
        if (msg.startsWith("POJEDYNEK:")) {
            val parts = msg.split(":")
            val name = parts[1]
            val hp = parts[2].toInt()
            val atk = parts[3].toInt()
            
            encounterSystem.activeEncounter = null
            gameRepository.updateState { it.pendingQuestId = "RAID:$name:$hp:$atk" }
            _uiState.update { 
                it.copy(
                    activeEncounter = null,
                    raidCombatData = "RAID" to Triple(name, hp, atk)
                ) 
            }
        } else {
            gameRepository.log("Wydarzenie: ${choice.description} -> $msg")
            encounterSystem.activeEncounter = null
            gameRepository.persistCurrentState()
            _uiState.update { it.copy(activeEncounter = null, encounterLog = msg) }
        }
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
        
        // CRITICAL FIX: Update state to trigger Combat before calling onStart
        if (result is com.grimreich.systems.ExpeditionResult.StartCombat) {
            gameRepository.updateState { 
                it.pendingQuestId = "COMBAT_WIN:${quest.id}"
                // Ensure combat state is initialized in repo
                it.combat.active = true
                it.combat.enemyName = result.enemyName
                it.combat.enemyHp = result.enemyHp
                it.combat.enemyMaxHp = result.enemyHp
                it.combat.enemyAttack = result.enemyAtk
                it.combat.enemyDefense = result.enemyDef
            }
        }

        processExpeditionResult(result, onStart)
    }

    fun completeNonCombatQuest(quest: QuestEntry, onNext: () -> Unit) {
        val startResult = expeditionManager.startQuest(quest.id)
        if (startResult is com.grimreich.systems.ExpeditionResult.StartInvestigation || 
            startResult is com.grimreich.systems.ExpeditionResult.StartDialogue ||
            startResult is com.grimreich.systems.ExpeditionResult.Travel) {
            
            // For now, auto-resolve non-combat steps with success
            val finishResult = expeditionManager.onStepFinished(quest.id, success = true)
            processExpeditionResult(finishResult, onNext)
        } else {
             processExpeditionResult(startResult, onNext)
        }
    }

    private fun processExpeditionResult(result: com.grimreich.systems.ExpeditionResult, onUIAction: () -> Unit) {
        when (result) {
            is com.grimreich.systems.ExpeditionResult.StartCombat -> {
                // Bridge back the quest ID for completion after victory
                gameRepository.updateState { it.pendingQuestId = "COMBAT_WIN:${result.enemyId}" }
                onUIAction()
            }
            is com.grimreich.systems.ExpeditionResult.QuestCompleted -> {
                gameRepository.log("Zadanie wykonane: ${result.questId}")
                gameRepository.persistCurrentState()
                onUIAction()
            }
            is com.grimreich.systems.ExpeditionResult.Error -> {
                gameRepository.log("Błąd ekspedycji: ${result.message}")
            }
            else -> {
                // If it's another step (dialogue/travel), we might need to handle it.
                // For "Wyrok", investigations are currently logic-only.
                val finishResult = expeditionManager.onStepFinished(gameRepository.currentState().pendingQuestId ?: "", success = true)
                if (finishResult is com.grimreich.systems.ExpeditionResult.QuestCompleted) {
                     gameRepository.log("Cel wyprawy osiągnięty.")
                }
            }
        }
    }

    fun questHasCombat(quest: QuestEntry): Boolean = quest.hasCombat

    override fun onCleared() {
        super.onCleared()
        // Defensive cleanup if user leaves via other means
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
