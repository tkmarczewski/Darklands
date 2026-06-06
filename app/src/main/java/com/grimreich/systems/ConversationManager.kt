package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import kotlin.random.Random

object ConversationManager {
    private val dialogueNodes = mutableMapOf<String, DialogueNode>()

    fun registerDialogue(node: DialogueNode) {
        dialogueNodes[node.id] = node
    }

    fun start(npcId: String): DialogueNode? {
        val node = dialogueNodes.values.find { it.npcId == npcId && it.id.endsWith("_start") }
        return node?.let { applyEchoEffect(it) }
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        choice.onSelect(GameRepository.state)
        val node = dialogueNodes[choice.targetNodeId]
        return node?.let { applyEchoEffect(it) }
    }
    
    private fun applyEchoEffect(node: DialogueNode): DialogueNode {
        val intensity = GameRepository.state.world.echoIntensity
        if (intensity <= 0.1f) return node
        
        // Simulating fractured memory/speech
        val echoedText = if (Random.nextFloat() < intensity) {
            node.text.split(" ").map { word ->
                if (Random.nextFloat() < intensity * 0.5f) "[...]" else word
            }.joinToString(" ") + " ...czy to się już wydarzyło?"
        } else {
            node.text
        }
        
        return node.copy(text = echoedText)
    }
    
    fun seedSampleDialogues() {
        registerDialogue(DialogueNode(
            id = "innkeeper_start",
            npcId = "npc_innkeeper",
            text = "Witaj podróżniku. Co cię sprowadza do naszej karczmy w tych mrocznych czasach?",
            choices = listOf(
                DialogueChoice("Szukam pracy", "innkeeper_work"),
                DialogueChoice("Podaj mi piwa (5g)", "innkeeper_beer", onSelect = { it.gold -= 5 }),
                DialogueChoice("Żegnaj", "innkeeper_end")
            )
        ))
        
        registerDialogue(DialogueNode(
            id = "innkeeper_work",
            npcId = "npc_innkeeper",
            text = "Zawsze znajdzie się robota dla kogoś z mieczem. Podobno w ruinach na wschodzie zalęgły się cienie...",
            choices = listOf(
                DialogueChoice("Zajmę się tym", "innkeeper_end", onSelect = { 
                    // Add quest
                }),
                DialogueChoice("To brzmi zbyt niebezpiecznie", "innkeeper_start")
            )
        ))
        
        registerDialogue(DialogueNode("innkeeper_end", "npc_innkeeper", "Powodzenia. Niech światło cię prowadzi."))
    }
}
