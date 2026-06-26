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
            val cmd = state.pendingQuestId
            
            // CRITICAL FIX: Clear dialogue pointers FIRST to prevent re-triggering logic
            gameRepository.updateState { 
                it.pendingDialogueNodeId = null
                it.pendingDialogueNpcName = null
                it.pendingDialogueNpcRole = null
                it.pendingQuestId = null 
            }
            
            cmd?.let { c ->
                when {
                    c.startsWith("COMPLETE:") -> {
                        val qId = c.removePrefix("COMPLETE:")
                        state.quest.activeQuests.remove(qId)
                        if (!state.quest.completedQuests.contains(qId)) {
                            state.quest.completedQuests.add(qId)
                            gameRepository.log("Zadanie ukończone: $qId")
                        }
                    }
                    c.startsWith("FINALIZE:") -> {
                        val qId = c.removePrefix("FINALIZE:")
                        // Call the explicit system complete to handle gold reward
                        questSystem.complete(qId)
                    }
                    c.startsWith("RECRUIT:") -> {
                        val heroType = c.removePrefix("RECRUIT:")
                        val hero = createRecruitedHero(heroType)
                        state.party.add(hero)
                        gameRepository.log("Bohater zrekrutowany: ${hero.name}")
                    }
                    else -> {
                        if (!state.quest.activeQuests.contains(c)) {
                            state.quest.activeQuests.add(c)
                            gameRepository.log("Nowe zadanie aktywowane: $c")
                        }
                    }
                }
            }

            state.pendingDialogueNodeId = null
            state.pendingDialogueNpcName = null
            state.pendingDialogueNpcRole = null
        }

        gameRepository.persistCurrentState()
    }
    
    override fun onCleared() {
        super.onCleared()
        gameRepository.updateState { 
            it.pendingDialogueNodeId = null
            it.pendingDialogueNpcName = null
            it.pendingDialogueNpcRole = null
        }
    }

    private fun createRecruitedHero(type: String): com.grimreich.core.Hero {
        return when (type.lowercase()) {
            "mira" -> com.grimreich.core.Hero(
                id = "hero_mira", name = "Mira Wieloznaczna", age = 120, strength = 10, agility = 14, 
                perception = 18, intelligence = 20, endurance = 12, charisma = 15, piety = 10,
                hp = 60, maxHp = 60, portraitRes = "port_alchemist"
            )
            "ferrun" -> com.grimreich.core.Hero(
                id = "hero_ferrun", name = "Ferrun Żelazny", age = 85, strength = 22, agility = 8, 
                perception = 10, intelligence = 12, endurance = 25, charisma = 8, piety = 15,
                hp = 120, maxHp = 120, portraitRes = "port_barbarian"
            )
            "noctyros" -> com.grimreich.core.Hero(
                id = "hero_noctyros", name = "Noctyros", age = 0, strength = 15, agility = 18, 
                perception = 20, intelligence = 25, endurance = 15, charisma = 10, piety = 5,
                hp = 80, maxHp = 80, portraitRes = "port_demon"
            )
            "aelion" -> com.grimreich.core.Hero(
                id = "hero_aelion", name = "Prorok Aelion", age = 300, strength = 8, agility = 10, 
                perception = 15, intelligence = 18, endurance = 12, charisma = 20, piety = 25,
                hp = 70, maxHp = 70, portraitRes = "port_priest"
            )
            else -> com.grimreich.core.Hero(
                id = "hero_gen_${java.util.UUID.randomUUID()}", name = "Wysłannik", age = 30, hp = 40, maxHp = 40
            )
        }
    }
}
