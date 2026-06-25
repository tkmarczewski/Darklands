package com.grimreich.ui.dialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestStatus
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
    private val questSystem: QuestSystem,
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
        val gameState = gameRepository.currentState()
        val currentCityId = gameState.grimCurrentRegion
        val city = cityCatalogue.get(currentCityId)
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
        if (choice.requiredAttributes.isEmpty() && choice.requiredSkills.isEmpty() && choice.factionId == null) return true
        
        val hero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull() ?: return false
        
        // Check attributes
        choice.requiredAttributes.forEach { (attr, value) ->
            val heroValue = when (attr.lowercase()) {
                "strength", "siła" -> hero.strength
                "agility", "zwinność" -> hero.agility
                "perception", "percepcja", "postrzeganie" -> hero.perception
                "intelligence", "inteligencja" -> hero.intelligence
                "endurance", "wytrzymałość" -> hero.endurance
                "charisma", "charyzma" -> hero.charisma
                "piety", "pobożność" -> hero.piety
                else -> 0
            }
            if (heroValue < value) return false
        }
        
        // Check skills
        choice.requiredSkills.forEach { (skill, value) ->
            if ((hero.skills[skill] ?: 0) < value) return false
        }

        // Check reputation
        if (choice.factionId != null) {
            val rep = state.reputation.cityFactions[state.grimCurrentRegion]?.get(choice.factionId) ?: 0
            if (rep < choice.requiredReputation) return false
        }

        return true
    }

    fun choose(choice: DialogueChoice) {
        val state = gameRepository.currentState()
        if (!checkRequirements(choice, state)) return

        choice.onSelect(state)
        val nextNode = dialogueManager.getNode(choice.targetNodeId)
        
        _uiState.update { 
            it.copy(
                currentNode = nextNode,
                availableChoices = nextNode?.choices?.map { c ->
                    c to checkRequirements(c, state)
                } ?: emptyList()
            )
        }
        
        if (choice.targetNodeId == "end" || nextNode == null) {
            // Handle quest activation/completion from dialogue
            state.pendingQuestId?.let { cmd ->
                when {
                    cmd.startsWith("COMPLETE:") -> {
                        val qId = cmd.removePrefix("COMPLETE:")
                        state.quest.activeQuests.remove(qId)
                        if (!state.quest.completedQuests.contains(qId)) {
                            state.quest.completedQuests.add(qId)
                            gameRepository.log("Zadanie ukończone: $qId")
                        }
                    }
                    cmd.startsWith("FINALIZE:") -> {
                        val qId = cmd.removePrefix("FINALIZE:")
                        // Call the explicit system complete to handle gold reward
                        questSystem.complete(qId)
                    }
                    else -> {
                        if (!state.quest.activeQuests.contains(cmd)) {
                            state.quest.activeQuests.add(cmd)
                            gameRepository.log("Nowe zadanie aktywowane: $cmd")
                        }
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
