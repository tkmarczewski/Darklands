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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DialogueUiState(
    val currentNode: DialogueNode? = null,
    val npcName: String = "",
    val npcRole: String = "",
    val npcPortrait: String = "",
    val backgroundDrawable: String = "",
    val availableChoices: List<Pair<DialogueChoice, Boolean>> = emptyList(),
    val worldStability: Int = 100
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
                val npcName = state.pendingDialogueNpcName ?: ""
                val npcRole = state.pendingDialogueNpcRole ?: ""
                val nodeId = state.pendingDialogueNodeId ?: "start"
                
                val cityId = state.grimCurrentRegion
                val city = cityCatalogue.get(cityId)
                
                val node = dialogueManager.getNode(nodeId)
                val choices = node?.choices?.map { choice ->
                    choice to checkRequirements(choice, state)
                } ?: emptyList()

                _uiState.update { 
                    it.copy(
                        currentNode = node,
                        npcName = npcName,
                        npcRole = npcRole,
                        npcPortrait = "port_knight", // Default for now
                        backgroundDrawable = city?.backgroundDrawable ?: "bg_generic_city",
                        availableChoices = choices,
                        worldStability = state.world.globalStability
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkRequirements(choice: DialogueChoice, state: GameState): Boolean {
        // Logic check: e.g. gold, items, quest status
        if (choice.requiredReputation > 0) {
            // Check global or city rep?
        }
        return true
    }

    fun choose(choice: DialogueChoice) {
        gameRepository.updateState { state ->
            state.pendingDialogueNodeId = choice.targetNodeId
            
            // Execute logic-level effects defined in the choice
            choice.onSelect(state)
            
            if (choice.targetNodeId == "end") {
                state.pendingDialogueNpcName = null
                state.pendingDialogueNpcRole = null
                state.pendingDialogueNodeId = null
            }
        }
    }
}
