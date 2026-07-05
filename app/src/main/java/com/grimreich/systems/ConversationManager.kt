package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ConversationManager @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val dialogueNodes = mutableMapOf<String, DialogueNode>()

    fun registerDialogue(node: DialogueNode) {
        dialogueNodes[node.id] = node
    }

    fun start(nodeId: String): DialogueNode? {
        return dialogueNodes[nodeId]?.let { applyEchoEffect(it) }
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        gameRepository.updateState { state ->
            choice.onSelect?.invoke(state)
        }
        return if (choice.targetNodeId == "end") null else start(choice.targetNodeId)
    }

    private fun applyEchoEffect(node: DialogueNode): DialogueNode {
        val intensity = gameRepository.currentState().world.echoIntensity
        if (intensity < 0.3f) return node
        
        return node.copy(text = node.text + " " + generateEchoDistortion(intensity))
    }

    private fun generateEchoDistortion(intensity: Float): String {
        return if (Random.nextFloat() < intensity) "[...ECHO...]" else ""
    }

    fun seedSampleDialogues() {
        registerDialogue(DialogueNode(
            "start", "citizen", "Witaj w naszym mieście.",
            listOf(DialogueChoice("Dziękuję.", "end"))
        ))
    }
}
