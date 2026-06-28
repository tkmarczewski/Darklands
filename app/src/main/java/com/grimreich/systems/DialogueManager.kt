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
    private val gameRepositoryProvider: Lazy<GameRepository>,
    private val chronicleSystem: Lazy<ChronicleSystem>,
    private val questEngine: Lazy<QuestEngine>
) {
    private val nodes = mutableMapOf<String, DialogueNode>()
    private var activeDialogueId: String? = null

    fun isDialogueActive(): Boolean = activeDialogueId != null
    fun currentDialogueId(): String? = activeDialogueId
    fun endDialogue() { activeDialogueId = null }

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val baseNode = nodes[id] ?: return null
        val stability = gameRepositoryProvider.get().currentState().world.globalStability
        
        // Project Cipher: Apply glitches based on stability
        return if (stability < 40) {
            applyWorldEffects(baseNode, stability)
        } else {
            baseNode
        }
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        val state = gameRepositoryProvider.get().currentState()
        
        // Execute side effects
        choice.onSelect(state)
        
        activeDialogueId = choice.targetNodeId
        return if (choice.targetNodeId == "end") null else getNode(choice.targetNodeId)
    }

    fun getPortrait(npcId: String): String {
        return when (npcId) {
            "guard" -> "port_guard"
            "merchant" -> "port_merchant"
            "aelion" -> "port_prophet"
            "mira" -> "port_mira"
            else -> "port_knight"
        }
    }

    private fun applyWorldEffects(node: DialogueNode, stability: Int): DialogueNode {
        val glitchedText = if (stability < 20) glitchText(node.text) else node.text
        return node.copy(text = glitchedText)
    }

    private fun glitchText(text: String): String {
        if (text.isEmpty()) return text
        val chars = text.toCharArray()
        repeat(maxOf(1, text.length / 20)) {
            val idx = Random.nextInt(chars.size)
            chars[idx] = if (Random.nextBoolean()) '?' else '#'
        }
        return String(chars)
    }

    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return

        // 1. GUARD
        registerNode(DialogueNode(
            id = "guard_start", npcId = "guard",
            text = "Stój! Mgła gęstnieje, a prawo musi być przestrzegane. Czego szukasz w cieniu murów?",
            choices = listOf(
                DialogueChoice("Czy coś niepokojącego działo się ostatnio?", "guard_quest_check"),
                DialogueChoice("Tylko przechodzę.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "guard_quest_check", npcId = "guard",
            text = "Mamy problem z 'Wyrokiem'. Jeśli chcesz pomóc, weź to zlecenie.",
            choices = listOf(
                DialogueChoice("Przyjmuję (ZADANIE: Wyrok).", "end", onSelect = { state ->
                    questEngine.get().activateQuest("q_verdict_1")
                    state.pendingQuestId = null
                }),
                DialogueChoice("Może później.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "guard_report_back", npcId = "guard",
            text = "Dobra robota, Kotwico. Inkwizycja dziękuje za Twoją służbę. Oto zapłata.",
            choices = listOf(
                DialogueChoice("Ku chwale Zakonu.", "end", onSelect = { s ->
                    s.gold += 100
                    s.reputation.globalFactions["KNIGHTS"] = (s.reputation.globalFactions["KNIGHTS"] ?: 0) + 10
                })
            )
        ))

        // 2. MERCHANT
        registerNode(DialogueNode(
            id = "merchant_start", npcId = "merchant",
            text = "Witaj, podróżniku. Mam towary, których nie znajdziesz nigdzie indziej... za odpowiednią cenę.",
            choices = listOf(
                DialogueChoice("Pokaż mi swoje towary (RYNEK).", "end"),
                DialogueChoice("Co wiesz o tym regionie?", "merchant_info"),
                DialogueChoice("Do widzenia.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "merchant_info", npcId = "merchant",
            text = "Ceny rosną, a stabilność spada. Mówią, że Archiwiści znowu zaczęli śnić.",
            choices = listOf(
                DialogueChoice("Interesujące.", "merchant_start")
            )
        ))

        // 3. AELION (Regional Hero)
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Kotwico... Twoja obecność tutaj jest jak pęknięcie na tafli jeziora. Czy wiesz, że ten świat jest tylko snem Sędziów?",
            choices = listOf(
                DialogueChoice("Nie rozumiem.", "aelion_meta"),
                DialogueChoice("Szukam sposobu na stabilizację Mgły.", "aelion_quest"),
                DialogueChoice("Muszę iść.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "aelion_meta", npcId = "aelion",
            text = "Gdy stabilność spadnie do zera, szyfr zostanie ujawniony. Wtedy zobaczymy surowy kod naszego przeznaczenia.",
            choices = listOf(
                DialogueChoice("Jak to możliwe?", "aelion_start")
            )
        ))

        registerNode(DialogueNode(
            id = "aelion_quest", npcId = "aelion",
            text = "Aby ocalić GrimReich, musisz odnaleźć pozostałe relikwie. Zacznij od Wybrzeża Północnego.",
            choices = listOf(
                DialogueChoice("Zrobię to.", "end")
            )
        ))
        
        // 4. MIRA (Regional Hero)
        registerNode(DialogueNode(
            id = "mira_start", npcId = "mira",
            text = "Lustra nie kłamią, podróżniku. Widzę w Twoim odbiciu wiele wersji GrimReich. Która z nich jest prawdziwa?",
            choices = listOf(
                DialogueChoice("Wszystkie są prawdziwe.", "mira_wisdom"),
                DialogueChoice("Żadna nie jest prawdziwa.", "mira_wisdom"),
                DialogueChoice("To nie ma znaczenia.", "end")
            )
        ))
        
        registerNode(DialogueNode(
            id = "mira_wisdom", npcId = "mira",
            text = "Słusznie. Prawda jest jedynie sumą wszystkich echa. Jeśli chcesz wiedzieć więcej, przynieś mi Esencję Odbicia.",
            choices = listOf(
                DialogueChoice("Będę pamiętał.", "end")
            )
        ))
    }
}
