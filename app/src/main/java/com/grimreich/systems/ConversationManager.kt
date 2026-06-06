package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice

object ConversationManager {
    private val dialogueNodes = mutableMapOf<String, DialogueNode>()

    fun registerDialogue(node: DialogueNode) {
        dialogueNodes[node.id] = node
    }

    fun start(npcId: String): DialogueNode? {
        // Return the start node for the given NPC
        return dialogueNodes.values.find { it.npcId == npcId && it.id.endsWith("_start") }
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        choice.onSelect(GameRepository.state)
        return dialogueNodes[choice.targetNodeId]
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
