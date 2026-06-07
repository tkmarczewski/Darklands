package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import kotlin.random.Random

object DialogueManager {
    private val nodes = mutableMapOf<String, DialogueNode>()

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val baseNode = nodes[id] ?: return null
        return applyWorldEffects(baseNode)
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val world = GameRepository.state.world
        val stability = world.globalStability
        
        if (stability >= 70) return node
        
        // Fracture Logic: Text glitches at low world stability
        val fracturedText = if (stability < 30) {
            glitchText(node.text) + " ...GŁOSY... [NIE SŁUCHAJ ICH]"
        } else {
            node.text + " (czujesz nienaturalny chłód)"
        }
        
        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.2f) "[WYMAZANO]" else word
        }.joinToString(" ")
    }
    
    fun seedBasicDialogues() {
        registerNode(DialogueNode(
            id = "merchant_start",
            npcId = "procedural",
            text = "Mam towary, których nie znajdziesz nigdzie indziej. Interesuje cię coś konkretnego?",
            choices = listOf(
                DialogueChoice("Pokaż ofertę", "merchant_trade"),
                DialogueChoice("Skąd masz te rzeczy?", "merchant_info"),
                DialogueChoice("Żegnaj", "end")
            )
        ))
        
        registerNode(DialogueNode(
            id = "chronicler_start",
            npcId = "procedural",
            text = "Każde pęknięcie w rzeczywistości zapisuję w mojej księdze. Czy chcesz poznać prawdę?",
            choices = listOf(
                DialogueChoice("Tak, powiedz mi", "chronicler_truth", onSelect = { it.party.forEach { h -> h.sanity -= 5 } }),
                DialogueChoice("To tylko bajki", "chronicler_dismiss"),
                DialogueChoice("Nie teraz", "end")
            )
        ))
    }
}
