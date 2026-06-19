package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationManager @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val dialogueNodes = mutableMapOf<String, DialogueNode>()

    fun registerDialogue(node: DialogueNode) {
        dialogueNodes[node.id] = node
    }

    fun start(nodeId: String): DialogueNode? {
        val node = dialogueNodes[nodeId]
        return node?.let { applyEchoEffect(it) }
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        choice.onSelect(gameRepository.currentState())
        gameRepository.persistCurrentState()
        return start(choice.targetNodeId)
    }

    private fun applyEchoEffect(node: DialogueNode): DialogueNode {
        val intensity = gameRepository.currentState().world.echoIntensity
        if (intensity < 0.3f) return node

        return node.copy(text = node.text + " ... " + generateEchoDistortion(intensity))
    }

    private fun generateEchoDistortion(intensity: Float): String {
        return if (intensity > 0.7f) "[BŁĄD RZECZYWISTOŚCI]" else "Czy to Ty, Kotwico?"
    }

    fun seedSampleDialogues() {
        if (dialogueNodes.isNotEmpty()) return

        registerDialogue(DialogueNode(
            id = "start", npcId = "narrator",
            text = "Mgła otacza miasto. Czujesz chłód.",
            choices = listOf(
                DialogueChoice("Idź dalej", "intro_2"),
                DialogueChoice("Zatrzymaj się", "end")
            )
        ))
        
        registerDialogue(DialogueNode(
            id = "intro_2", npcId = "narrator",
            text = "Widzisz sylwetkę Aeliona w oddali.",
            choices = listOf(DialogueChoice("Podejdź", "aelion_start"))
        ))
    }
}
