package com.grimreich.ui.dialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.DialogueManager
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogueUiState(
    val currentNode: DialogueNode? = null,
    val npcName: String = "",
    val npcRole: String = "",
    val backgroundDrawable: String = "bg_region_north_coast"
)

@HiltViewModel
class DialogueViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dialogueManager: DialogueManager,
    private val cityCatalogue: CityCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(DialogueUiState())
    val uiState: StateFlow<DialogueUiState> = _uiState.asStateFlow()

    init {
        // Observe pending dialogue state to make the VM reactive
        viewModelScope.launch {
            gameRepository.currentState().let { state ->
                // Initial load
                if (state.pendingDialogueNodeId != null) {
                    refresh(
                        state.pendingDialogueNpcName ?: "Nieznajomy",
                        state.pendingDialogueNpcRole ?: "Cień",
                        state.pendingDialogueNodeId!!
                    )
                }
            }
        }
    }

    private fun refresh(npcName: String, npcRole: String, nodeId: String) {
        val currentCityId = gameRepository.currentState().grimCurrentRegion
        val city = cityCatalogue.get(currentCityId)
        
        _uiState.update { 
            it.copy(
                npcName = npcName,
                npcRole = npcRole,
                backgroundDrawable = city?.backgroundDrawable ?: "bg_region_north_coast",
                currentNode = dialogueManager.getNode(nodeId)
            )
        }
    }

    fun choose(choice: DialogueChoice) {
        choice.onSelect(gameRepository.currentState())
        val nextNode = dialogueManager.getNode(choice.targetNodeId)
        _uiState.update { it.copy(currentNode = nextNode) }
        
        if (choice.targetNodeId == "end" || nextNode == null) {
            val state = gameRepository.currentState()
            state.pendingDialogueNodeId = null
            state.pendingDialogueNpcName = null
            state.pendingDialogueNpcRole = null
        }

        gameRepository.persistCurrentState()
    }
}
