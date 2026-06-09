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
        nodes.clear()

        // MERCHANT DIALOGUES
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
        registerNode(DialogueNode(id = "merchant_info", npcId = "procedural", text = "Część z odzysku, część z... Drugiej Strony. Pytaj mniej, płać więcej."))
        registerNode(DialogueNode(id = "merchant_trade", npcId = "procedural", text = "Złoto to jedyna stała w tym rozpadającym się świecie."))

        // CHRONICLER DIALOGUES
        registerNode(DialogueNode(
            id = "chronicler_start",
            npcId = "procedural",
            text = "Każde pęknięcie w rzeczywistości zapisuję w mojej księdze. Czy chcesz poznać prawdę?",
            choices = listOf(
                DialogueChoice("Tak, powiedz mi", "chronicler_truth", onSelect = { it.party.forEach { h -> h.sanity -= 10 } }),
                DialogueChoice("To tylko bajki", "chronicler_dismiss"),
                DialogueChoice("Nie teraz", "end")
            )
        ))
        registerNode(DialogueNode(id = "chronicler_truth", npcId = "procedural", text = "Świat jest snem fenomenów. My jesteśmy tylko echem, które powoli zanika... [Tracisz Poczytalność]"))
        registerNode(DialogueNode(id = "chronicler_dismiss", npcId = "procedural", text = "Ignorancja to dar, którego ci zazdroszczersss..."))

        // ZEALOT DIALOGUES
        registerNode(DialogueNode(
            id = "zealot_start",
            npcId = "procedural",
            text = "Prorocy patrzą! Czy Twoja dusza jest czysta, wędrowcze?",
            choices = listOf(
                DialogueChoice("Ofiaruj krew (HP-5)", "zealot_sacrifice", onSelect = { 
                    it.party.forEach { h -> h.hp -= 5 }
                    it.prayer.faith += 15
                }),
                DialogueChoice("Odejdź w pokoju", "end")
            )
        ))
        registerNode(DialogueNode(id = "zealot_sacrifice", npcId = "procedural", text = "Twoja ofiara została przyjęta. Prorocy szepczą Twoje imię."))
        
        // MYSTIC DIALOGUES
        registerNode(DialogueNode(
            id = "mystic_start",
            npcId = "procedural",
            text = "Widzę mrok wokół Ciebie. Czy on też Cię woła?",
            choices = listOf(
                DialogueChoice("O czym mówisz?", "mystic_lore"),
                DialogueChoice("Zostaw mnie", "end")
            )
        ))
        registerNode(DialogueNode(id = "mystic_lore", npcId = "procedural", text = "Absolut nie jest bogiem. To błąd w tkance, który chce zostać naprawiony naszym kosztem."))

        // FUGITIVE DIALOGUES
        registerNode(DialogueNode(
            id = "fugitive_start",
            npcId = "procedural",
            text = "Nie patrz na mnie tak... ja tylko przechodzę. Czego chcesz?",
            choices = listOf(
                DialogueChoice("Dlaczego uciekasz?", "fugitive_reason"),
                DialogueChoice("Widziałem strażników...", "fugitive_guards"),
                DialogueChoice("Żegnaj", "end")
            )
        ))
        registerNode(DialogueNode(id = "fugitive_reason", npcId = "procedural", text = "Zobaczyłem coś, czego nie powinienem. Mgła nie tylko zabiera, ona... pokazuje prawdę o nas."))
        registerNode(DialogueNode(id = "fugitive_guards", npcId = "procedural", text = "Strażnicy? Oni są już martwi w środku, tylko jeszcze o tym nie wiedzą."))
    }
}
