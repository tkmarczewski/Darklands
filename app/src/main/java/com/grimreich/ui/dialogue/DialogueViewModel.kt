package com.grimreich.ui.dialogue

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.DialogueManager
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DialogueUiState(
    val currentNode: DialogueNode? = null,
    val npcName: String = "",
    val npcRole: String = "",
    val backgroundDrawable: String = "bg_region_north_coast"
)

class DialogueViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DialogueUiState())
    val uiState: StateFlow<DialogueUiState> = _uiState.asStateFlow()

    fun init(npcName: String, npcRole: String, startNodeId: String) {
        val currentCityId = GameRepository.state.grimCurrentRegion
        val city = com.grimreich.world.CityCatalogue.get(currentCityId ?: "")
        
        _uiState.update { 
            it.copy(
                npcName = npcName,
                npcRole = npcRole,
                backgroundDrawable = city?.backgroundDrawable ?: "bg_region_north_coast",
                currentNode = DialogueManager.getNode(startNodeId)
            )
        }
    }

    fun choose(choice: DialogueChoice) {
        choice.onSelect(GameRepository.state)
        val nextNode = DialogueManager.getNode(choice.targetNodeId)
        _uiState.update { it.copy(currentNode = nextNode) }
    }
}
