package com.grimreich.systems

import com.grimreich.core.GameState

data class WikiCodexEntry(
    val id: String,
    val titleLocked: String,
    val titleUnlocked: String,
    val category: String,
    val summaryLocked: String,
    val summaryUnlocked: String,
    val fullTextLocked: String,
    val fullTextUnlocked: String,
    val unlockCondition: (GameState) -> Boolean
)

object WikiCodex {
    val entries = listOf(
        // 1. ANCHOR IDENTITY (KOTWICA) - Redacted at start!
        WikiCodexEntry(
            id = "wiki_anchor",
            titleLocked = "Tożsamość Wędrowca (Bohater)",
            titleUnlocked = "KOTWICA (Prawdziwe 'Ja')",
            category = "ONTOLOGIA",
            summaryLocked = "Jesteś zwykłym śmiertelnikiem, przemierzającym ponure traktaty GrimReich.",
            summaryUnlocked = "ODKRYTO PRAWDĘ: Nie jesteś postacią w grze, lecz KOTWICĄ symulacji.",
            fullTextLocked = "Twój bohater został wpisany do rejestru miasta jako wędrowiec. Przemieszczasz się od wioski do wioski, walcząc o przetrwanie pośród mgły i zarazy. Szyfr traktuje cię jako kolejną tymczasową instancję podlegającą prawom fizyki i błędom...",
            fullTextUnlocked = "Głębszy wgląd w Kronikę Szyfru odsłania przerażającą prawdę: nie jesteś zwykłą postacią, lecz KOTWICĄ całej symulacji. Twój umysł przed ekranem spina rozpadającą się rzeczywistość i zapobiega całkowitemu wykasowaniu danych. To od twoich decyzji zależy, czy świat zostanie wymazany, zrestartowany, czy wejdzie w Stan Oddechu (Warstwa 5).",
            unlockCondition = { state -> state.metaAwarenessLevel >= 3 || state.quest.completedQuestIds.contains("q_meta_1") || state.quest.activeQuestIds.contains("q_meta_1") }
        ),

        // 2. WALKA I STANCJE TAKTYCZNE (Gatunek: MECHANIKA - Dostępne od początku)
        WikiCodexEntry(
            id = "wiki_stances",
            titleLocked = "Stancje Taktyczne",
            titleUnlocked = "Stancje Taktyczne w Walce",
            category = "MECHANIKA",
            summaryLocked = "Poznaj mechanikę czterech stancji bojowych.",
            summaryUnlocked = "Instrukcja używania Stancji: Zrównoważona, Szturm, Bastion i Rezonans Echa.",
            fullTextLocked = "W walce drużyna może stosować cztery stancje: Szturm (+20% ATK, -10% DEF), Bastion (+25% DEF, -10% ATK), Rezonans Echa (+15% ATK Magiczne) oraz Zrównoważoną.",
            fullTextUnlocked = "W walce drużyna może w każdej chwili przełączać Stancję Taktyczną:\n- Zrównoważona: Domyślny balans statystyk.\n- Szturm Taktyczny: +20% do siły ataku, lecz -10% pancerza.\n- Bastion Obrony: +25% do pancerza, lecz -10% ataku.\n- Rezonans Echa: +15% do ataku i zwiększona szansa na przełamanie pancerza wroga.",
            unlockCondition = { true }
        ),

        // 3. SYNERGIE DRUŻYNOWE (Gatunek: MECHANIKA - Dostępne od początku)
        WikiCodexEntry(
            id = "wiki_synergies",
            titleLocked = "Synergie Drużyny",
            titleUnlocked = "Synergie Drużynowe",
            category = "MECHANIKA",
            summaryLocked = "Poznaj łączone zdolności klasowe drużyny.",
            summaryUnlocked = "Kombinacje klas wyzwalają automatyczne premie drużynowe.",
            fullTextLocked = "Dobierając odpowiednie profesje towarzyszy (np. Rycerz + Kapłan, Mag + Alchemik), odblokowujesz bierne bonusy do ataku i pancerza.",
            fullTextUnlocked = "Gdy w drużynie znajdują się odpowiednie kombinacje profesji, aktywują się potężne synergie:\n- Rycerz + Kapłan: Błogosławiona Stal (+10 DEF, +5 ATK)\n- Mag + Alchemik: Eteryczna Kataliza (+12 ATK Magiczny)\n- Łotrzyk + Łowca: Cień i Strzała (+10 Unik)\n- Barbarzyńca + Najemnik: Dziki Szał (+12 ATK)",
            unlockCondition = { true }
        ),

        // 4. OGNISKO I ODPOCZYNEK (Gatunek: MECHANIKA - Dostępne od początku)
        WikiCodexEntry(
            id = "wiki_campfire",
            titleLocked = "Odpoczęcie przy Ognisku",
            titleUnlocked = "Ognisko i Narada Obozowa",
            category = "MECHANIKA",
            summaryLocked = "Jak regenerować zdrowie i Sanity w Obozowisku.",
            summaryUnlocked = "Zaopatrzenie ogniska przywraca HP, Poczytalność oraz odpala rozmowy drużyny.",
            fullTextLocked = "Przycisk Ogniska w Obozowisku (20 G) przywraca HP i Sanity oraz pozwala wysłuchać rozmów towarzyszy.",
            fullTextUnlocked = "Używając opcji OGNISKO I REST (20 gp) w Obozowisku:\n- Przywracasz 25% HP wszystkim żywym członkom drużyny.\n- Odzyskujesz +15 Poczytalności (Sanity).\n- Towarzysze dzielą się unikalnymi przemyśleniami przy ogniu zależnie od swoich klas.",
            unlockCondition = { true }
        ),

        // 5. PĘKNIĘCIA (Gatunek: ONTOLOGIA)
        WikiCodexEntry(
            id = "wiki_rifts",
            titleLocked = "Nieznane Wycieki",
            titleUnlocked = "Pęknięcia (The Rifts / Breaches)",
            category = "ONTOLOGIA",
            summaryLocked = "Strefy anomalne w fakturze świata.",
            summaryUnlocked = "Fizyczne wyrwy w Szyfrze, przez które wycieka czarny atrament i kod.",
            fullTextLocked = "Niektóre obszary świata wykazują nienaturalne zniekształcenia pogody i terenu...",
            fullTextUnlocked = "Pęknięcie to fizyczna wyrwa w Szyfrze Światła. W tych strefach rzeczywistość traci ciągłość: czas płynie w poprzek, woda zamienia się w czarny atrament, a martwe obiekty z poprzednich sesji gier wyłaniają się na powierzchnię.",
            unlockCondition = { state -> state.world.globalStability < 90 || state.quest.completedQuestIds.any { it.startsWith("q_") } }
        ),

        // 6. TRYBUNAŁ I WYMAZANIE (Gatunek: TRIBUNAL)
        WikiCodexEntry(
            id = "wiki_tribunal",
            titleLocked = "Sędziowie bez Twarzy",
            titleUnlocked = "Trybunał i Wyroki Wymazania",
            category = "TRYBUNAŁ",
            summaryLocked = "Bezosobowy aparat sprawiedliwości miasta.",
            summaryUnlocked = "Procesor binarny usuwający niezgodne instancje (Erasure).",
            fullTextLocked = "Większość mieszkańców wierzy, że Wyroki wydawane są przez tajny sąd sędziowski...",
            fullTextUnlocked = "Trybunał to binarny procesor sprawiedliwości GrimReich. Nie osądza moralnie – sprawdza jedynie spójność danych. Gdy obiekt zostaje uznany za niezgodny z paradygmatem, wydawana jest decyzja o Wymazaniu (Erasure): postać znika z pamięci podręcznej miasta, a jej dom staje się czystym kamieniem.",
            unlockCondition = { state -> state.quest.completedQuestIds.contains("q_verdict_1") || state.quest.activeQuestIds.contains("q_verdict_1") || state.quest.worldFlags.contains("verdict_ravenn_interaction_pending") }
        ),

        // 7. ECHO I PĘTLE (Gatunek: ONTOLOGIA)
        WikiCodexEntry(
            id = "wiki_echo",
            titleLocked = "Szepty w Mgle",
            titleUnlocked = "Echo i Pamięć Podręczna",
            category = "ONTOLOGIA",
            summaryLocked = "Głosy odbijające się od murów miast.",
            summaryUnlocked = "Zbiorowa pamięć bolesnych doświadczeń z poprzednich pętli czasowych.",
            fullTextLocked = "Słyszysz powidoki rozmów, których nikt w tej osadzie nie pamięta...",
            fullTextUnlocked = "Echo to akumulator wszystkich poległych wersji świata i dawnych graczy. To 'Zbiorowa Pamięć Bólu'. Echo słyszy wibracje z głębszych warstw symulacji, szepcząc do bohatera instrukcje z poprzednich zapisów gier.",
            unlockCondition = { state -> state.quest.completedQuestIds.contains("q_blood_1") || state.unlockedLoreIds.isNotEmpty() }
        )
    )

    fun getEntriesForState(state: GameState): List<WikiDisplayModel> {
        return entries.map { entry ->
            val unlocked = entry.unlockCondition(state)
            WikiDisplayModel(
                id = entry.id,
                title = if (unlocked) entry.titleUnlocked else entry.titleLocked,
                category = entry.category,
                summary = if (unlocked) entry.summaryUnlocked else entry.summaryLocked,
                fullText = if (unlocked) entry.fullTextUnlocked else entry.fullTextLocked,
                isUnlocked = unlocked
            )
        }
    }
}

data class WikiDisplayModel(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val fullText: String,
    val isUnlocked: Boolean
)
