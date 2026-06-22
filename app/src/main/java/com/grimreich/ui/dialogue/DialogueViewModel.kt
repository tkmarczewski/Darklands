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
        // Observe game state to react to NPC clicks
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
        val state = gameRepository.currentState()
        choice.onSelect(state)
        val nextNode = dialogueManager.getNode(choice.targetNodeId)
        _uiState.update { it.copy(currentNode = nextNode) }
        
        if (choice.targetNodeId == "end" || nextNode == null) {
            // Handle quest activation/completion from dialogue
            state.pendingQuestId?.let { cmd ->
                if (cmd.startsWith("COMPLETE:")) {
                    val qId = cmd.removePrefix("COMPLETE:")
                    state.quest.activeQuests.remove(qId)
                    if (!state.quest.completedQuests.contains(qId)) {
                        state.quest.completedQuests.add(qId)
                        gameRepository.log("Zadanie ukończone: $qId")
                    }
                } else {
                    if (!state.quest.activeQuests.contains(cmd)) {
                        state.quest.activeQuests.add(cmd)
                        gameRepository.log("Nowe zadanie aktywowane: $cmd")
                    }
                }
                state.pendingQuestId = null
            }

            state.pendingDialogueNodeId = null
            state.pendingDialogueNpcName = null
            state.pendingDialogueNpcRole = null
        }

        gameRepository.persistCurrentState()
    }
}
