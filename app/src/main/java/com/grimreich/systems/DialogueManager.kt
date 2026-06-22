package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameConstants
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
        val baseNode = nodes[id] ?: return null
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
            "aelion" -> "port_priest"
            "merchant", "kupiec" -> "port_rogue"
            "zealot", "pielgrzym" -> "port_priest"
            "mystic", "mistyk" -> "port_mage"
            "guard", "straznik" -> "port_warrior"
            "xyrel" -> "port_knight"
            "mira" -> "port_mage"
            "sereth" -> "port_wraith"
            "ferrun" -> "port_barbarian"
            "noctyros" -> "port_demon"
            "incident" -> "port_skeleton"
            "alchemist", "alchemik" -> "port_alchemist"
            "beggar", "zebrak" -> "port_rogue"
            else -> "port_rogue"
        }
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val state = gameRepositoryProvider.get().currentState()
        val stability = state.world.globalStability

        if (stability >= GameConstants.STABILITY_THRESHOLD_HIGH) return node

        val fracturedText = if (stability < GameConstants.STABILITY_THRESHOLD_LOW) {
            glitchText(node.text) + " ...GŁOSY... ABSOLUT... [NIE SŁUCHAJ ICH] ...CISZA..."
        } else {
            node.text + " (rzeczywistosc wowkol ciebie zaczyna tracic nasycenie)"
        }

        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.1f) "[BLAD_ONTOLOGICZNY]" else word
        }.joinToString(" ")
    }

    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return
        // 1. GUARD
        registerNode(DialogueNode(
            id = "guard_start", npcId = "guard",
            text = "Stoj! Mgla gescieje, a prawo musi byc przestrzegane. Czego szukasz w cieniu murow?",
            choices = listOf(
                DialogueChoice("Szukam pracy (ZADANIA).", "guard_work"),
                DialogueChoice("Czy cos niepokojocego dzialo sie ostatnio? (MISJA)", "guard_quest_check"),
                DialogueChoice("Tylko przechodze.", "end")
            )
        ))
        registerNode(DialogueNode(id = "guard_work", npcId = "guard", text = "Zawsze potrzebujemy rak do pracy przy murach. Sprawdz tablice zadan w HUBie.", choices = listOf(DialogueChoice("Dziekuję.", "end"))))

        registerNode(DialogueNode(
            id = "guard_quest_check", npcId = "guard",
            text = "Zalyz kogo pytasz. Mieszczanie szepcza o 'Wyroku', ale we dnie pilnujemy tylko porzadku. Chociaz... jesli widzia脸色 jakieś 'Miejsce Zbrodni', to daj znać.",
            choices = listOf(
                DialogueChoice("Widziałem cos takiego.", "verdict_hook_start"),
                DialogueChoice("Bede mial oczy otwarte.", "end")
            )
        ))
        // 2. MERCHANT
        registerNode(DialogueNode(
            id = "merchant_start", npcId = "merchant",
            text = "Mam towary z Drugiej Strony. Zloto jest tu jedyna prawda. Chcesz handlowac?",
            choices = listOf(
                DialogueChoice("Pokaz oferte (OTWORZ TARG).", "end"),
                DialogueChoice("Czy slyszales o dziwnych relikwiach? (MISJA)", "merchant_quest_check"),
                DialogueChoice("Masz jakies plotki?", "merchant_rumors"),
                DialogueChoice("Moze innym razem.", "end")
            )
        ))
        registerNode(DialogueNode(id = "merchant_rumors", npcId = "merchant", text = "Mowia, ze Prorok Aelion ukrywa cos pod kaplica. Ale kto by sluchal kupca?", choices = listOf(DialogueChoice("Interesujace.", "end"))))

        registerNode(DialogueNode(
            id = "merchant_quest_check", npcId = "merchant",
            text = "Relikwie? Zawsze. Ale niektore sa przeklete. Mowia, ze krwawa ikona w pobliskiej wiosce zaczela plakac. To zly znak.",
            choices = listOf(
                DialogueChoice("Gdzie jest ta wioska? (ZADANIE)", "end", onSelect = {
                    it.activeQuests.add("q_blood_icon")
                }),
                DialogueChoice("Nie brzmi to dobrze.", "end")
            )
        ))
        // 3. CITIZEN
        registerNode(DialogueNode(
            id = "citizen_start", npcId = "merchant",
            text = "Dzien dobry... chociaz czy w GrimReich dni wciaz sa dobre? Kazdy rano sprawdza, czy jego odbicie w lustrze wciaz mroga w tym samym czasie.",
            choices = listOf(
                DialogueChoice("Co slychac w miescie?", "citizen_rumors"),
                DialogueChoice("Zegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "citizen_rumors", npcId = "merchant", text = "Mowia, ze straznicy znajduja ciala z napisem 'WINNI'. Boje sie wychodzic po zmroku.", choices = listOf(DialogueChoice("Badz ostrozny.", "end"))))
        // 3. PILGRIM / ZEALOT
        registerNode(DialogueNode(
            id = "zealot_start", npcId = "zealot",
            text = "Prorocy patrza! Czy Twoja dusza jest czysta, wedrowcze? Pielgrzymujemy do Serca Krainy, by obmyc sie w jeziorach prawdy.",
            choices = listOf(
                DialogueChoice("Jestem wierny.", "end"),
                DialogueChoice("Ofiaruj krew (HP-${GameConstants.ZEALOT_SACRIFICE_HP_LOSS})", "zealot_sacrifice", onSelect = {
                    it.party.forEach { h -> h.hp -= GameConstants.ZEALOT_SACRIFICE_HP_LOSS }
                }),
                DialogueChoice("Dokad dokladnie zmierzacie?", "zealot_destination")
            )
        ))
        registerNode(DialogueNode(id = "zealot_destination", npcId = "zealot", text = "Do Opactwa Ciszy. Tam, gdzie slowa traca znaczenie, a Absolut staje sie slyszalny.", choices = listOf(DialogueChoice("Powodzenia.", "end"))))
        registerNode(DialogueNode(id = "zealot_sacrifice", npcId = "zealot", text = "Twoja ofiara zostala przyjeta. Czuc mrowienie w kosciach.", choices = listOf(DialogueChoice("Idz w pokoju.", "end"))))
        //  4. MYSTIC
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "mystic",
            text = "Cien w Tobie rosnie. Absolut Cie wola, Kotwico. Widze wize bez drzwi w Twoich snach... czy ona juz tu jest?",
            choices = listOf(
                DialogueChoice("Powiedz mi o wiezy (ZADANIE).", "mystic_tower_info"),
                DialogueChoice("Kim jestes?", "mystic_who"),
                DialogueChoice("Nie interesuja mnie sny.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "mystic_tower_info", npcId = "mystic",
            text = "Ona pojawia sie tylko tam, gdzie smierc jest swieza. Szukaj jej na obrzezach miasta, posrod mgly. By wejsc, musisz przestac istniec na chwile.",
            choices = listOf(
                DialogueChoice("Jak moge 'przestac istniec'?", "mystic_tower_exist"),
                DialogueChoice("Rozumiem.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "mystic_tower_exist", npcId = "mystic",
            text = "To stan miedzy uderzeniami serca. Medytuj w ciszy ruin. Sproboj tego (UKONCZ ZADANIE: Wieza Bez Drzwi).",
            choices = listOf(
                DialogueChoice("Sproboje.", "end", onSelect = {
                    it.pendingQuestId = "COMPLETE:q_doorless_tower"
                }),
                DialogueChoice("To brzmi jak szalenstwo.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mystic_who", npcId = "mystic", text = "Jestem echem kogos, kto za dlugo patrzyl w Pekniecie. Widze wezly czasu, ktore probujesz rozplatac.", choices = listOf(DialogueChoice("To niepokojace.", "end"))))
        //  5. INCIDENT HOOK (VERDICT CHAIN)
        registerNode(DialogueNode(
            id = "verdict_hook_start", npcId = "incident",
            text = "Przed Toba leza zwloki straznika. Na scianie obok ktos nabazgral krwia: 'WINNI'. To juz trzeci taki przypadek w tym tygodniu.",
            choices = listOf(
                DialogueChoice("Zbadaj cialo (ZADANIE).", "verdict_hook_investigate"),
                DialogueChoice("Zawiadom straze.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "verdict_hook_investigate", npcId = "incident",
            text = "W zacisnietej piesci denata znajdujesz symbol wysokiego urzednika. Musisz sprawdzic jego gabinet w Twierdzy Zakonu.",
            choices = listOf(
                DialogueChoice("Podejmij sledztwo (START: Wyrok).", "end", onSelect = {
                    it.pendingQuestId = "q_verdict_1"
                })
            )
        ))
        // 6. BLOOD ICON QUEST
        registerNode(DialogueNode(
            id = "blood_icon_start", npcId = "zealot",
            text = "Wioska jest przestraszona. Statua placze krwia, ktora nigdy nie zasycha. Czy pomozez nam ja oczyscic?",
            choices = listOf(
                DialogueChoice("Pomoge Wam (WYPRAWA).", "end", onSelect = {
                    it.pendingQuestId = "COMBAT_WIN:q_blood_icon"
                }),
                DialogueChoice("Moja Kotwica jest zbyt slaba.", "end")
            )
        )
