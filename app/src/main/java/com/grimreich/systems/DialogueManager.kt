package com.grimreich.systems

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grimreich.core.GameConstants
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DialogueManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val gameRepositoryProvider: Lazy<GameRepository>,
    private val questEngine: Lazy<QuestEngine>,
) {
    private val nodes = mutableMapOf<String, DialogueNode>()
    private val gson = Gson()
    private val loadLock = Any()
    private var isLoaded = false

    fun loadNodesFromAsset(path: String) {
        synchronized(loadLock) {
            try {
                val json = context.assets.open(path).bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<DialogueNode>>() {}.type
                val loaded: List<DialogueNode> = gson.fromJson(json, type)
                loaded.forEach { registerNode(it) }
                android.util.Log.d("DialogueManager", "Loaded ${loaded.size} nodes from $path")
            } catch (e: Exception) {
                android.util.Log.e("DialogueManager", "Error loading dialogues from $path: ${e.message}")
            }
        }
    }

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getAllNodes(): Map<String, DialogueNode> = nodes

    fun hasNode(nodeId: String): Boolean = nodes.containsKey(nodeId)

    fun getNode(id: String): DialogueNode? {
        if (id == "end") return null
        
        synchronized(loadLock) {
            if (!isLoaded && nodes.isEmpty()) {
                seedBasicDialogues()
            }
        }
        
        return nodes[id]
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        val repo = gameRepositoryProvider.get()
        repo.updateState { state ->
            handleTrigger(state, choice.triggerEvent, choice.triggerValue)
        }
        
        if (choice.targetNodeId == "end") {
            return null
        }
        return getNode(choice.targetNodeId)
    }

    fun handleTrigger(state: GameState, event: String?, value: String?) {
        if (event == null) return
        val engine = questEngine.get()
        
        // PURIFICATION: All event triggers are case-insensitive
        val normalizedEvent = event.lowercase().trim()
        android.util.Log.d("DialogueManager", "[DIALOGUE] Trigger firing: $normalizedEvent -> $value")
        
        when (normalizedEvent) {
            "activate_quest" -> {
                val action = state.pendingAction
                val targetId = if (value?.lowercase() == "active" && action is com.grimreich.core.PendingWorldAction.Dialogue) {
                    action.relatedQuestId
                } else {
                    value?.lowercase()
                }
                targetId?.let { engine.activateQuestDirect(state, it) }
            }
            "advance_quest", "quest_objective_met" -> {
                val action = state.pendingAction
                val targetId = if (value?.lowercase() == "active" && action is com.grimreich.core.PendingWorldAction.Dialogue) {
                    action.relatedQuestId
                } else {
                    value?.lowercase()
                }
                targetId?.let { engine.advanceStepDirect(state, it) }
            }
            "fail_quest" -> {
                val action = state.pendingAction
                val targetId = if (value?.lowercase() == "active" && action is com.grimreich.core.PendingWorldAction.Dialogue) {
                    action.relatedQuestId
                } else {
                    value?.lowercase()
                }
                targetId?.let { engine.failQuestDirect(state, it) }
            }
            "complete_quest" -> {
                val action = state.pendingAction
                val targetId = if ((value?.lowercase() == "active") && (action is com.grimreich.core.PendingWorldAction.Dialogue)) {
                    action.relatedQuestId
                } else {
                    value?.lowercase()
                }
                
                targetId?.let { engine.completeQuestDirect(state, it) }
            }
            "increment_meta" -> {
                val inc = value?.toIntOrNull() ?: 1
                state.metaAwarenessLevel += inc
                state.logEntries.add("Czujesz, że ktoś dopisał uwagę na marginesie twojej sesji.")
            }
            "set_world_flag" -> {
                value?.let { state.quest.worldFlags.add(it.lowercase()) }
            }
            "grant_reputation" -> {
                val parts = value?.split(":") ?: return
                if (parts.size == 2) {
                    val faction = parts[0].lowercase()
                    val amount = parts[1].toIntOrNull() ?: 0
                    state.reputation.globalFactions[faction] = (state.reputation.globalFactions[faction] ?: 0) + amount
                }
            }
            "give_item" -> {
                value?.let { itemId ->
                    val repo = gameRepositoryProvider.get()
                    val item = repo.itemCatalogue.createInstance(itemId.lowercase())
                    if (item != null) {
                        state.inventory.add(item)
                        state.logEntries.add("Otrzymano przedmiot: ${item.name}")
                    }
                }
            }
            "unlock_lore" -> {
                value?.let { loreId ->
                    if (state.unlockedLoreIds.add(loreId.lowercase())) {
                        state.logEntries.add("Nowy wpis w Kronice: $loreId")
                    }
                }
            }
            "check_world_flag" -> {
                android.util.Log.d("DialogueManager", "Checked world flag: $value (Status: ${state.quest.worldFlags.contains(value?.lowercase())})")
            }
            "open_market" -> {
                state.logEntries.add("Otwierasz okno handlu...")
            }
            "increment_stability" -> {
                val amount = value?.toIntOrNull() ?: GameConstants.DEFAULT_STABILITY_INC
                state.world.globalStability = (state.world.globalStability + amount).coerceAtMost(100)
                state.logEntries.add("Poczucie celu wzmacnia paradygmat świata.")
            }
        }
    }

    fun getPortrait(role: String): String {
        // PURIFICATION: Role must match lowercase registry
        return when (role.lowercase().trim()) {
            "guard", "straznik", "fortress_guard" -> "port_knight"
            "merchant", "kupiec" -> "port_alchemist"
            "aelion" -> "port_priest"
            "mira" -> "port_mage"
            "ravenn" -> "port_wraith"
            "echo" -> "port_wraith"
            "peasant" -> "port_barbarian"
            "priest" -> "port_priest"
            "monk" -> "port_priest"
            "assassin" -> "port_rogue"
            "ritualist" -> "port_alchemist"
            "noble" -> "port_knight"
            "scholar" -> "port_scholar"
            "hunter" -> "port_ranger"
            else -> "port_barbarian"
        }
    }

    fun applyWorldEffects(node: DialogueNode, stability: Int): DialogueNode {
        if (stability > GameConstants.STABILITY_GLITCH_THRESHOLD) return node
        val seed = node.id.hashCode().toLong()
        return node.copy(text = glitchText(node.text, seed))
    }

    fun applyTraumaEffects(node: DialogueNode, hero: com.grimreich.core.Hero): DialogueNode {
        if (hero.traumaMarks.isEmpty()) return node
        
        var modifiedText = node.text
        if (hero.traumaMarks.any { it.severity >= 2 }) {
            modifiedText = "[NPC COFA SIĘ Z PRZERAŻENIEM] $modifiedText"
        }
        
        if (hero.traumaMarks.any { it.id == "t_echo_vision" }) {
            modifiedText = modifiedText.replace(" ", " . ")
        }

        if (hero.traumaMarks.any { it.id == "t_hollow_voice" }) {
            modifiedText = modifiedText.uppercase()
        }

        return node.copy(text = modifiedText)
    }

    fun glitchText(input: String, seed: Long): String {
        val rand = Random(seed)
        val sb = StringBuilder()
        input.forEach { c ->
            if (c == ' ') {
                sb.append(' ')
            } else if (rand.nextFloat() < GameConstants.GLITCH_CHANCE_LOW_STABILITY) {
                val glitches = listOf('#', '@', '$', '%', '&', '0', '1', 'X')
                sb.append(glitches.random(rand))
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun seedBasicDialogues() {
        synchronized(loadLock) {
            if (isLoaded) return
            // BUG-15: Do NOT clear if we want to preserve modded or dynamic nodes
            loadNodesFromAsset("grimreich/dialogues_pilot.json")
            loadNodesFromAsset("grimreich/dialogues_extended.json")
            isLoaded = true
        }
    }
}
