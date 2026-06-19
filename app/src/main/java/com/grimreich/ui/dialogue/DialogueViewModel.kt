package com.grimreich.ui.dialogue

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.DialogueManager
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        val state = gameRepository.currentState()
        if (state.pendingDialogueNodeId != null) {
            init(
                state.pendingDialogueNpcName ?: "Nieznajomy",
                state.pendingDialogueNpcRole ?: "Cień",
                state.pendingDialogueNodeId!!
            )
        }
    }

    fun init(npcName: String, npcRole: String, startNodeId: String) {
        val currentCityId = gameRepository.currentState().grimCurrentRegion
        val city = cityCatalogue.get(currentCityId)
        
        _uiState.update { 
            it.copy(
                npcName = npcName,
                npcRole = npcRole,
                backgroundDrawable = city?.backgroundDrawable ?: "bg_region_north_coast",
                currentNode = dialogueManager.getNode(startNodeId)
            )
        }
    }

    fun choose(choice: DialogueChoice) {
        choice.onSelect(gameRepository.currentState())
        val nextNode = dialogueManager.getNode(choice.targetNodeId)
        _uiState.update { it.copy(currentNode = nextNode) }
        gameRepository.persistCurrentState()
    }
}
