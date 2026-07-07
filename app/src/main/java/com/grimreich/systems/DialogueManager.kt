package com.grimreich.systems

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.systems.ChronicleSystem
import com.grimreich.systems.QuestEngine
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DialogueManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameRepositoryProvider: Lazy<GameRepository>,
    private val chronicleSystem: Lazy<ChronicleSystem>,
    private val questEngine: Lazy<QuestEngine>
) {
    private val nodes = mutableMapOf<String, DialogueNode>()
    private var activeDialogueId: String? = null
    private val gson = Gson()

    fun loadNodesFromAsset(path: String) {
        try {
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<DialogueNode>>() {}.type
            val loaded: List<DialogueNode> = gson.fromJson(json, type)
            loaded.forEach { registerNode(it) }
        } catch (e: Exception) {
            android.util.Log.e("DialogueManager", "Error loading dialogues: ${e.message}")
        }
    }

    fun isDialogueActive() = activeDialogueId != null
    fun currentDialogueId() = activeDialogueId
    fun endDialogue() { activeDialogueId = null }

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        if (id == "end") return null
        
        // FIX: Ensure pilot nodes are loaded if not present
        if (nodes.isEmpty()) {
            loadNodesFromAsset("grimreich/dialogues_pilot.json")
        }
        
        return nodes[id]
    }

    fun hasNode(id: String) = nodes.containsKey(id)

    fun listMissingTargets(): List<String> {
        val missing = mutableListOf<String>()
        nodes.values.forEach { node ->
            node.choices.forEach { choice ->
                if (choice.targetNodeId != "end" && !nodes.containsKey(choice.targetNodeId)) {
                    missing.add(choice.targetNodeId)
                }
            }
        }
        return missing
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        val state = gameRepositoryProvider.get().currentState()
        handleTrigger(state, choice.triggerEvent, choice.triggerValue)
        
        if (choice.targetNodeId == "end") {
            activeDialogueId = null
            return null
        }
        return getNode(choice.targetNodeId)
    }

    fun handleTrigger(state: GameState, event: String?, value: String?) {
        if (event == null) return
        val engine = questEngine.get()
        when (event) {
            "ACTIVATE_QUEST" -> {
                value?.let { engine.activateQuestDirect(state, it) }
                state.pendingQuestId = null
            }
            "ADVANCE_QUEST" -> {
                value?.let { engine.advanceStepDirect(state, it) }
            }
            "FAIL_QUEST" -> {
                value?.let { engine.failQuestDirect(state, it) }
            }
            "COMPLETE_QUEST" -> {
                value?.let { engine.completeQuestDirect(state, it) }
            }
            "GRANT_REPUTATION" -> {
                val parts = value?.split(":") ?: return
                if (parts.size == 2) {
                    val faction = parts[0]
                    val amount = parts[1].toIntOrNull() ?: 0
                    state.reputation.globalFactions[faction] = (state.reputation.globalFactions[faction] ?: 0) + amount
                }
            }
            "GIVE_ITEM" -> {
                value?.let { itemId ->
                    val repo = gameRepositoryProvider.get()
                    val item = repo.itemCatalogue.get(itemId)
                    if (item != null) {
                        state.inventory.add(item.copy())
                        state.logEntries.add("Otrzymano przedmiot: ${item.name}")
                    }
                }
            }
            "ACTIVATE_QUEST_CHAIN" -> {
                // Special logic for Mira's quest chain progression
                when {
                    engine.getStatus("q_scribes_1", state) == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuestDirect(state, "q_scribes_1")
                    engine.getStatus("q_scribes_2", state) == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuestDirect(state, "q_scribes_2")
                    engine.getStatus("q_scribes_3", state) == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuestDirect(state, "q_scribes_3")
                    engine.getStatus("q_collapse_core", state) == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuestDirect(state, "q_collapse_core")
                }
                state.pendingQuestId = null
            }
        }
    }

    fun getPortrait(role: String): String {
        return when (role.uppercase()) {
            "GUARD" -> "port_guard"
            "MERCHANT" -> "port_merchant"
            "AELION" -> "port_aelion"
            "MIRA" -> "port_mira"
            "ECHO" -> "port_wraith"
            else -> "port_peasant"
        }
    }

    fun applyWorldEffects(node: DialogueNode, stability: Int): DialogueNode {
        if (stability > 40) return node
        
        val seed = node.id.hashCode().toLong()
        return node.copy(
            text = glitchText(node.text, seed)
        )
    }

    fun glitchText(input: String, seed: Long): String {
        val rand = Random(seed)
        val sb = StringBuilder()
        input.forEach { c ->
            if (c == ' ') {
                sb.append(' ')
            } else if (rand.nextFloat() < 0.15f) {
                val glitches = listOf('#', '@', '$', '%', '&', '0', '1', 'X')
                sb.append(glitches.random(rand))
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun seedBasicDialogues() {
        nodes.clear()
        loadNodesFromAsset("grimreich/dialogues_pilot.json")
    }
}
