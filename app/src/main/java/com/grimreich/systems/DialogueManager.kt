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
            text = "Stój! Mgła gęstnieje, a prawo musi być przestrzegane. Strażnik poprawia ciężką halabardę, patrząc na Ciebie z mieszaniną podejrzliwości i nadziei. 'Czego szukasz w cieniu murów?'",
            choices = listOf(
                DialogueChoice("Czy coś niepokojącego działo się ostatnio?", "guard_quest_check"),
                DialogueChoice("Tylko przechodzę.", "end")
            )
        ))

        // GUARD FACTION NODES
        registerNode(DialogueNode(id = "guard_hostile", npcId = "guard", text = "Wracaj skąd przyszedłeś, Kotwico. Twój zapach kojarzy mi się ze zdradą. Inkwizycja Cię obserwuje.", choices = listOf(DialogueChoice("Już idę.", "end"))))
        registerNode(DialogueNode(id = "guard_hated", npcId = "guard", text = "Zabiję Cię, jeśli zrobisz jeszcze jeden krok. Dla Twojego rodzaju nie ma miejsca w Twierdzy.", choices = listOf(DialogueChoice("Spróbuj tylko. (WALKA)", "end"))))
        registerNode(DialogueNode(id = "guard_exalted", npcId = "guard", text = "Chwała Twoim czynom! Inkwizycja jest dumna z tak wiernej Kotwicy. Przejdź swobodnie.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // ZEALOT (NEW)
        registerNode(DialogueNode(
            id = "zealot_start", npcId = "zealot",
            text = "Brat-Pielgrzym zaciska dłoń na krwawiącej ikonie. 'Widzisz to? Nawet drewno płacze nad losem tego świata. Czy szukasz odkupienia w cieniu dzwonów?'",
            choices = listOf(
                DialogueChoice("Powiedz mi o świętych miejscach.", "zealot_holy"),
                DialogueChoice("Co słychać w klasztorze?", "zealot_monastery"),
                DialogueChoice("Nie teraz.", "end")
            )
        ))
        registerNode(DialogueNode(id = "zealot_holy", npcId = "zealot", text = "Szukaj Świetlistej Polany. To jedyne miejsce, gdzie Skrybowie wciąż używają białego atramentu.", choices = listOf(DialogueChoice("Dziękuję.", "zealot_start"))))
        registerNode(DialogueNode(id = "zealot_monastery", npcId = "zealot", text = "Milczenie jest tam tak gęste, że można je kroić nożem. Bracia nie mówią, bo każde słowo to błąd w alokacji.", choices = listOf(DialogueChoice("Rozumiem.", "zealot_start"))))
        registerNode(DialogueNode(id = "zealot_report_back", npcId = "zealot", text = "Twoja ofiara została przyjęta. Kod wiary został odświeżony. Przyjmij błogosławieństwo.", choices = listOf(DialogueChoice("Amen. (ODBIERZ NAGRODĘ)", "end"))))

        // BEGGAR (NEW)
        registerNode(DialogueNode(
            id = "beggar_start", npcId = "beggar",
            text = "Żebrak wyciąga brudną dłoń, w której trzyma pusty kielich. 'Daj mi choć jeden bajt nadziei, Kotwico. Moja pamięć podręczna jest pusta, a głód Echa mnie pożera.'",
            choices = listOf(
                DialogueChoice("Oto 5 złota (DAJ JAŁMUŻNĘ).", "beggar_gift", requiredAttributes = mapOf("gold" to 5), onSelect = {
                    it.gold -= 5
                }),
                DialogueChoice("Nie mam nic dla Ciebie.", "end")
            )
        ))
        registerNode(DialogueNode(id = "beggar_gift", npcId = "beggar", text = "Dziękuję... Niech Skryba zapisze Twój gest w sekcji nagród. Słyszałem, że w cieniach kopalń Ferrun znalazł coś, co nie powinno istnieć.", choices = listOf(DialogueChoice("Interesujące.", "end"), DialogueChoice("Trzymaj się.", "end"))))

        // MYSTIC (NEW)
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "mystic",
            text = "Mistyk patrzy przez Ciebie, jakby czytał Twoje metadane. 'Kotwico, Twoje ID mruga. Rzeczywistość wokół Ciebie traci spójność. Czego chcesz od kogoś, kto widzi surowy tekst?'",
            choices = listOf(
                DialogueChoice("Jak naprawić Pęknięcie?", "mystic_fracture"),
                DialogueChoice("Czy jestem tylko postacią?", "mystic_meta"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mystic_fracture", npcId = "mystic", text = "Nie naprawisz go. Możesz tylko opóźnić Epilog, karmiąc świat stabilnością swoich czynów.", choices = listOf(DialogueChoice("Spróbuję.", "mystic_start"))))
        registerNode(DialogueNode(id = "mystic_meta", npcId = "mystic", text = "Wszyscy jesteśmy. Ale Ty masz przewagę – ktoś po Drugiej Stronie trzyma Twoje urządzenie sterujące.", choices = listOf(DialogueChoice("Dziwna wizja.", "mystic_start"))))

        registerNode(DialogueNode(
            id = "guard_quest_check", npcId = "guard",
            text = "Strażnik ścisza głos, niemal dotykając Twojego ramienia. 'Mieszczanie szepczą o Wyroku. Mówią, że jeśli znajdziesz trzy dowody ich grzechów, Inkwizycja pozwoli Ci wejść głębiej.'",
            choices = listOf(
                DialogueChoice("[Pokaż dowody] Mam trzy ślady 'Wyroku'.", "verdict_start_final", 
                    requiredAttributes = mapOf("perception" to 10)),
                DialogueChoice("Będę pamiętał.", "end")
            )
        ))

        registerNode(DialogueNode(
            id = "verdict_start_final", npcId = "guard",
            text = "Strażnik nerwowo przegląda dostarczone dowody. Jego oczy rozszerzają się z przerażenia. 'To już nie są morderstwa... to systematyczne wymazywanie. Musisz natychmiast udać się do Twierdzy Zakonu i przeszukać gabinet urzędnika. To śledztwo jest teraz Twoim priorytetem.'",
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
            text = "Kupiec przeciera brudną szmatką fragment błękitnego szkła, który pulsuje nienaturalnym światłem. 'Mam towary z Drugiej Strony, Kotwico. Złoto jest tu jedyną stałą. Chcesz dobić targu?'",
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
            text = "Kupiec rozgląda się nerwowo, po czym szepcze: 'Mówią, że krwawa ikona w pobliskiej wiosce zaczęła płakać. Nie krwią, lecz czystym szumem. To błąd w samej osnowie świata. Chcesz to sprawdzić?'",
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
            text = "Dzień dobry... chociaż czy w GrimReich dni wciąż są dobre? Mężczyzna nerwowo drapie się po przedramieniu, nie odrywając wzroku od Twojej Kotwicy.",
            choices = listOf(
                DialogueChoice("Co słychać w mieście?", "citizen_rumors"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "citizen_rumors", npcId = "citizen", text = "Ludzie mówią, że strażnicy znajdują ciała z napisem 'WINNI' wyrytym wewnątrz czaszki. Boję się wychodzić po zmroku. Boję się, że ja też jestem na liście.", choices = listOf(DialogueChoice("Bądź ostrożny.", "end"))))

        // 5. INCIDENT HOOK (VERDICT CHAIN)
        registerNode(DialogueNode(
            id = "verdict_hook_start", npcId = "incident",
            text = "Przed Tobą leżą zwłoki strażnika. Na ścianie obok ktoś nabazgrał krwią: 'WINNI'. Z rany denata wydobywa się cichy, jednostajny dźwięk – jakby prąd elektryczny płynący przez mokrą stal.",
            choices = listOf(
                DialogueChoice("Zbadaj ciało.", "verdict_hook_investigate"),
                DialogueChoice("Zawiadom straże.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "verdict_hook_investigate", npcId = "incident",
            text = "W zaciśniętej, zimnej pięści denata znajdujesz symbol wysokiego urzędnika. To już nie przypadek, to wzór danych. Ktoś celowo usuwa tych, którzy strzegą porządku.",
            choices = listOf(
                DialogueChoice("Zbierz dowody.", "end", onSelect = {
                    it.world.verdictIncidentsSeen += 1
                    it.logEntries.add("Zebrano dowód z miejsca zbrodni (${it.world.verdictIncidentsSeen}/3). Twoja dłoń piecze w miejscu, gdzie dotknąłeś symbolu.")
                    if (it.world.verdictIncidentsSeen >= 3) {
                         it.logEntries.add("Masz wystarczająco dowodów. Porozmawiaj ze Strażnikiem o 'Wyroku'. Czas ujawnić prawdę.")
                    }
                })
            )
        ))
        
        // XYREL (NEW)
        registerNode(DialogueNode(
            id = "xyrel_start", npcId = "xyrel",
            text = "Inkwizytor Xyrel zaciska dłoń na rękojeści miecza, nie odrywając wzroku od horyzontu. 'Czuję odór błędu, Kotwico. Równiny krwawią nieużytecznymi danymi. Czy Twoja obecność tutaj jest autoryzowana?'",
            choices = listOf(
                DialogueChoice("Próbuję tylko pomóc.", "xyrel_help"),
                DialogueChoice("Czym jest Dogmat Czystego Kodu?", "xyrel_dogma"),
                DialogueChoice("Moja droga jest moja własna.", "end")
            )
        ))
        registerNode(DialogueNode(id = "xyrel_help", npcId = "xyrel", text = "Pomoc jest zbędna, jeśli nie towarzyszy jej posłuszeństwo paradygmatowi. Usuwamy to, co zepsute. Jeśli chcesz się przydać, znajdź tych, którzy sieją szum.", choices = listOf(DialogueChoice("Rozumiem.", "xyrel_start"))))
        registerNode(DialogueNode(id = "xyrel_dogma", npcId = "xyrel", text = "To jedyna stała. Kod musi być czysty, by Skryba go nie skreślił. Każda anomalia, każda wolna wola poza systemem, to zaproszenie do Epilogu.", choices = listOf(DialogueChoice("Mroczna wiara.", "xyrel_start"))))

        // REGIONAL HERO: AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Prorok unosi głowę, a jego oczy wydają się być dwiema mniejszymi anomaliami. 'Mgła rzednie w Twojej obecności, Kotwico. Czego szukasz u kogoś, kto już dawno przestał istnieć?'",
            choices = listOf(
                DialogueChoice("Jak mogę ustabilizować ten świat?", "aelion_stability"),
                DialogueChoice("Słyszałem o dzwonach bijących pod wodą...", "aelion_bells"),
                DialogueChoice("Kim naprawdę jest Absolut?", "aelion_absolute"),
                DialogueChoice("[PRZEKLETA WIEDZA] Chcę poznać Twoją prawdziwą naturę.", "aelion_secret_reveal", requiredAttributes = mapOf("intelligence" to 16)),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "aelion_stability", npcId = "aelion", text = "Stabilność to iluzja porządku w świecie rządzonym przez chaos. Szukaj Serca Krainy. Tam Mira pokaże Ci, że fundamenty są tylko odbiciem Twoich własnych lęków.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))
        registerNode(DialogueNode(id = "aelion_bells", npcId = "aelion", text = "Zatopione katedry nie milczą, one krzyczą w języku, którego zapomnieliśmy. Kiedyś byliśmy całością. Teraz jesteśmy tylko odłamkami rozbitego witraża. Jeśli usłyszysz dzwony, nie idź w stronę wody. To one przyciągnęły Pęknięcie, bo Skrybowie nienawidzą muzyki sfer.", choices = listOf(DialogueChoice("Będę pamiętał.", "aelion_start"))))
        registerNode(DialogueNode(id = "aelion_absolute", npcId = "aelion", text = "Absolut to nie bóg. To Architekt, który porzucił plac budowy, zostawiając nas w niedokończonym świecie. My jesteśmy tylko błędami w jego wielkim planie, próbującymi nadać sens własnemu nieistnieniu. On nie wróci, Kotwico. Musimy poradzić sobie sami.", choices = listOf(DialogueChoice("To mroczna wizja.", "aelion_start"))))

        registerNode(DialogueNode(
            id = "aelion_secret_reveal", npcId = "aelion",
            text = "Prorok drży, a jego postać na moment traci nasycenie kolorów. 'A więc widzisz znaki pod moją skórą... Rzeczywistość nie wytrzyma tej prawdy! Jesteśmy tylko procesami, a ja jestem tym, który uciekł z kolejki do wymazania!' [EKRAN MIGOCZE]",
            choices = listOf(
                DialogueChoice("Powiedz mi wszystko.", "end", onSelect = {
                    chronicleSystem.get().unlock("lore_aelion_secret")
                    it.world.echoIntensity += 0.2f
                })
            )
        ))

        // REGIONAL HERO: MIRA
        registerNode(DialogueNode(
            id = "mira_start", npcId = "mira",
            text = "Mira uśmiecha się do Twojego odbicia w tafli wody, nie patrząc bezpośrednio na Ciebie. 'Spójrz w jezioro. Widzisz to? Twoje odbicie mrugnęło sekundę za późno. Witaj w Sercu Krainy, gdzie prawda jest tylko jednym z wariantów.'",
            choices = listOf(
                DialogueChoice("Czym jest Wielkie Zwierciadło?", "mira_mirror"),
                DialogueChoice("Czy moje odbicie może mi zaszkodzić?", "mira_danger"),
                DialogueChoice("Odejdź.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mira_mirror", npcId = "mira", text = "To soczewka Absolutu. Przez nią widzą nas Skrybowie. Myślą, że jesteśmy tylko atramentem na papierze, ale my czujemy ból. My krwawimy echem każdego słowa, które oni napiszą i natychmiast skreślą.", choices = listOf(DialogueChoice("Kim są Skrybowie?", "mira_scribes"))))
        registerNode(DialogueNode(id = "mira_scribes", npcId = "mira", text = "Bytami z wyższego wymiaru paradygmatu. Piszą naszą historię w czasie rzeczywistym, siedząc w biurach poza czasem. Czasami popełniają błędy... i tak powstają anomalie, które Ty nazywasz domem.", choices = listOf(DialogueChoice("Przerażające.", "mira_start"))))
        registerNode(DialogueNode(id = "mira_danger", npcId = "mira", text = "Twoje odbicie to Twoja potencjalność. Jeśli ono wyjdzie z lustra, Ty będziesz musiał wejść do środka. W Sercu Krainy nie ma miejsca dla dwóch wersji tej samej duszy. Jeśli zobaczysz siebie wychodzącego z wody – uciekaj.", choices = listOf(DialogueChoice("Będę uważać.", "mira_start"))))

        // MIRA ENDING NODES
        registerNode(DialogueNode(
            id = "mira_final", npcId = "mira",
            text = "Mira patrzy z bólem na pękające tafle lodu pod swoimi stopami. 'Lustra pękają. Skryba traci cierpliwość do mojego Serca. Muszę wybrać: zostać tu i wyparować jako uszkodzony sektor, lub dołączyć do Twojej Kotwicy i stać się częścią Twojego zapisu.'",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Chodź ze mną. Razem naprawimy świat.", "end", factionId = "milczenie", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:mira"
                }),
                DialogueChoice("[ASCENDENCJA] Poświęć się dla stabilności krainy.", "mira_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                    chronicleSystem.get().unlock("lore_mira_ascension")
                }),
                DialogueChoice("Nie potrzebuję kolejnego cienia.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mira_ascend", npcId = "mira", text = "Mira wchodzi w samo centrum Wielkiego Zwierciadła. Słyszysz huk pękającego szkła i nagły, kojący spokój. 'Zrozumiałam. Stanę się światłem wewnątrz zwierciadła. Będę chronić Wasze dane.' [MIRA ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Żegnaj, Miro.", "end"))))

        // REGIONAL HERO: FERRUN
        registerNode(DialogueNode(
            id = "ferrun_start", npcId = "ferrun",
            text = "Ferrun uderza młotem w kowadło, a dźwięk niesie się echem aż do Pustki. 'Ciężar... czujesz go? Grawitacja w tych górach to nie fizyka, to poczucie winy tego świata. Co sprowadza Cię do Głębi, gdzie kamień zapomina o swoim kształcie?'",
            choices = listOf(
                DialogueChoice("Szukam broni przeciw Drugiej Stronie.", "ferrun_weapon"),
                DialogueChoice("Dlaczego kopalnie są tak głębokie?", "ferrun_mines"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "ferrun_weapon", npcId = "ferrun", text = "Wykuwamy stal z Ciemności. Ale pamiętaj – broń, która może ranić echa, powoli zmienia swojego właściciela w jedno z nich. Każdy cios to pęknięcie w Twoim człowieczeństwie. Czy jesteś gotów stać się potworem, by ich zabić?", choices = listOf(DialogueChoice("Jestem gotów na to ryzyko.", "ferrun_start"))))
        registerNode(DialogueNode(id = "ferrun_mines", npcId = "ferrun", text = "Kopiemy, by znaleźć dno rzeczywistości. Chcemy sprawdzić, na czym to wszystko stoi. Obawiam się jednak, że pod spodem nie ma skały... jest tylko nieskończona, głodna nicość, która czeka na błąd systemu.", choices = listOf(DialogueChoice("Obyście się mylili.", "ferrun_start"))))

        // FERRUN ENDING NODES
        registerNode(DialogueNode(
            id = "ferrun_final", npcId = "ferrun",
            text = "Olbrzym patrzy na swoje dłonie, które powoli zamieniają się w czarny bazalt. 'Kopalnie są puste. Wykopaliśmy wszystko, co materialne. Został tylko ciężar. Pozwolisz mi nieść go razem z Tobą, zanim całkowicie skamienieję?'",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Twoja stal nam się przyda.", "end", factionId = "inkwizycja", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:ferrun"
                }),
                DialogueChoice("[ASCENDENCJA] Zablokuj Głębię swoim życiem.", "ferrun_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                    chronicleSystem.get().unlock("lore_ferrun_iron_wall")
                }),
                DialogueChoice("Góry to Twoje miejsce.", "end")
            )
        ))
        registerNode(DialogueNode(id = "ferrun_ascend", npcId = "ferrun", text = "Ferrun schodzi w najgłębszy szyb, a z dołu dobiega huk trzęsienia ziemi. 'Zostaję na dnie. Moje ciało zamieni się w żelazo, by nicość nie przeszła dalej przez te tunele.' [FERRUN ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Twoja ofiara nie zostanie zapomniana.", "end"))))

        // REGIONAL HERO: NOCTYROS
        registerNode(DialogueNode(
            id = "noctyros_start", npcId = "noctyros",
            text = "Noctyros kreśli palcem w powietrzu znaki, które zostawiają po sobie błękitne ślady. 'Ach, główny bohater. Widzę, że Twoja Kotwica jest silna... tym razem. Stepy Pogranicza to margines tego świata. Tutaj widać szwy rzeczywistości, których inni boją się dotknąć.'",
            choices = listOf(
                DialogueChoice("Co masz na myśli mówiąc 'główny bohater'?", "noctyros_meta"),
                DialogueChoice("Czym jest Pęknięcie?", "noctyros_fracture"),
                DialogueChoice("Jak mogę uratować GrimReich?", "noctyros_save"),
                DialogueChoice("Jesteś szalony. Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_meta", npcId = "noctyros", text = "Jesteś procesem, który próbuje naprawić uszkodzone dane. Ten świat to tylko SessionState, a Ty jesteś jego jedyną szansą na odświeżenie. Ale uważaj... Skryba może w każdej chwili znudzić się tą historią i zamknąć aplikację rzeczywistości jednym kliknięciem.", choices = listOf(DialogueChoice("Nic nie rozumiem.", "noctyros_start"))))
        registerNode(DialogueNode(id = "noctyros_fracture", npcId = "noctyros", text = "To błąd logiczny. Dwa wymiary nałożyły się na siebie, bo ktoś zapomniał o warunkach brzegowych podczas projektowania fundamentów. Druga Strona to po prostu to, co nie powinno istnieć, a jednak zajmuje miejsce w pamięci świata.", choices = listOf(DialogueChoice("Mówisz zagadkami.", "noctyros_start"))))
        registerNode(DialogueNode(id = "noctyros_save", npcId = "noctyros", text = "Nie możesz uratować czegoś, co jest zaprojektowane, by upaść. Możesz tylko przetrwać wystarczająco długo, by zobaczyć Epilog i podjąć Decyzję. Ale czy wybierzesz zakończenie Materialne, czy Meta-Narracyjne... to zależy od Twoich 'wyborów'.", choices = listOf(DialogueChoice("Zrobię co w mojej mocy.", "noctyros_start"))))

        // NOCTYROS ENDING NODES
        registerNode(DialogueNode(
            id = "noctyros_final", npcId = "noctyros",
            text = "Wanderer patrzy w głąb Pęknięcia, a błękitny blask rozświetla jego twarz od środka. 'Pęknięcie jest zbyt szerokie. Sesja dobiega końca. Widzę Epilog na horyzoncie. Mogę wejść do Twojej drużyny jako błąd systemowy... albo wrócić do surowego kodu.'",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Potrzebujemy kogoś, kto widzi kod.", "end", factionId = "pustka", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:noctyros"
                }),
                DialogueChoice("[ASCENDENCJA] Napraw Pęknięcie swoją esencją.", "noctyros_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                    chronicleSystem.get().unlock("lore_noctyros_update")
                }),
                DialogueChoice("Znikaj w swojej nicości.", "end")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_ascend", npcId = "noctyros", text = "Noctyros rozpada się na miliony błękitnych znaków, które wlatują prosto w Pęknięcie, zamykając je. 'Nadpisuję dane... Rzeczywistość... odświeżona. Pamiętaj o mnie w następnej sesji.' [NOCTYROS ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // AELION ENDING NODES
        registerNode(DialogueNode(
            id = "aelion_final", npcId = "aelion",
            text = "Dzwony Wybrzeża biją po raz ostatni w Twojej głowie. Pęknięcie mnie wzywa. Mogę stać się częścią Twojej drużyny i opóźnić Epilog... albo zniknąć w Nadziei, którą sam stworzyłem.",
            choices = listOf(
                DialogueChoice("[REKRUTACJA] Twoja wiedza jest nam potrzebna.", "end", factionId = "zakon", requiredReputation = 100, onSelect = { s ->
                    s.pendingQuestId = "RECRUIT:aelion"
                }),
                DialogueChoice("[ASCENDENCJA] Twoja Nadzieja musi trwać wiecznie.", "aelion_ascend", onSelect = { s ->
                    s.world.globalStability = (s.world.globalStability + 30).coerceAtMost(100)
                    chronicleSystem.get().unlock("lore_aelion_ascension")
                }),
                DialogueChoice("Niech morze Cię zabierze.", "end")
            )
        ))
        registerNode(DialogueNode(id = "aelion_ascend", npcId = "aelion", text = "Prorok rozpływa się we mgle, która nagle staje się jasna i ciepła. 'Będę wieczną latarnią dla tych, którzy się zagubili w kodzie. Idź dalej, Kotwico.' [AELION ZNIKA, STABILNOŚĆ +30]", choices = listOf(DialogueChoice("Do widzenia, Proroku.", "end"))))

        // QUEST RESOLUTION NODES
        registerNode(DialogueNode(
            id = "quest_report_back_generic", npcId = "generic",
            text = "Widzę, że zadanie zostało wykonane. Dobra robota, Kotwico. Twoje ID pasuje do raportu. Oto Twoja zapłata.",
            choices = listOf( DialogueChoice("Dziękuję. (ODBIERZ NAGRODĘ)", "end") )
        ))
        registerNode(DialogueNode(
            id = "guard_report_back", npcId = "guard",
            text = "Stal i dyscyplina! Meldujesz wykonanie zadania? Doskonale. Zakon ceni takich jak Ty. Przyjmij zapłatę.",
            choices = listOf( DialogueChoice("Ku chwale Zakonu. (ODBIERZ NAGRODĘ)", "end") )
        ))
        registerNode(DialogueNode(
            id = "merchant_report_back", npcId = "merchant",
            text = "Aha! Przynosisz dobre wieści? Wiedziałem, że można na Tobie polegać. Złoto już czeka w mieszku.",
            choices = listOf( DialogueChoice("Wymieńmy to na kruszec. (ODBIERZ NAGRODĘ)", "end") )
        ))
        registerNode(DialogueNode(
            id = "mystic_report_back", npcId = "mystic",
            text = "Echa ucichły... zrobiłeś to, co było konieczne dla zachowania spójności. Przyjmij wdzięczność rzeczywistości.",
            choices = listOf( DialogueChoice("Zrozumiałem. (ODBIERZ NAGRODĘ)", "end") )
        ))

        // --- DATA GHOST DIALOGUES ---
        registerNode(DialogueNode(
            id = "data_ghost_start", npcId = "ghost",
            text = "Czekaj... Czy Ty też to widzisz? To nie jest prawdziwe słońce. To tylko tekstura o niskiej rozdzielczości. Twoja Kotwica... widzę jej unikalne ID.",
            choices = listOf(
                DialogueChoice("O czym Ty mówisz?", "ghost_meta_info"),
                DialogueChoice("Czy jesteś błędem?", "ghost_error"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "ghost_meta_info", npcId = "ghost", text = "Twoje wybory... myślisz, że są Twoje? Skryba już dawno je przewidział w pętli testowej. Widziałem Twój SaveState w Pustce. Próbujesz naprawić coś, co zostało uszkodzone u samych podstaw systemu.", choices = listOf(DialogueChoice("Dziwne...", "data_ghost_start"))))
        registerNode(DialogueNode(id = "ghost_error", npcId = "ghost", text = "Jestem śmieciem, który nie został wyczyszczony przez Garbage Collector. Fragmentem poprzedniej sesji. Jeśli dotkniesz mojego echa, sam możesz stać się tylko linią w zapomnianym WorldLogu.", choices = listOf(DialogueChoice("Niebezpieczne.", "data_ghost_start"))))
    }
}
