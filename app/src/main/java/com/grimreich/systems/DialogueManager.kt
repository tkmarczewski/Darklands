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
    private val gameRepositoryProvider: Lazy<GameRepository>,
    private val chronicleSystem: Lazy<ChronicleSystem>
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
        
        // Handle Déjà Vu (Meta-Awareness of previous sessions)
        if (id.endsWith("_start") && gameState.persistentMeta.totalSessionsFinished > 0) {
            val dejavuNode = "${id}_dejavu"
            if (nodes.containsKey(dejavuNode)) {
                activeDialogueId = dejavuNode
                return applyWorldEffects(nodes[dejavuNode]!!)
            }
        }

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
                DialogueChoice("Słyszałem o dzwonach bijących pod wodą...", "aelion_bells"),
                DialogueChoice("Kim naprawdę jest Absolut?", "aelion_absolute"),
                DialogueChoice("[PRZEKLETA WIEDZA] Chcę poznać Twoją prawdziwą naturę.", "aelion_secret_reveal", requiredAttributes = mapOf("intelligence" to 16)),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "aelion_stability", npcId = "aelion", text = "Stabilność to iluzja. Szukaj Serca Krainy. Tam Mira pokaże Ci prawdę.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // AELION DEJA VU
        registerNode(DialogueNode(
            id = "aelion_start_dejavu", npcId = "aelion",
            text = "Znowu? Kotwico, ile jeszcze razy musimy świadczyć tej samej pętli? Twoja obecność tutaj jest już niemal... wyryta w kodzie regionu.",
            choices = listOf(
                DialogueChoice("Tym razem będzie inaczej.", "aelion_start"),
                DialogueChoice("Nie rozumiem.", "aelion_start")
            )
        ))
        registerNode(DialogueNode(id = "aelion_bells", npcId = "aelion", text = "Zatopione katedry nie milczą, one krzyczą w języku, którego zapomnieliśmy. Kiedyś byliśmy całością. Teraz jesteśmy tylko odłamkami rozbitego witraża. Jeśli usłyszysz dzwony, nie idź w stronę wody. To one przyciągnęły Pęknięcie.", choices = listOf(DialogueChoice("Będę pamiętał.", "aelion_start"))))
        registerNode(DialogueNode(id = "aelion_absolute", npcId = "aelion", text = "Absolut to nie bóg. To Architekt, który porzucił plac budowy, zostawiając nas w niedokończonym świecie. My jesteśmy tylko błędami w jego wielkim planie, próbującymi nadać sens własnemu nieistnieniu.", choices = listOf(DialogueChoice("To mroczna wizja.", "aelion_start"))))

        // AELION ENDING NODES
        registerNode(DialogueNode(
            id = "aelion_final", npcId = "aelion",
            text = "Dzwony Wybrzeża biją po raz ostatni. Pęknięcie mnie wzywa. Mogę stać się częścią Twojej drużyny i opóźnić Epilog... albo zniknąć w Nadziei.",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Twoja wiedza jest nam potrzebna.", "end", factionId = "zakon", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:aelion"
                }),
                DialogueChoice("[ASCENDENCJA] Twoja Nadzieja musi trwać wiecznie.", "aelion_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                }),
                DialogueChoice("Niech morze Cię zabierze.", "end")
            )
        ))
        registerNode(DialogueNode(id = "aelion_ascend", npcId = "aelion", text = "Będę wieczną latarnią dla tych, którzy się zagubili. [AELION ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Do widzenia, Proroku.", "end"))))

        registerNode(DialogueNode(
            id = "aelion_secret_reveal", npcId = "aelion",
            text = "A więc widzisz znaki pod moją skórą... Rzeczywistość nie wytrzyma tej prawdy! [EKRAN MIGOCZE]",
            choices = listOf(
                DialogueChoice("Powiedz mi wszystko.", "end", onSelect = {
                    chronicleSystem.get().unlock("lore_aelion_secret")
                    it.world.echoIntensity += 0.2f
                    // Trigger visual glitch effect through some state change if possible
                })
            )
        ))

        // REGIONAL HERO: MIRA (NEW)
        registerNode(DialogueNode(
            id = "mira_start", npcId = "mira",
            text = "Spójrz w taflę jeziora. Widzisz to? Twoje odbicie mrugnęło sekundę za późno. Witaj w Sercu Krainy, gdzie prawda jest tylko jednym z wariantów.",
            choices = listOf(
                DialogueChoice("Czym jest Wielkie Zwierciadło?", "mira_mirror"),
                DialogueChoice("Czy moje odbicie może mi zaszkodzić?", "mira_danger"),
                DialogueChoice("Odejdź.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mira_mirror", npcId = "mira", text = "To soczewka Absolutu. Przez nią widzą nas Skrybowie. Myślą, że jesteśmy tylko atramentem na papierze, ale my czujemy ból. My krwawimy echem.", choices = listOf(DialogueChoice("Kim są Skrybowie?", "mira_scribes"))))
        registerNode(DialogueNode(id = "mira_scribes", npcId = "mira", text = "Bytami z wyższego wymiaru paradygmatu. Piszą naszą historię w czasie rzeczywistym. Czasami popełniają błędy... i tak powstają anomalie.", choices = listOf(DialogueChoice("Przerażające.", "mira_start"))))
        registerNode(DialogueNode(id = "mira_danger", npcId = "mira", text = "Twoje odbicie to Twoja potencjalność. Jeśli ono wyjdzie z lustra, Ty będziesz musiał wejść do środka. W Sercu Krainy nie ma miejsca dla dwóch wersji tej samej duszy.", choices = listOf(DialogueChoice("Będę uważać.", "mira_start"))))

        // MIRA ENDING NODES
        registerNode(DialogueNode(
            id = "mira_final", npcId = "mira",
            text = "Lustra pękają. Skryba traci cierpliwość do mojego Serca. Muszę wybrać: zostać tu i wyparować, lub dołączyć do Twojej Kotwicy.",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Chodź ze mną. Razem naprawimy świat.", "end", factionId = "milczenie", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:mira"
                }),
                DialogueChoice("[ASCENDENCJA] Poświęć się dla stabilności krainy.", "mira_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                }),
                DialogueChoice("Nie potrzebuję kolejnego cienia.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mira_ascend", npcId = "mira", text = "Zrozumiałam. Stanę się światłem wewnątrz zwierciadła. [MIRA ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Żegnaj, Miro.", "end"))))

        // FERRUN ENDING NODES
        registerNode(DialogueNode(
            id = "ferrun_final", npcId = "ferrun",
            text = "Kopalnie są puste. Wykopaliśmy wszystko, co materialne. Został tylko ciężar. Pozwolisz mi nieść go razem z Tobą?",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Twoja stal nam się przyda.", "end", factionId = "inkwizycja", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:ferrun"
                }),
                DialogueChoice("[ASCENDENCJA] Zablokuj Głębię swoim życiem.", "ferrun_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                }),
                DialogueChoice("Góry to Twoje miejsce.", "end")
            )
        ))
        registerNode(DialogueNode(id = "ferrun_ascend", npcId = "ferrun", text = "Zostaję na dnie. Moje ciało zamieni się w żelazo, by nicość nie przeszła dalej. [FERRUN ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Twoja ofiara nie zostanie zapomniana.", "end"))))

        // NOCTYROS ENDING NODES
        registerNode(DialogueNode(
            id = "noctyros_final", npcId = "noctyros",
            text = "Pęknięcie jest zbyt szerokie. Sesja dobiega końca. Widzę Epilog na horyzoncie. Mogę wejść do Twojej drużyny jako błąd systemowy... albo wrócić do kodu.",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Potrzebujemy kogoś, kto widzi kod.", "end", factionId = "pustka", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:noctyros"
                }),
                DialogueChoice("[ASCENDENCJA] Napraw Pęknięcie swoją esencją.", "noctyros_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                }),
                DialogueChoice("Znikaj w swojej nicości.", "end")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_ascend", npcId = "noctyros", text = "Nadpisuję dane... Rzeczywistość... odświeżona. [NOCTYROS ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // REGIONAL HERO: FERRUN (NEW)
        registerNode(DialogueNode(
            id = "ferrun_start", npcId = "ferrun",
            text = "Ciężar... czujesz go? Grawitacja w tych górach to nie fizyka, to poczucie winy tego świata. Co sprowadza Cię do Głębi?",
            choices = listOf(
                DialogueChoice("Szukam broni przeciw Drugiej Stronie.", "ferrun_weapon"),
                DialogueChoice("Dlaczego kopalnie są tak głębokie?", "ferrun_mines"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "ferrun_weapon", npcId = "ferrun", text = "Wykuwamy stal z Ciemności. Ale pamiętaj – broń, która może ranić echa, powoli zmienia swojego właściciela w jedno z nich. Każdy cios to pęknięcie w Twoim człowieczeństwie.", choices = listOf(DialogueChoice("Jestem gotów na to ryzyko.", "ferrun_start"))))
        registerNode(DialogueNode(id = "ferrun_mines", npcId = "ferrun", text = "Kopiemy, by znaleźć dno rzeczywistości. Chcemy sprawdzić, na czym to wszystko stoi. Obawiam się jednak, że pod spodem jest tylko nieskończona, głodna nicość.", choices = listOf(DialogueChoice("Obyście się mylili.", "ferrun_start"))))

        // REGIONAL HERO: NOCTYROS (NEW - META AWARE)
        registerNode(DialogueNode(
            id = "noctyros_start", npcId = "noctyros",
            text = "Ach, główny bohater. Widzę, że Twoja Kotwica jest silna... tym razem. Stepy Pogranicza to margines tego świata. Tutaj widać szwy rzeczywistości.",
            choices = listOf(
                DialogueChoice("Co masz na myśli mówiąc 'główny bohater'?", "noctyros_meta"),
                DialogueChoice("Czym jest Pęknięcie?", "noctyros_fracture"),
                DialogueChoice("Jak mogę uratować GrimReich?", "noctyros_save"),
                DialogueChoice("Jesteś szalony. Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_meta", npcId = "noctyros", text = "Jesteś procesem, który próbuje naprawić uszkodzone dane. Ten świat to tylko SessionState, a Ty jesteś jego jedyną szansą na odświeżenie. Ale uważaj... Skryba może w każdej chwili zamknąć aplikację rzeczywistości.", choices = listOf(DialogueChoice("Nic nie rozumiem.", "noctyros_start"))))

        // NOCTYROS DEJA VU
        registerNode(DialogueNode(
            id = "noctyros_start_dejavu", npcId = "noctyros",
            text = "Ach, wskaźnik do poprzedniej sesji powraca. Widzę, że 'Anchor_Save' zadziałał bez zarzutu. Czego szukasz w tym starym marginesie?",
            choices = listOf(
                DialogueChoice("Szukam ostatecznego Epilogu.", "noctyros_start"),
                DialogueChoice("Nie nazywaj mnie wskaźnikiem.", "noctyros_start")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_fracture", npcId = "noctyros", text = "To błąd logiczny. Dwa wymiary nałożyły się na siebie, bo ktoś zapomniał o warunkach brzegowych. Druga Strona to po prostu to, co nie powinno istnieć, a jednak zajmuje miejsce w pamięci świata.", choices = listOf(DialogueChoice("Mówisz zagadkami.", "noctyros_start"))))
        registerNode(DialogueNode(id = "noctyros_save", npcId = "noctyros", text = "Nie możesz uratować czegoś, co jest zaprojektowane, by upaść. Możesz tylko przetrwać wystarczająco długo, by zobaczyć Epilog. Ale czy wybierzesz zakończenie Materialne, czy Meta-Narracyjne... to zależy od Twoich 'wyborów'.", choices = listOf(DialogueChoice("Zrobię co w mojej mocy.", "noctyros_start"))))

        // GUARD PERSONALITY VARIATIONS
        registerNode(DialogueNode(id = "guard_normal_start", npcId = "guard", text = "Stój! Prawo musi być przestrzegane. Czego szukasz?", choices = listOf(DialogueChoice("Tylko przechodzę.", "end"))))
        registerNode(DialogueNode(id = "guard_fanatic_start", npcId = "guard", text = "W Imię Absolutu! Czy Twoja dusza jest czysta od błędów Pęknięcia? Każdy obcy to potencjalna anomalia!", choices = listOf(DialogueChoice("Jestem wierny.", "end"))))
        registerNode(DialogueNode(id = "guard_weary_start", npcId = "guard", text = "Kolejna Kotwica... Czy to się kiedyś skończy? Przejdź szybko, zanim mgła znów namiesza mi w głowie.", choices = listOf(DialogueChoice("Dziękuję za zrozumienie.", "end"))))

        // MERCHANT PERSONALITY VARIATIONS
        registerNode(DialogueNode(id = "merchant_normal_start", npcId = "merchant", text = "Złoto to jedyna prawda. Chcesz handlować?", choices = listOf(DialogueChoice("Pokaż ofertę.", "end"))))
        registerNode(DialogueNode(id = "merchant_greedy_start", npcId = "merchant", text = "Widzę, że masz pełny trzos... Ceny poszły w górę przez to całe Pęknięcie. Płać albo znikaj.", choices = listOf(DialogueChoice("Zobaczymy...", "end"))))
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

        // --- DATA GHOST DIALOGUES ---
        registerNode(DialogueNode(
            id = "data_ghost_start", npcId = "ghost",
            text = "Czekaj... Czy Ty też to widzisz? To nie jest prawdziwe słońce. To tylko tekstura. Twoja Kotwica... widzę jej ID.",
            choices = listOf(
                DialogueChoice("O czym Ty mówisz?", "ghost_meta_info"),
                DialogueChoice("Czy jesteś błędem?", "ghost_error"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "ghost_meta_info", npcId = "ghost", text = "Twoje wybory... myślisz, że są Twoje? Skryba już dawno je przewidział. Widziałem Twój SaveState. Próbujesz naprawić coś, co zostało uszkodzone u podstaw.", choices = listOf(DialogueChoice("Dziwne...", "data_ghost_start"))))
        registerNode(DialogueNode(id = "ghost_error", npcId = "ghost", text = "Jestem śmieciem, który nie został wyczyszczony. Fragmentem poprzedniej sesji. Jeśli dotkniesz mojego echa, sam możesz stać się tylko linią w WorldLogu.", choices = listOf(DialogueChoice("Niebezpieczne.", "data_ghost_start"))))
    }
}
