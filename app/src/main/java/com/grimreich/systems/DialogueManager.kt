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

    fun isDialogueActive() = activeDialogueId != null
    fun getActiveDialogueId() = activeDialogueId
    fun endDialogue() { activeDialogueId = null }

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val gameState = gameRepositoryProvider.get().currentState()
        
        if (id.endsWith("_start") && gameState.persistentMeta.totalSessionsFinished > 0) {
            val dejavuNode = "${id}_dejavu"
            if (nodes.containsKey(dejavuNode)) {
                activeDialogueId = dejavuNode
                return applyWorldEffects(nodes[dejavuNode]!!)
            }
        }

        val baseNode = nodes[id] ?: return null
        activeDialogueId = id
        return applyWorldEffects(baseNode)
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        gameRepositoryProvider.get().updateState { state ->
            choice.onSelect(state)
        }

        return if (choice.targetNodeId == "end") {
            endDialogue()
            null
        } else {
            getNode(choice.targetNodeId)
        }
    }

    fun getPortrait(role: String): String {
        return when (role.lowercase()) {
            "aelion" -> "kaplan"
            "merchant", "kupiec" -> "zloto"
            "guard", "straznik" -> "rycerz"
            "mira" -> "mag"
            "ferrun" -> "barbarzynca"
            "noctyros" -> "demon"
            else -> "lowca"
        }
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val state = gameRepositoryProvider.get().currentState()
        val stability = state.world.globalStability
        if (stability >= 70) return node
        return node.copy(text = glitchText(node.text))
    }

    private fun glitchText(text: String): String {
        val metaGlitches = listOf("[BŁĄD]", "[ZABIJ_PROCES]", "[CISZA]")
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.05f) metaGlitches.random() else word
        }.joinToString(" ")
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
                DialogueChoice("Przyjmuję (ZADANIE: Wyrok).", "end", onSelect = {
                    questEngine.get().activateQuest("q_verdict_1")
                }),
                DialogueChoice("Może później.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "guard_report_back", npcId = "guard",
            text = "Dobra robota, Kotwico. Inkwizycja dziękuje za Twoją służbę. Oto zapłata.",
            choices = listOf(
                DialogueChoice("Dziękuję. (ZAMKNIJ ZADANIE)", "end", onSelect = { state ->
                    val qId = state.pendingQuestId?.removePrefix("FINALIZE:") ?: "q_verdict_1"
                    questEngine.get().completeQuest(qId)
                    state.pendingQuestId = null
                })
            )
        ))

        // 2. MYSTIC
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "mystic",
            text = "Czuję w Tobie bicie Serca... lub jego brak. Czego pragnie dusza w świecie binarnym?",
            choices = listOf(
                DialogueChoice("Słyszałem o Krwawej Ikonie.", "mystic_quest_check"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "mystic_quest_check", npcId = "mystic",
            text = "Ikona płacze szumem. Musisz ją uciszyć, zanim Skryba ją wymaże.",
            choices = listOf(
                DialogueChoice("Zrobię to. (ZADANIE: Ikona)", "end", onSelect = {
                    questEngine.get().activateQuest("q_blood_icon")
                }),
                DialogueChoice("Nie tym razem.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "mystic_report_back", npcId = "mystic",
            text = "Echa ucichły. Przyjmij tę ofiarę za swój trud.",
            choices = listOf(
                DialogueChoice("Zrozumiałem. (ZAMKNIJ ZADANIE)", "end", onSelect = { state ->
                    val qId = state.pendingQuestId?.removePrefix("FINALIZE:") ?: "q_blood_icon"
                    questEngine.get().completeQuest(qId)
                    state.pendingQuestId = null
                })
            )
        ))
        
        // 3. GENERIC REPORT BACK (BUG #4 / #21)
        registerNode(DialogueNode(
            id = "quest_report_back_generic", npcId = "generic",
            text = "Dobra robota. To zadanie wymagało poświęcenia. Oto Twoja nagroda.",
            choices = listOf(
                DialogueChoice("Przyjmuję zapłatę. (ZAMKNIJ ZADANIE)", "end", onSelect = { state ->
                    val qId = state.pendingQuestId?.removePrefix("FINALIZE:") ?: return@DialogueChoice
                    questEngine.get().completeQuest(qId)
                    state.pendingQuestId = null
                })
            )
        ))
        
        // AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Prorok unosi głowę. 'Mgła rzednie w Twojej obecności. Czego szukasz?'",
            choices = listOf(
                DialogueChoice("Chcę poznać Twoją naturę.", "aelion_secret"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        
        registerNode(DialogueNode(
            id = "aelion_secret", npcId = "aelion",
            text = "A więc widzisz znaki pod moją skórą... Rzeczywistość nie wytrzyma tej prawdy!",
            choices = listOf(
                DialogueChoice("Powiedz mi wszystko.", "end", onSelect = {
                    chronicleSystem.get().unlock("lore_aelion_secret")
                })
            )
        ))
    }
}
