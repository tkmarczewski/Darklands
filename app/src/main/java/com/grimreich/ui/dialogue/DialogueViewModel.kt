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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogueUiState(
    val currentNode: DialogueNode? = null,
    val npcName: String = "",
    val npcRole: String = "",
    val npcPortrait: String = "port_peasant",
    val backgroundDrawable: String = "bg_city_default",
    val availableChoices: List<ChoiceInfo> = emptyList(), // ZMIANA: Bardziej szczegółowe info
    val worldStability: Int = 100
)

data class ChoiceInfo(
    val choice: DialogueChoice,
    val isVisible: Boolean,
    val isEnabled: Boolean,
    val activeHeroName: String? = null // Jeśli inna postać przejmuje rozmowę
)

@HiltViewModel
class DialogueViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dialogueManager: DialogueManager,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val combatSystem: CombatSystem
) : ViewModel() {

    val uiState: StateFlow<DialogueUiState> = gameRepository.gameState
        .map { state ->
            val action = state.pendingAction
            val (nodeId, npcName, npcRole) = if (action is com.grimreich.core.PendingWorldAction.Dialogue) {
                Triple(action.nodeId, action.npcName, action.npcRole)
            } else {
                Triple("start", "Nieznajomy", "Mieszkaniec")
            }
            
            val node = dialogueManager.getNode(nodeId)
            
            val choicesInfo = node?.choices?.map { choice ->
                val mainHero = state.party.find { it.id == state.activeHeroId }
                val mainCanDo = mainHero?.let { checkHeroRequirements(choice, it, state) } ?: false
                
                if (mainCanDo) {
                    ChoiceInfo(choice, isVisible = true, isEnabled = true)
                } else {
                    val helper = state.party.find { it.id != state.activeHeroId && checkHeroRequirements(choice, it, state) }
                    if (helper != null) {
                        ChoiceInfo(choice, isVisible = true, isEnabled = true, activeHeroName = helper.name)
                    } else {
                        // DESIGN CHOICE: Hidden by user request to ensure each playthrough 
                        // with different stats/heroes reveals different content.
                        ChoiceInfo(choice, isVisible = false, isEnabled = false)
                    }
                }
            }?.filter { it.isVisible } ?: emptyList()

            val currentCity = cityCatalogue.get(state.world.locationId)

            // --- ONTOLOGICAL AUDIT: Glitch Address ---
            // If stability is low, NPCs might reveal the Player's real name (The Anchor)
            val finalNpcName = if (state.world.globalStability < 25 && kotlin.random.Random.nextFloat() < 0.3f) {
                state.playerName ?: npcName
            } else {
                npcName
            }

            val activeHero = state.party.find { it.id == state.activeHeroId }
            val processedNode = node?.let { 
                var n = it
                n = dialogueManager.applyWorldEffects(n, state.world.globalStability)
                if (activeHero != null) {
                    n = dialogueManager.applyTraumaEffects(n, activeHero)
                }
                n
            }

            DialogueUiState(
                currentNode = processedNode,
                npcName = finalNpcName,
                npcRole = npcRole,
                npcPortrait = dialogueManager.getPortrait(npcRole),
                backgroundDrawable = currentCity?.backgroundDrawable ?: "bg_city_default",
                availableChoices = choicesInfo,
                worldStability = state.world.globalStability
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DialogueUiState()
        )

    init {
        // init body is empty, state is handled via stateIn
    }

    private fun checkHeroRequirements(choice: DialogueChoice, hero: com.grimreich.core.Hero, state: GameState): Boolean {
        val attrs = choice.requiredAttributes ?: emptyMap()
        attrs.forEach { (attr, value) ->
            val heroVal = when (attr.uppercase()) {
                "STR", "STRENGTH", "SIŁA" -> hero.strength
                "INT", "INTELLIGENCE", "INTELIGENCJA" -> hero.intelligence
                "AGI", "AGILITY", "ZRĘCZNOŚĆ" -> hero.agility
                "CHA", "CHARISMA", "CHARYZMA" -> hero.charisma
                "PIETY", "POBOŻNOŚĆ" -> hero.piety
                else -> 100
            }
            if (heroVal < value) return false
        }

        // Reszta warunków (questy, złoto) jest globalna dla drużyny
        if (choice.requiredQuestId != null && !state.quest.activeQuestIds.contains(choice.requiredQuestId)) return false

        if (choice.triggerEvent == "QUEST_ACTIVE" && !state.quest.activeQuestIds.contains(choice.triggerValue)) return false
        if (choice.triggerEvent == "QUEST_OBJECTIVE_MET" && !questEngine.isObjectiveMet(choice.triggerValue ?: "", state)) return false
        if (choice.triggerEvent == "QUEST_COMPLETED" && !state.quest.completedQuestIds.contains(choice.triggerValue)) return false
        if (choice.triggerEvent == "REQUIRE_GOLD") {
            val amount = choice.triggerValue?.toIntOrNull() ?: 0
            if (state.gold < amount) return false
        }

        return true
    }

    fun choose(choice: DialogueChoice, onEnd: () -> Unit, onCombat: () -> Unit, onMarket: () -> Unit, onRitual: () -> Unit) {
        val state = gameRepository.currentState()
        
        // WYKRYWANIE POMOCY: Jeśli opcja została wybrana przez "pomocnika", ustawiamy go jako aktywnego
        val choiceInfo = uiState.value.availableChoices.find { it.choice == choice }
        choiceInfo?.activeHeroName?.let { helperName ->
            val helper = state.party.find { it.name == helperName }
            if (helper != null) {
                gameRepository.updateState { it.activeHeroId = helper.id }
                state.logEntries.add("${helper.name} przejmuje inicjatywę w rozmowie.")
            }
        }

        // Handle triggers (quest advances, item giving, etc)
        dialogueManager.handleTrigger(state, choice.triggerEvent, choice.triggerValue)

        // Project Anchor: Dialogue choice is a meaningful action. Apply Blood Tax if in Iron Fortress.
        if (state.world.locationId == "twierdza_zelazna") {
            gameRepository.updateState { s ->
                s.party.forEach { if (!it.isDead) it.hp = (it.hp - 1).coerceAtLeast(1) }
                s.logEntries.add("Krew: Decyzja kosztuje. Kotwica pije z Naczyń.")
            }
        }

        // SPECIAL TRANSITIONS
        if (choice.triggerEvent == "START_COMBAT" || choice.isCombatTrigger) {
            val enemyType = try { 
                // FIX: If it's a combat trigger but triggerEvent is ADVANCE_QUEST (common for mixed choices),
                // use a default enemy type if none provided in triggerValue.
                val typeStr = choice.triggerValue ?: "BANDIT"
                com.grimreich.core.EnemyType.valueOf(typeStr.trim().uppercase())
            } catch (e: Exception) { 
                com.grimreich.core.EnemyType.BANDIT 
            }
            val enemy = com.grimreich.core.Bestiary.get(enemyType)
            combatSystem.startCombat(enemy)
            gameRepository.updateState { it.pendingAction = com.grimreich.core.PendingWorldAction.None }
            onCombat()
            return
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
                it.pendingAction = com.grimreich.core.PendingWorldAction.None
            }
            onEnd()
        } else {
            gameRepository.updateState { s ->
                val current = s.pendingAction
                if (current is com.grimreich.core.PendingWorldAction.Dialogue) {
                    s.pendingAction = current.copy(nodeId = choice.targetNodeId)
                }
            }
        }
    }
}
