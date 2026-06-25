package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameConstants
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.ReputationLevel
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DialogueManager @Inject constructor(
    private val gameRepositoryProvider: Lazy<GameRepository>
) {
    private val nodes = mutableMapOf<String, DialogueNode>()
    private var activeDialogueId: String? = null

    fun isDialogueActive() = activeDialogueId != null

    fun getActiveDialogueId() = activeDialogueId

    fun endDialogue() {
        activeDialogueId = null
    }

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val gameState = gameRepositoryProvider.get().currentState()
        
        // Handle Faction-Specific Start Nodes
        var targetId = id
        if (id.endsWith("_start")) {
            val baseRole = id.removeSuffix("_start")
            val factionId = when (baseRole) {
                "guard" -> "inkwizycja"
                "merchant" -> "pustka"
                "zealot" -> "zakon"
                "mystic" -> "milczenie"
                else -> null
            }
            
            if (factionId != null) {
                val score = gameState.reputation.globalFactions[factionId] ?: 0
                val level = ReputationLevel.fromScore(score)
                when (level) {
                    ReputationLevel.HATED -> targetId = "${baseRole}_hated"
                    ReputationLevel.HOSTILE -> targetId = "${baseRole}_hostile"
                    ReputationLevel.EXALTED -> targetId = "${baseRole}_exalted"
                    else -> {} // Use normal start
                }
            }
        }

        val baseNode = nodes[targetId] ?: nodes[id] ?: return null
        activeDialogueId = id
        return applyWorldEffects(baseNode)
    }

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        val state = gameRepositoryProvider.get().currentState()
        choice.onSelect(state)
        gameRepositoryProvider.get().persistCurrentState()

        val result = when (choice.targetNodeId) {
            "end" -> {
                endDialogue()
                null
            }
            else -> getNode(choice.targetNodeId)
        }
        return result
    }

    fun getPortrait(role: String): String {
        return when (role.lowercase()) {
            "aelion" -> "kaplan"
            "merchant", "kupiec" -> "zloto"
            "zealot", "pielgrzym" -> "kaplan"
            "mystic", "mistyk" -> "mag"
            "guard", "straznik" -> "rycerz"
            "xyrel" -> "rycerz"
            "mira" -> "mag"
            "sereth" -> "upior"
            "ferrun" -> "barbarzynca"
            "noctyros" -> "demon"
            "incident" -> "szkielet"
            "alchemist", "alchemik" -> "alchemik"
            "beggar", "zebrak" -> "lotr"
            else -> "lowca"
        }
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val state = gameRepositoryProvider.get().currentState()
        val stability = state.world.globalStability

        if (stability >= GameConstants.STABILITY_THRESHOLD_HIGH) return node

        val fracturedText = if (stability < GameConstants.STABILITY_THRESHOLD_LOW) {
            glitchText(node.text) + " ...GŁOSY... ${glitchText("ABSOLUT")}... [NIE SŁUCHAJ ICH] ...CISZA..."
        } else {
            node.text + " (rzeczywistość wokół ciebie zaczyna tracić nasycenie... i sens)"
        }

        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        val metaGlitches = listOf(
            "[BŁĄD_PARADYGMATU]",
            "[ZABIJ_PROCES]",
            "[NIEISTNIEJESZ]",
            "[ECHO_ABSOLUTU]",
            "[KOTWICA_ZERWANA]",
            "[CISZA_PROSZE]",
            "[01010110]"
        )
        return text.split(" ").map { word ->
            val rand = Random.nextFloat()
            when {
                rand < 0.05f -> metaGlitches.random()
                rand < 0.15f -> word.map { if (Random.nextFloat() < 0.2f) '?' else it }.joinToString("")
                else -> word
            }
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

        // GUARD FACTION NODES
        registerNode(DialogueNode(id = "guard_hostile", npcId = "guard", text = "Wracaj skąd przyszedłeś, Kotwico. Twój zapach kojarzy mi się ze zdradą. Inkwizycja Cię obserwuje.", choices = listOf(DialogueChoice("Już idę.", "end"))))
        registerNode(DialogueNode(id = "guard_hated", npcId = "guard", text = "Zabiję Cię, jeśli zrobisz jeszcze jeden krok. Dla Twojego rodzaju nie ma miejsca w Twierdzy.", choices = listOf(DialogueChoice("Spróbuj tylko. (WALKA)", "end"))))
        registerNode(DialogueNode(id = "guard_exalted", npcId = "guard", text = "Chwała Twoim czynom! Inkwizycja jest dumna z tak wiernej Kotwicy. Przejdź swobodnie.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        registerNode(DialogueNode(
            id = "guard_quest_check", npcId = "guard",
            text = "Mieszczanie szepczą o 'Wyroku'. Jeśli zobaczysz coś podejrzanego, daj znać. Szczególnie jeśli natrafisz na ślady morderstw.",
            choices = listOf(
                DialogueChoice("[Pokaż dowody] Mam trzy ślady 'Wyroku'.", "verdict_start_final", 
                    requiredAttributes = mapOf("perception" to 10)), // Requirement to check if they have it
                DialogueChoice("Będę pamiętał.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "verdict_start_final", npcId = "guard",
            text = "To już recydywa. Musisz natychmiast udać się do Twierdzy Zakonu i przeszukać gabinet urzędnika. To śledztwo jest teraz Twoim priorytetem.",
            choices = listOf(
                DialogueChoice("Podejmę się tego (START: Wyrok).", "end", onSelect = {
                    it.quest.activeQuests.add("q_verdict_1")
                    it.logEntries.add("Rozpoczęto śledztwo: Wyrok, którego nikt nie wydał.")
                })
            )
        ))

        // 2. MERCHANT
        registerNode(DialogueNode(
            id = "merchant_start", npcId = "merchant",
            text = "Mam towary z Drugiej Strony. Złoto jest tu jedyną prawdą. Chcesz handlować?",
            choices = listOf(
                DialogueChoice("Pokaż ofertę (OTWÓRZ TARG).", "end"),
                DialogueChoice("Czy słyszałeś o dziwnych relikwiach?", "merchant_quest_check"),
                DialogueChoice("Może innym razem.", "end")
            )
        ))

        // MERCHANT FACTION NODES
        registerNode(DialogueNode(id = "merchant_hostile", npcId = "merchant", text = "Zajęty jestem. Nie handluję z tymi, którzy psują mi rynek. Znikaj.", choices = listOf(DialogueChoice("Jasne.", "end"))))
        registerNode(DialogueNode(id = "merchant_exalted", npcId = "merchant", text = "Och! Moja ulubiona Kotwica! Dla Ciebie mam specjalne ceny i towary spod lady.", choices = listOf(DialogueChoice("Pokaż co masz.", "end"))))

        registerNode(DialogueNode(
            id = "merchant_quest_check", npcId = "merchant",
            text = "Mówią, że krwawa ikona w pobliskiej wiosce zaczęła płakać. To zły znak. Chcesz to sprawdzić?",
            choices = listOf(
                DialogueChoice("Gdzie jest ta wioska? (ZADANIE)", "end", onSelect = {
                    it.quest.activeQuests.add("q_blood_icon")
                }),
                DialogueChoice("Nie brzmi to dobrze.", "end")
            )
        ))

        // 3. CITIZEN
        registerNode(DialogueNode(
            id = "citizen_start", npcId = "citizen",
            text = "Dzień dobry... chociaż czy w GrimReich dni wciąż są dobre?",
            choices = listOf(
                DialogueChoice("Co słychać w mieście?", "citizen_rumors"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "citizen_rumors", npcId = "citizen", text = "Mówią, że strażnicy znajdują ciała z napisem 'WINNI'. Boję się wychodzić po zmroku.", choices = listOf(DialogueChoice("Bądź ostrożny.", "end"))))

        // 5. INCIDENT HOOK (VERDICT CHAIN)
        registerNode(DialogueNode(
            id = "verdict_hook_start", npcId = "incident",
            text = "Przed Tobą leżą zwłoki strażnika. Na ścianie obok ktoś nabazgrał krwią: 'WINNI'.",
            choices = listOf(
                DialogueChoice("Zbadaj ciało.", "verdict_hook_investigate"),
                DialogueChoice("Zawiadom straże.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "verdict_hook_investigate", npcId = "incident",
            text = "W zaciśniętej pięści denata znajdujesz symbol wysokiego urzędnika.",
            choices = listOf(
                DialogueChoice("Zbierz dowody.", "end", onSelect = {
                    it.world.verdictIncidentsSeen += 1
                    it.logEntries.add("Zebrano dowód z miejsca zbrodni (${it.world.verdictIncidentsSeen}/3).")
                    if (it.world.verdictIncidentsSeen >= 3) {
                         it.logEntries.add("Masz wystarczająco dowodów. Porozmawiaj ze Strażnikiem o 'Wyroku'.")
                    }
                })
            )
        ))

        // REGIONAL HERO: AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Mgła rzednie w Twojej obecności, Kotwico. Czego szukasz u Proroka?",
            choices = listOf(
                DialogueChoice("Jak mogę ustabilizować ten świat?", "aelion_stability"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "aelion_stability", npcId = "aelion", text = "Stabilność to iluzja. Szukaj Serca Krainy. Tam Mira pokaże Ci prawdę.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // QUEST RESOLUTION NODES
        registerNode(DialogueNode(
            id = "quest_report_back_generic", npcId = "generic",
            text = "Widzę, że zadanie zostało wykonane. Dobra robota, Kotwico. Oto Twoja zapłata.",
            choices = listOf( DialogueChoice("Dziękuję. (ODBIERZ NAGRODĘ)", "end") )
        ))
        registerNode(DialogueNode(
            id = "guard_report_back", npcId = "guard",
            text = "Stal i dyscyplina! Meldujesz wykonanie zadania? Doskonale. Przyjmij zapłatę.",
            choices = listOf( DialogueChoice("Ku chwale Zakonu. (ODBIERZ NAGRODĘ)", "end") )
        ))
        registerNode(DialogueNode(
            id = "merchant_report_back", npcId = "merchant",
            text = "Aha! Przynosisz dobre wieści? Złoto już czeka.",
            choices = listOf( DialogueChoice("Wymieńmy to na kruszec. (ODBIERZ NAGRODĘ)", "end") )
        ))
        registerNode(DialogueNode(
            id = "mystic_report_back", npcId = "mystic",
            text = "Echa ucichły... zrobiłeś to, co było konieczne. Przyjmij wdzięczność rzeczywistości.",
            choices = listOf( DialogueChoice("Zrozumiałem. (ODBIERZ NAGRODĘ)", "end") )
        ))
    }
}
