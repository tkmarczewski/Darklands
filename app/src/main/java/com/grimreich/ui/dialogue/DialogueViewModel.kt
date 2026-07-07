package com.grimreich.ui.dialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestEngine
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.CombatSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogueUiState(
    val currentNode: DialogueNode? = null,
    val npcName: String = "",
    val npcRole: String = "",
    val npcPortrait: String = "port_peasant",
    val backgroundDrawable: String = "bg_city_default",
    val availableChoices: List<Pair<DialogueChoice, Boolean>> = emptyList(),
    val worldStability: Int = 100
)

@HiltViewModel
class DialogueViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dialogueManager: DialogueManager,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val combatSystem: CombatSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(DialogueUiState())
    val uiState: StateFlow<DialogueUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.gameState.collect { state ->
                val nodeId = state.pendingDialogueNodeId ?: "start"
                val node = dialogueManager.getNode(nodeId)
                
                val currentCity = cityCatalogue.get(state.grimCurrentRegion)
                
                val choicesWithAvailability = node?.choices?.map { choice ->
                    choice to checkRequirements(choice, state)
                } ?: emptyList()

                _uiState.value = DialogueUiState(
                    currentNode = node?.let { dialogueManager.applyWorldEffects(it, state.world.globalStability) },
                    npcName = state.pendingDialogueNpcName ?: "Nieznajomy",
                    npcRole = state.pendingDialogueNpcRole ?: "Mieszkaniec",
                    npcPortrait = dialogueManager.getPortrait(state.pendingDialogueNpcRole ?: ""),
                    backgroundDrawable = currentCity?.backgroundDrawable ?: "bg_city_default",
                    availableChoices = choicesWithAvailability,
                    worldStability = state.world.globalStability
                )
            }
        }
    }

    private fun checkRequirements(choice: DialogueChoice, state: GameState): Boolean {
        val hero = state.party.find { it.id == state.activeHeroId } ?: return false
        
        // Check attributes
        choice.requiredAttributes.forEach { (attr, value) ->
            val heroVal = when (attr.uppercase()) {
                "STR", "STRENGTH", "SIŁA" -> hero.strength
                "INT", "INTELLIGENCE", "INTELIGENCJA" -> hero.intelligence
                "AGI", "AGILITY", "ZRĘCZNOŚĆ" -> hero.agility
                "CHA", "CHARISMA", "CHARYZMA" -> hero.charisma
                "PIETY", "POBOŻNOŚĆ" -> hero.piety
                else -> 100 // Unknown attr -> auto-fail unless intentional
            }
            if (heroVal < value) return false
        }

        // Special quest requirements encoded in triggers (Legacy support)
        if (choice.triggerEvent == "QUEST_ACTIVE" && !state.quest.activeQuestIds.contains(choice.triggerValue)) return false
        if (choice.triggerEvent == "QUEST_COMPLETED" && !state.quest.completedQuestIds.contains(choice.triggerValue)) return false
        
        // Gold check (Convention: If triggerEvent is GOLD, triggerValue is amount)
        if (choice.triggerEvent == "REQUIRE_GOLD") {
            val amount = choice.triggerValue?.toIntOrNull() ?: 0
            if (state.gold < amount) return false
        }

        return true
    }

    fun choose(choice: DialogueChoice, onEnd: () -> Unit, onCombat: () -> Unit, onMarket: () -> Unit, onRitual: () -> Unit) {
        val state = gameRepository.currentState()
        
        // Handle triggers (quest advances, item giving, etc)
        dialogueManager.handleTrigger(state, choice.triggerEvent, choice.triggerValue)

        // SPECIAL TRANSITIONS
        if (choice.triggerEvent == "START_COMBAT" || choice.isCombatTrigger) {
            val enemyType = try { 
                com.grimreich.core.EnemyType.valueOf(choice.triggerValue ?: "BANDIT") 
            } catch (e: Exception) { 
                com.grimreich.core.EnemyType.BANDIT 
            }
            val enemy = com.grimreich.core.Bestiary.get(enemyType)
            if (enemy != null) {
                combatSystem.startCombat(enemy)
                gameRepository.updateState { it.pendingDialogueNodeId = null }
                onCombat()
                return
            }
        }

        if (choice.triggerEvent == "OPEN_MARKET") {
            onMarket()
            return
        }

        if (choice.triggerEvent == "OPEN_RITUAL") {
            onRitual()
            return
        }

        if (choice.targetNodeId == "end") {
            gameRepository.updateState { 
                it.pendingDialogueNodeId = null 
                it.pendingDialogueNpcName = null
                it.pendingDialogueNpcRole = null
            }
            onEnd()
        } else {
            gameRepository.updateState { 
                it.pendingDialogueNodeId = choice.targetNodeId
            }
        }
    }
}
