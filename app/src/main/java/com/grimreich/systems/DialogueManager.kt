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
            text = "Stoj! Mgla gescieje, a prawo musi byc przestrzegane. Czego szukasz w cieniu murow?",
            choices = listOf(
                DialogueChoice("Szukam pracy (ZADANIA).", "guard_work"),
                DialogueChoice("Czy cos niepokojocego dzialo sie ostatnio? (MISJA)", "guard_quest_check"),
                DialogueChoice("[Charisma 12] Przekonaj go, ze jestes wyslannikiem Zakonu.", "guard_convince", requiredAttributes = mapOf("charisma" to 12)),
                DialogueChoice("[Strength 15] Zaimponuj mu swoja postura.", "guard_impress", requiredAttributes = mapOf("strength" to 15)),
                DialogueChoice("Tylko przechodze.", "end")
            )
        ))
        registerNode(DialogueNode(id = "guard_impress", npcId = "guard", text = "Widze, ze Kotwica nie oszczedzila Twoich miesni. Tacy jak Ty sa potrzebni w Straznicy. Mowia, ze w ruinach na poludniu widzieli cos... nieludzkiego.", choices = listOf(DialogueChoice("Dziekuje.", "end"))))
        registerNode(DialogueNode(id = "guard_convince", npcId = "guard", text = "Wyslannikiem? Wybacz, nie poznalem Twoich szat. Przejdz w pokoju, Kotwico. Mowia, ze w ruinach na poludniu widzieli cos... nieludzkiego.", choices = listOf(DialogueChoice("Dziekuje.", "end"))))
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
                DialogueChoice("[Intelligence 14] Rozpoznaj rzadki artefakt in jego torbie.", "merchant_artifact", requiredAttributes = mapOf("intelligence" to 14)),
                DialogueChoice("Masz jakies plotki?", "merchant_rumors"),
                DialogueChoice("Moze innym razem.", "end")
            )
        ))
        registerNode(DialogueNode(id = "merchant_artifact", npcId = "merchant", text = "Spostrzegawczy jestes. To Fragment Pustki. Nie na sprzedaz dla zwyklego smiertelnika, ale skoro go widzisz... moze kiedys pohandlujemy czyms wiecej.", choices = listOf(DialogueChoice("Bede pamietal.", "end"))))
        registerNode(DialogueNode(id = "merchant_rumors", npcId = "merchant", text = "Mowia, ze Prorok Aelion ukrywa cos pod kaplica. Ale kto by sluchal kupca?", choices = listOf(DialogueChoice("Interesujace.", "end"))))

        registerNode(DialogueNode(
            id = "merchant_quest_check", npcId = "merchant",
            text = "Relikwie? Zawsze. Ale niektore sa przeklete. Mowia, ze krwawa ikona w pobliskiej wiosce zaczela plakac. To zly znak.",
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
            text = "Dzien dobry... chociaz czy w GrimReich dni wciaz sa dobre? Kazdy rano sprawdza, czy jego odbicie w lustrze wciaz mroga w tym samym czasie.",
            choices = listOf(
                DialogueChoice("Co slychac w miescie?", "citizen_rumors"),
                DialogueChoice("[Perception 11] Zauwaz, ze jego cien porusza sie niezaleznie.", "citizen_shadow", requiredAttributes = mapOf("perception" to 11)),
                DialogueChoice("Zegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "citizen_shadow", npcId = "citizen", text = "Ciii! On slucha. Cien to jedyne, co nam zostanie, gdy swiatlo Absolutu zgasnie. Uwazaj na wlasne odbicie.", choices = listOf(DialogueChoice("Rozumiem.", "end"))))
        registerNode(DialogueNode(id = "citizen_rumors", npcId = "citizen", text = "Mowia, ze straznicy znajduja ciala z napisem 'WINNI'. Boje sie wychodzic po zmroku.", choices = listOf(DialogueChoice("Badz ostrozny.", "end"))))
        // 3. PILGRIM / ZEALOT
        registerNode(DialogueNode(
            id = "zealot_start", npcId = "zealot",
            text = "Prorocy patrza! Czy Twoja dusza jest czysta, wedrowcze? Pielgrzymujemy do Serca Krainy, by obmyc sie w jeziorach prawdy.",
            choices = listOf(
                DialogueChoice("Jestem wierny.", "end"),
                DialogueChoice("[Piety 13] Odmow wspolna modlitwe.", "zealot_prayer", requiredAttributes = mapOf("piety" to 13)),
                DialogueChoice("Ofiaruj krew (HP-${GameConstants.ZEALOT_SACRIFICE_HP_LOSS})", "zealot_sacrifice", onSelect = {
                    it.party.forEach { h -> h.hp -= GameConstants.ZEALOT_SACRIFICE_HP_LOSS }
                }),
                DialogueChoice("Dokad dokladnie zmierzacie?", "zealot_destination")
            )
        ))
        registerNode(DialogueNode(id = "zealot_prayer", npcId = "zealot", text = "Twoje slowa niosą moc, ktorej dawno nie slyszalem. Niech Absolut oswieca Twoja droge. Wez ten amulet.", choices = listOf(DialogueChoice("Dziekuje (OTRZYMANO RELIKWIE).", "end", onSelect = {
             it.logEntries.add("Otrzymano Amulet Pielgrzyma.")
        }))))
        registerNode(DialogueNode(id = "zealot_destination", npcId = "zealot", text = "Do Opactwa Ciszy. Tam, gdzie slowa traca znaczenie, a Absolut staje sie slyszalny.", choices = listOf(DialogueChoice("Powodzenia.", "end"))))
        registerNode(DialogueNode(id = "zealot_sacrifice", npcId = "zealot", text = "Twoja ofiara zostala przyjeta. Czuc mrowienie w kosciach.", choices = listOf(DialogueChoice("Idz w pokoju.", "end"))))
        //  4. MYSTIC
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "mystic",
            text = "Cien w Tobie rosnie. Absolut Cie wola, Kotwico. Widze wize bez drzwi w Twoich snach... czy ona juz tu jest?",
            choices = listOf(
                DialogueChoice("Powiedz mi o wiezy (ZADANIE).", "mystic_tower_info"),
                DialogueChoice("[Intelligence 15] Zapytaj o Nature Pekniecia.", "mystic_fracture", requiredAttributes = mapOf("intelligence" to 15)),
                DialogueChoice("Kim jestes?", "mystic_who"),
                DialogueChoice("Nie interesuja mnie sny.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mystic_fracture", npcId = "mystic", text = "Pekniecie to nie dziura, to szew. Swiat sie rozpada, bo ktos probuje go uszyc na nowo wedlug innego wzoru. My jestesmy tylko nicmi.", choices = listOf(DialogueChoice("To mroczna wizja.", "end"))))
        registerNode(DialogueNode(
            id = "mystic_tower_info", npcId = "mystic",
            text = "Ona pojawia sie only tam, gdzie smierc jest swieza. Szukaj jej na obrzezach miasta, posrod mgly. By wejsc, musisz przestac istniec na chwile.",
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
                DialogueChoice("To brzmi like szalenstwo.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mystic_who", npcId = "mystic", text = "Jestem echem kogos, kto za dlugo patrzyl w Pekniecie. Widze wezly czasu, ktore probujesz rozplatac.", choices = listOf(DialogueChoice("To niepokojace.", "end"))))

        // 7. ALCHEMIST
        registerNode(DialogueNode(
            id = "alchemist_start", npcId = "alchemist",
            text = "Uważaj na to, co pijesz. Woda w tych stronach ma pamięć... i czasami nie chce zapomnieć o tych, którzy w niej utonęli.",
            choices = listOf(
                DialogueChoice("Masz jakieś mikstury?", "end"),
                DialogueChoice("[Intelligence 12] Czy to prawda, że rtęć może stabilizować Kotwicę?", "alchemist_mercury", requiredAttributes = mapOf("intelligence" to 12)),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "alchemist_mercury", npcId = "alchemist", text = "Rtęć? Tylko ta destylowana w świetle pełnej Anomalii. Niebezpieczna wiedza. Weź to, przyda ci się przy kolejnym skoku stabilności.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // 8. BEGGAR
        registerNode(DialogueNode(
            id = "beggar_start", npcId = "beggar",
            text = "Daj miedziaka dla kogoś, kto widział Drugą Stronę i wrócił bez oczu...",
            choices = listOf(
                DialogueChoice("Daj 5 złota.", "beggar_give", onSelect = { it.gold -= 5 }),
                DialogueChoice("[Perception 13] Zauważ, że pod łachmanami chowa złoty sztylet.", "beggar_dagger", requiredAttributes = mapOf("perception" to 13)),
                DialogueChoice("Nie mam nic dla ciebie.", "end")
            )
        ))
        registerNode(DialogueNode(id = "beggar_give", npcId = "beggar", text = "Niech Absolut ci wynagrodzi. Uważaj na karczmę, ściany tam mają uszy, które krwawią.", choices = listOf(DialogueChoice("Dziwna rada...", "end"))))
        registerNode(DialogueNode(id = "beggar_dagger", npcId = "beggar", text = "Spostrzegawczyś... To pamiątka z lepszych czasów. Albor klątwa. Idź swoją drogą, zanim on też ciebie zauważy.", choices = listOf(DialogueChoice("Odchodzę.", "end"))))

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
        ))

        // INFESTED NPC
        registerNode(DialogueNode(
            id = "infested_start", npcId = "infested",
            text = "Twoja... Kotwica... lśni... Pozwól mi... Dotknąć... Prawdy...",
            choices = listOf(
                DialogueChoice("Odejdź ode mnie!", "end"),
                DialogueChoice("[Intelligence 13] Spróbuj nawiązać kontakt z umysłem.", "infested_contact", requiredAttributes = mapOf("intelligence" to 13))
            )
        ))
        registerNode(DialogueNode(id = "infested_contact", npcId = "infested", text = "Widzę... to, co ty... Świat to tylko... Skóra... pod którą... Pulsuje... Nic...", choices = listOf(DialogueChoice("To przerażające.", "end"))))

        // REGIONAL HERO: AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Witaj, Kotwico. Mgła rzednie w Twojej obecności, ale mrok pod nią staje się gęstszy. Czego szukasz u Proroka?",
            choices = listOf(
                DialogueChoice("Jak mogę ustabilizować ten świat?", "aelion_stability"),
                DialogueChoice("Kim są 'Wybrańcy'?", "aelion_chosen"),
                DialogueChoice("Żegnaj.", "end")
            )
        ))
        registerNode(DialogueNode(id = "aelion_stability", npcId = "aelion", text = "Stabilność to iluzja. Szukaj Serca Krainy. Tam Mirror (Mira) pokaże Ci, co jest odbiciem, a co źródłem.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))
        registerNode(DialogueNode(id = "aelion_chosen", npcId = "aelion", text = "Ci, którzy przetrwali Pęknięcie bez utraty siebie. Jest nas niewielu. Xyrel na wschodzie pilnuje murów, ale on widzi tylko Wyrok.", choices = listOf(DialogueChoice("Interesujące.", "end"))))

        // REGIONAL HERO: XYREL
        registerNode(DialogueNode(
            id = "xyrel_start", npcId = "xyrel",
            text = "Kotwica. Kolejna, która myśli, że może powstrzymać to, co nieuniknione. Równiny spływają krwią, a my tylko liczymy winnych.",
            choices = listOf(
                DialogueChoice("Szukam Aeliona.", "xyrel_aelion"),
                DialogueChoice("Dlaczego Inkwizycja jest taka surowa?", "xyrel_strict"),
                DialogueChoice("Nie przeszkadzam.", "end")
            )
        ))
        registerNode(DialogueNode(id = "xyrel_aelion", npcId = "xyrel", text = "Starzec siedzi w swojej mgle. Myśli, że modlitwa naprawi Pęknięcie. Tutaj potrzebujemy stali, nie kadzidła.", choices = listOf(DialogueChoice("Rozumiem.", "end"))))
        registerNode(DialogueNode(id = "xyrel_strict", npcId = "xyrel", text = "Bo słabość to zaproszenie dla Drugiej Strony. Każda wątpliwość to kolejna szczelina w rzeczywistości.", choices = listOf(DialogueChoice("Mocne słowa.", "end"))))
    }
}
