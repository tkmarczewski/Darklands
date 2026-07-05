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
            android.util.Log.e("DialogueManager", "Failed to load dialogues from $path: ${e.message}")
        }
    }

    fun isDialogueActive(): Boolean = activeDialogueId != null
    fun currentDialogueId(): String? = activeDialogueId
    fun endDialogue() { activeDialogueId = null }

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val node = nodes[id] ?: return null
        val state = gameRepositoryProvider.get().currentState()
        
        // Dynamic text processing (e.g. glitches based on world state)
        val stability = state.world.globalStability
        return if (stability < 30) applyWorldEffects(node, stability) else node
    }

    fun hasNode(id: String): Boolean = nodes.containsKey(id)

    fun listMissingTargets(): List<String> {
        return nodes.values.flatMap { it.choices }
            .map { it.targetNodeId }
            .filter { it != "end" && !nodes.containsKey(it) }
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        gameRepositoryProvider.get().updateState { state ->
            // Execute hardcoded callback (if any)
            choice.onSelect?.invoke(state)
            
            // Execute data-driven triggers from JSON
            handleTrigger(state, choice.triggerEvent, choice.triggerValue)
        }

        if (choice.targetNodeId == "end") {
            endDialogue()
            return null
        }
        
        activeDialogueId = choice.targetNodeId
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
            "GRANT_REPUTATION" -> {
                val parts = value?.split(":") ?: return
                if (parts.size == 2) {
                    val faction = parts[0]
                    val amount = parts[1].toIntOrNull() ?: 0
                    state.reputation.globalFactions[faction] = (state.reputation.globalFactions[faction] ?: 0) + amount
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

    fun getPortrait(npcId: String): String {
        return when (npcId) {
            "guard" -> "port_guard"
            "merchant" -> "port_merchant"
            "aelion" -> "port_aelion"
            "mira" -> "port_mira"
            "mystic" -> "port_mystic"
            else -> "port_knight"
        }
    }

    private fun applyWorldEffects(node: DialogueNode, stability: Int): DialogueNode {
        // Use a deterministic seed based on node ID and current stability
        val seed = node.id.hashCode().toLong() + stability
        val glitchedText = glitchText(node.text, seed)
        return node.copy(text = glitchedText)
    }

    private fun glitchText(text: String, seed: Long): String {
        val rng = Random(seed)
        val chars = text.toCharArray()
        val glitchChars = "ØΣΠΞΩλμ"
        val glitchCount = (text.length * 0.1).toInt()
        
        repeat(glitchCount) {
            val idx = rng.nextInt(chars.size)
            if (!chars[idx].isWhitespace()) {
                chars[idx] = glitchChars[rng.nextInt(glitchChars.length)]
            }
        }
        return String(chars)
    }

    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return
        // Load external dialogues (Pilot)
        loadNodesFromAsset("grimreich/dialogues_pilot.json")
    }
}
