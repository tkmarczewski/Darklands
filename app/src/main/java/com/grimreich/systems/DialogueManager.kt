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

    fun hasNode(id: String): Boolean = nodes.containsKey(id)

    fun listMissingTargets(): List<String> = nodes.values
        .flatMap { node -> node.choices.mapNotNull { it.targetNodeId } }
        .filterNot { target -> target == "end" || nodes.containsKey(target) }
        .distinct()

    fun makeChoice(choice: DialogueChoice): DialogueNode? {
        val repo = gameRepositoryProvider.get()
        val state = repo.currentState()
        
        choice.onSelect(state)
        state.trimLogs()
        
        val target = choice.targetNodeId
        if (target != "end" && getNode(target) == null) {
            repo.log("[Dialogue] Missing target node: $target")
            activeDialogueId = null
            return null
        }

        activeDialogueId = if (target == "end") null else target
        return activeDialogueId?.let { getNode(it) }
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
                    val rewardFlag = "reward_guard_report_back"
                    if (!s.grantedRewardFlags.contains(rewardFlag)) {
                        s.gold += 100
                        s.reputation.globalFactions["KNIGHTS"] = (s.reputation.globalFactions["KNIGHTS"] ?: 0) + 10
                        s.grantedRewardFlags.add(rewardFlag)
                    } else {
                        s.logEntries.add("Nagroda od strażnika została już odebrana.")
                    }
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
                DialogueChoice("Czy potrzebujesz pomocy?", "merchant_quest_check"),
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

        registerNode(DialogueNode(
            id = "merchant_quest_check", npcId = "merchant",
            text = "Archiwiści potrzebują rzadkich ziół z mgły. To niebezpieczna robota, ale dobrze płatna.",
            choices = listOf(
                DialogueChoice("Przyjmuję zlecenie (Żniwa Mgły).", "end", onSelect = { state ->
                    questEngine.get().activateQuest("q_coast_harvest")
                    state.pendingQuestId = null
                }),
                DialogueChoice("Może później.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "merchant_report_back", npcId = "merchant",
            text = "Dobra robota. Zioła dotarły całe. Możemy zamknąć sprawę. Oto Twoje złoto.",
            choices = listOf(
                DialogueChoice("W porządku.", "end", onSelect = { s ->
                    val flag = "reward_merchant_report_back"
                    if (!s.grantedRewardFlags.contains(flag)) {
                        s.gold += 50
                        s.grantedRewardFlags.add(flag)
                        s.logEntries.add("Odebrano nagrodę: Żniwa Mgły.")
                    }
                })
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
                DialogueChoice("Potrzebuję Twojej wiedzy.", "mira_quest_check"),
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

        registerNode(DialogueNode(
            id = "mira_quest_check", npcId = "mira",
            text = "Kronika została naruszona. Widzę w niej cienie, które nie powinny tam być. Pomożesz mi zbadać to?",
            choices = listOf(
                DialogueChoice("Przyjmuję zadanie Cieni.", "end", onSelect = { state ->
                    val engine = questEngine.get()
                    when {
                        engine.getStatus("q_scribes_1") == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuest("q_scribes_1")
                        engine.getStatus("q_scribes_2") == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuest("q_scribes_2")
                        engine.getStatus("q_scribes_3") == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuest("q_scribes_3")
                        engine.getStatus("q_collapse_core") == com.grimreich.core.QuestStatus.AVAILABLE -> engine.activateQuest("q_collapse_core")
                    }
                    state.pendingQuestId = null
                }),
                DialogueChoice("Nie teraz.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "mira_report_back", npcId = "mira",
            text = "Dobrze. Zamknijmy ten rozdział i zobaczmy, co odsłoni następny.",
            choices = listOf(
                DialogueChoice("Jestem gotów.", "end", onSelect = { s ->
                    val q = s.quest.progress.values.find { 
                        it.status == com.grimreich.core.QuestStatus.OBJECTIVE_MET && 
                        it.questId.startsWith("q_scribes_") 
                    }
                    q?.let {
                        val flag = "reward_${it.questId}"
                        if (!s.grantedRewardFlags.contains(flag)) {
                            s.grantedRewardFlags.add(flag)
                        }
                    }
                })
            )
        ))
    }
}
