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
                        npcPortrait = dialogueManager.getPortrait(npcRole.lowercase()),
                        backgroundDrawable = city?.backgroundDrawable ?: "bg_generic_city",
                        availableChoices = choices,
                        worldStability = state.world.globalStability
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkRequirements(choice: DialogueChoice, state: GameState): Boolean {
        if (choice.requiredReputation > 0) {
            // TODO: check city/global reputation when rep system is wired up
        }
        return true
    }

    fun choose(choice: DialogueChoice) {
        // Extract quest-finalization flag BEFORE the synchronized updateState block
        // so we can call questEngine.completeQuest() outside the lock.
        var questToFinalize: String? = null

        gameRepository.updateState { state ->
            val pending = state.pendingQuestId
            if (pending != null && pending.startsWith("FINALIZE:")) {
                questToFinalize = pending.removePrefix("FINALIZE:")
                state.pendingQuestId = null
            }

            state.pendingDialogueNodeId = choice.targetNodeId
            choice.onSelect(state)

            if (choice.targetNodeId == "end") {
                state.pendingDialogueNpcName = null
                state.pendingDialogueNpcRole = null
                state.pendingDialogueNodeId = null
            }
        }

        // Must be called OUTSIDE the synchronized updateState block to avoid deadlock.
        // completeQuest() internally calls updateState() again.
        questToFinalize?.let { questEngine.completeQuest(it) }
    }
}
