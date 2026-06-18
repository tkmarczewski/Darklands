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
        return nodes[id]
    }

    fun getPortrait(role: String): String {
        return when (role.lowercase()) {
            "aelion" -> "port_priest"
            "merchant" -> "port_rogue"
            "zealot" -> "port_priest"
            "mystic" -> "port_mage"
            else -> "port_rogue"
        }
    }

    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return

        // AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Mgła nie jest pogodą, wędrowcze. To skroplona niepamięć Absolutu.",
            choices = listOf(
                DialogueChoice("Pamiętam imię mojej matki.", "end"),
                DialogueChoice("Szukam wizji (ZADANIE).", "end", onSelect = {
                    QuestSystem.complete("q_start_01")
                })
            )
        ))

        // PROCEDURAL FALLBACKS
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "procedural",
            text = "Cień w Tobie rośnie. Kotwico, słyszysz szept?",
            choices = listOf(DialogueChoice("Nie rozumiem.", "end"))
        ))
        
        registerNode(DialogueNode(
            id = "zealot_start", npcId = "procedural",
            text = "Prorocy patrzą! Czy Twoja dusza jest czysta?",
            choices = listOf(DialogueChoice("Zawsze.", "end"))
        ))

        registerNode(DialogueNode(
            id = "merchant_start", npcId = "procedural",
            text = "Towary z Drugiej Strony. Złoto to jedyna prawda.",
            choices = listOf(DialogueChoice("Pokaż ofertę.", "end"))
        ))
    }
}
