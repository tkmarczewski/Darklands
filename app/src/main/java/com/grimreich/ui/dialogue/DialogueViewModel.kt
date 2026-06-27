package com.grimreich.ui.dialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.QuestStatus
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestEngine
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DialogueUiState(
    val currentNode: DialogueNode? = null,
    val npcName: String = "",
    val npcRole: String = "",
    val npcPortrait: String = "port_rogue",
    val backgroundDrawable: String = "bg_region_north_coast",
    val availableChoices: List<Pair<DialogueChoice, Boolean>> = emptyList()
)

@HiltViewModel
class DialogueViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dialogueManager: DialogueManager,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(DialogueUiState())
    val uiState: StateFlow<DialogueUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                if (state.pendingDialogueNodeId != null) {
                    refresh(
                        state.pendingDialogueNpcName ?: "Nieznajomy",
                        state.pendingDialogueNpcRole ?: "Cień",
                        state.pendingDialogueNodeId!!
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refresh(npcName: String, npcRole: String, nodeId: String) {
        val gameState = gameRepository.currentState()
        val city = cityCatalogue.get(gameState.grimCurrentRegion)
        val node = dialogueManager.getNode(nodeId)
        
        _uiState.update { 
            it.copy(
                npcName = npcName,
                npcRole = npcRole,
                npcPortrait = dialogueManager.getPortrait(npcRole),
                backgroundDrawable = city?.backgroundDrawable ?: "bg_region_north_coast",
                currentNode = node,
                availableChoices = node?.choices?.map { choice ->
                    choice to checkRequirements(choice, gameState)
                } ?: emptyList()
            )
        }
    }

    private fun checkRequirements(choice: DialogueChoice, state: GameState): Boolean {
        choice.requiredAttributes["gold"]?.let { if (state.gold < it) return false }
        return true
    }

    fun choose(choice: DialogueChoice) {
        val state = gameRepository.currentState()
        choice.onSelect(state)
        
        val nextNode = dialogueManager.getNode(choice.targetNodeId)
        if (nextNode != null) {
            _uiState.update { it.copy(currentNode = nextNode) }
        } else {
            // Dialogue End - Clear pointers
            gameRepository.updateState { 
                it.pendingDialogueNodeId = null
                it.pendingDialogueNpcName = null
                it.pendingDialogueNpcRole = null
            }
        }
        gameRepository.persistCurrentState()
    }
}
