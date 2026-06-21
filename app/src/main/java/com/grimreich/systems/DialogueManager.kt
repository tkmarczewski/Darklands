package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DialogueManager @Inject constructor(
    private val gameRepositoryProvider: Lazy<GameRepository>
) {
    private val nodes = mutableMapOf<String, DialogueNode>()

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val baseNode = nodes[id] ?: return null
        return applyWorldEffects(baseNode)
    }

    fun getPortrait(role: String): String {
        return when (role.lowercase()) {
            "aelion" -> "port_priest"
            "merchant" -> "port_rogue"
            "zealot" -> "port_priest"
            "mystic" -> "port_mage"
            "guard" -> "port_warrior"
            "xyrel" -> "port_knight"
            "mira" -> "port_mage"
            "sereth" -> "port_wraith"
            "ferrun" -> "port_barbarian"
            "noctyros" -> "port_demon"
            "anomalia" -> "port_dragon"
            "alchemik", "alchemist" -> "port_alchemist"
            "barbarzynca", "barbarian" -> "port_barbarian"
            "kaplan", "priest" -> "port_priest"
            "lowca", "ranger" -> "port_ranger"
            "rycerz", "knight" -> "port_knight"
            "mag", "scholar" -> "port_mage"
            "ork", "orc" -> "port_orc"
            "troll" -> "port_troll"
            "szkielet", "skeleton" -> "port_skeleton"
            "upior", "wraith" -> "port_wraith"
            "demon" -> "port_demon"
            "smok", "dragon" -> "port_dragon"
            "wilk", "wolf" -> "port_wolf"
            "lotr", "rogue", "thief" -> "port_rogue"
            else -> "port_rogue"
        }
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val state = gameRepositoryProvider.get().currentState()
        val stability = state.world.globalStability
        
        if (stability >= 70) return node
        
        val fracturedText = if (stability < 30) {
            glitchText(node.text) + " ...GŁOSY... ABSOLUT... [NIE SŁUCHAJ ICH] ...CISZA..."
        } else {
            node.text + " (rzeczywistość wokół ciebie zaczyna tracić nasycenie)"
        }
        
        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.1f) "[BŁĄD_ONTOLOGICZNY]" else word
        }.joinToString(" ")
    }
    
    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return

        // GUARD
        registerNode(DialogueNode(
            id = "guard_start", npcId = "guard",
            text = "Stój! Mgła gęstnieje, a prawo musi być przestrzegane. Czego szukasz w cieniu murów?",
            choices = listOf(
                DialogueChoice("Szukam pracy.", "end"),
                DialogueChoice("Tylko przechodzę.", "end")
            )
        ))

        // MERCHANT
        registerNode(DialogueNode(
            id = "merchant_start", npcId = "procedural",
            text = "Mam towary z Drugiej Strony. Złoto jest tu jedyną prawdą.",
            choices = listOf(DialogueChoice("Pokaż ofertę", "end"))
        ))

        // ZEALOT
        registerNode(DialogueNode(
            id = "zealot_start", npcId = "procedural",
            text = "Prorocy patrzą! Czy Twoja dusza jest czysta, wędrowcze?",
            choices = listOf(
                DialogueChoice("Jestem wierny.", "end"),
                DialogueChoice("Ofiaruj krew (HP-5)", "zealot_sacrifice", onSelect = { 
                    it.party.forEach { h -> h.hp -= 5 }
                })
            )
        ))
        registerNode(DialogueNode(id = "zealot_sacrifice", npcId = "procedural", text = "Twoja ofiara została przyjęta. Czuć mrowienie w kościach."))

        // MYSTIC
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "procedural",
            text = "Cień w Tobie rośnie. Absolut Cię woła, Kotwico.",
            choices = listOf(DialogueChoice("Kim jesteś?", "end"))
        ))

        // AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Mgła nie jest pogodą, wędrowcze. To skroplona niepamięć Absolutu.",
            choices = listOf(
                DialogueChoice("Pamiętam imię mojej matki.", "end"),
                DialogueChoice("Szukam wizji.", "end")
            )
        ))

        // OTHER PROCEDURALS
        registerNode(DialogueNode(id = "soldier_start", npcId = "procedural", text = "Stal to jedyna modlitwa, jaką znam.", choices = listOf(DialogueChoice("Prowadź nas.", "end"))))
        registerNode(DialogueNode(id = "amnesiac_start", npcId = "procedural", text = "Gdzie jest mój dom? Pamiętam tylko białą pustkę...", choices = listOf(DialogueChoice("Nie ma już domu.", "end"))))
        registerNode(DialogueNode(id = "beggar_start", npcId = "procedural", text = "Daj miedziaka dla bytu, który znika.", choices = listOf(DialogueChoice("Proszę (Gold-5)", "end", onSelect = { it.gold -= 5 }))))
    }
}
