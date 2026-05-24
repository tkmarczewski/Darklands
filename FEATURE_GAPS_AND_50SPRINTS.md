# Darklands Mobile — Analiza Braków Funkcjonalności i Plan 50 Sprintów

**Data analizy:** 2026-05-24  
**Autor:** GitHub Copilot Analysis  
**Zakres:** Pełne porównanie z oryginałem Darklands (1992) + szczegółowy roadmap 50 sprintów z UT i test build.

---

## 1. EXECUTIVE SUMMARY

Projekt ma **solidny core** (walka, alchemia, systemy postaci, religia) w wersji Kotlin/Android, ale w porównaniu z oryginałem brakuje przede wszystkim:

- **Świata**: mapa ograniczona do 1 miasta (Magdeburg) + 8 dzielnic. Oryginał miał 8+ miast na głównej mapie.
- **Zawartości**: 5 świętych (powinno 60+), 8 karier (powinno 37), minimalny bestiary (16 typów wrogów, powinno 40+).
- **Powiązań międzysystemowych**: brak plotek, nazwanych NPC, sensownych eventów powiązanych z reputacją.
- **Głębi zagrywki**: prawie brak quest chainów, brak elementów sandbox (losowe generowanie, różnorodne cele).
- **UX/Game Feel**: minimalne UI, brak animacji, brak wersji demo dla graczy.

**Szacunkowe pokrycie core:** ~35–40%  
**Szacunkowe pokrycie contentu:** ~15–20%

---

## 2. SZCZEGÓŁOWA ANALIZA BRAKÓW

### 2.1 Świat i Eksploracja

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Mapa świata | 1 miasto + 8 dzielnic | 10+ miast, tereny przejściowe (las, góry, rzeki) | Mapa hex 8x8 z wieloma miastami i lokacjami przejściowymi |
| Miasta | Tylko Magdeburg | Hamburg, Köln, Frankfurt, Prag, Nürnberg, Wien, Breslau, Augsburg, Lübeck + inne | Każde miasto ma historię, gospodarki, plotki lokalne, reputacje |
| Tereny przejściowe | Brak | Lasy (wroga: zbójcy, wilcy), Góry (wroga: poszukiwacze, bandyci), Rzeki (crossing) | Dynamiczne spotkania zależne od terenu |
| Czasy przejazdu | Uproszczone | Brak modelu czasu podróży, szans na spotkanie | W Darklands to jest ważne |
| Losowe lokacje | Brak | Ruiny, zamki raubritterów, klasztory, hamletki, lochy | Stałe + proceduralne generowanie |

### 2.2 Postacie i Kariery

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Kariery | 8 (Farmer, Thief, Soldier, Cleric, Scholar, Merchant, Knight, Raubritter) | 29 brakujących (Military 5+, Civil 5+, Religious 4, Academics 4, Trades 5, Underworld 5) | 37 karier w 6 grupach |
| Tło społeczne | Plan | Brak (SocialBackground) | 6 typów (Nobility, Wealthy Urban, Town Trades, Country Crafts, Urban Commoners, Rural Commoners) |
| Starzenie się | Tak | Brak grafu wzrostu atrybutów, brak emerytury | Progresja naturalna + emerytura |
| Life Path | Plan | Brak konkretnej implementacji | W Darklands każda postać ma realną historię |

### 2.3 System Modlitwy i Świętych

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Liczba świętych | 5 | 55–65 brakujących | Katalog ~60 świętych |
| Efekty specjalne | Proste bufty | Brak mocy (teleport, woda błogosławiona, osłabienie demonów) | Każdy święty ma 1–3 moce |
| Lokalne bonusy | Brak | Święci związani z miastami (np. Św. Kolumb w Köln) | Zmienna dostępność świętych per miasto |
| Modlitwy | Tak | Brak skill-checków, brak konsekwencji | Modlitwy mogą się nie udać |
| Wotywne | Plan | Brak systemu ofiar | Rytuały ofiarnicze dla większych łask |

### 2.4 Alchemia

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Mikstury | 22 | Brakuje zaawansowanych (kombinacje specjalne, elixiry) | 30+ mikstury |
| Rodzaje efektów | Proste (buffy/debuffy) | Brak interakcji między mikstrami, brak czasu trwania | Dynamiczne efekty w trakcie walki |
| Kwalifikacja alchemika | Plan | System poziomu alchemika nieobecny | Poziom alchemy wpływa na szansę sukcesu i jakość |
| Składniki | 15 | Brakuje zaawansowanych składników | 20+ składników |

### 2.5 Walka i Bestiary

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Typy wrogów | 16 | 25+ brakujących (Demon Boss, Ghost, Troll, Dragon, Werewolf, Vampire, Cultist...) | 40+ typów |
| AI | Podstawowe | Brak AI dla łuczników (LOS), alchemików, bossów | Zaawansowana AI |
| Fazy bossów | Plan | Brak implementacji | Bossowie mają fazy walki |
| Special moves | Proste | Brak AOE, strach, debuffy specjalne | Rich moveset |
| Taktyka | Brak | Brak mechaniki pozycji, linii ognia (LOS) | Taktyczna walka |

### 2.6 Questy i Fabularni

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Quest chainy | 2 (Raubritter, Endgame/Kult) | 5+ brakujących (Witch Hunt, Dwarves, Plague, Słabi baronowie, Spiskownicy) | 8+ głównych chain'ów |
| Plotki | Plan | Brak RumorSystem | Plotki to główny driver questów |
| NPC | Plan (NamedNpc) | Brakuje nazwanych postaci | Każde miasto ma ~5 postaci |
| Eventy miasta | Proste | Brak powiązania z reputacją, brak dynamiki | Eventy to część storytellingu |
| Endings | 4 (Good, Pragmatic, Redemption, Corruption) | Brak powiązania z działaniami | Ending zależy od choices |

### 2.7 Ekonomia i Handel

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| System cen | Proste | Brak wpływu reputacji na ceny, brak cen lokalnych | Ekonomia dynamiczna |
| Usługi miasta | Plan | Brak leczenia, szkolenia karier | Usługi to część miasta |
| Zasoby | Brak | Brak jedzenia, wypoczynku, logistyki | Zaopatrzenie to real concern |
| Turnieje | Plan | Brak turnieji rycerskich | Turnieje = reputacja + złoto |
| Relikwie | Plan | Brak artefaktów | Artefakty to treasure |

### 2.8 Czas i Sezonowość

| Komponent | Status | Brak | Oryginał |
|---|---|---|---|
| Pory doby | Tak | Brak wpływu na shops, brak nocnych eventów | Dzień/noc wpływa na gameloop |
| Sezony | Tak | Brak wpływu na przejazdów, brak eventów sezonowych | Sezony to klimat gry |
| Czas rzeczywisty | Tak | Brak grafu długoterminowego, brak zmęczenia drużyny | Czas to resource |

---

## 3. OBSZARY WYMAGAJĄCE NATYCHMIASTOWEGO DZIAŁANIA (TOP 10)

| Priorytet | Obszar | Czemu | Effort |
|---|---|---|---|
| 1 | **Mapa świata + miasta** | Bez tego gra wygląda jak prototype. Kluczowy dla identyfikacji | Wysoki |
| 2 | **Święci (rozszerzenie)** | Najcharakterystyczniejsza mechanika Darklands | Średni |
| 3 | **Plotki + NPC** | Łączą systemy w spójny świat | Średni |
| 4 | **Kariery (pełny katalog)** | Personalizacja postaci | Niski/Średni |
| 5 | **Bestiary (rozszerzenie)** | Różnorodność walk | Niski |
| 6 | **Quest chainy (nowe)** | Zawartość fabularna | Wysoki |
| 7 | **Ekonomia (ceny lokalne)** | Spina miasta z systemami | Średni |
| 8 | **AI zaawansowana** | Walki się robią nudne bez tego | Wysoki |
| 9 | **UX/UI poprawy** | Gra jest praktycznie nieuprawialna | Średni |
| 10 | **Save/Load menu** | Gracze muszą mogli grać normalnie | Niski |

---

## 4. STRUKTURA 50 SPRINTÓW (z UT i test build)

### ETAP 0: Setup (Sprinty 1–3)

#### Sprint 1: Struktura pakietów i conventions
- [ ] Przejrzeć katalog `app/src/main/java/com/darklandsmobile/`
- [ ] Upewnić się, że pakiety `core`, `world`, `content`, `systems`, `ui` są czyste
- [ ] Ustalić konwencję TODO tagów (`TODO[city]`, `TODO[saint]`, itp.)
- [ ] **UT:** JUnit test na strukturę pakietów
- [ ] **Test Build:** `./gradlew clean assembleDebug` — brak błędów

#### Sprint 2: Format danych dla katalogów
- [ ] Wybrać format (Kotlin data classes + enumy)
- [ ] Zdefiniować `CityData`: (id, name, region, population, modifiers, events)
- [ ] Zdefiniować `SaintData`: (id, name, domain, patronage, bonuses, powers)
- [ ] Zdefiniować `CareerData`: (id, name, group, requirements, effects)
- [ ] Zdefiniować `BestiaryEntry`: (id, type, stats, equipment, behavior)
- [ ] **UT:** Data class serialization tests
- [ ] **Test Build:** Brak compilation errors

#### Sprint 3: Dokumentacja i Contributing
- [ ] Zaktualizować `README.md` — opis struktury
- [ ] Stwórz `CONTRIBUTING.md` — konwencje kodowania, TODO, sprint checklist
- [ ] Aktualizuj `Roadmap.md` jeśli potrzeba
- [ ] **UT:** Nie dotyczy
- [ ] **Test Build:** Upewnij się, że docs renderują się poprawnie

---

### ETAP I: Świat i Miasta (Sprinty 4–10)

#### Sprint 4: Miasta — Paczka #1 (5 miast)
- [ ] Implementuj `Köln` w `CityCatalogue`
  - [ ] Dane: pozycja na mapie, populacja, typ (metropolia)
  - [ ] Dziedziny: Market, Church, Guildhall
  - [ ] 2 events
- [ ] Implementuj `Nürnberg`
- [ ] Implementuj `Frankfurt`
- [ ] Implementuj `Prag`
- [ ] Implementuj `Lübeck`
- [ ] **UT:** `CityDataTest` — poprawna inicjalizacja miast, testuj reputacje
- [ ] **Test Build:** Weryfikuj travel między miastami, brak crash'y

#### Sprint 5: Miasta — Paczka #2 (5 miast) + połączenia
- [ ] Implementuj `Hamburg`, `Wien`, `Breslau`, `Augsburg`, `Strasbourg`
- [ ] Zaktualizuj `WorldMap` — połączenia między wszystkimi 10 miastami
- [ ] Dodaj tereny przejściowe (las, góry, rzeka) — ikony na mapie
- [ ] Zaimplementuj szanse na spotkanie w terenie
- [ ] **UT:** `WorldMapTest` — connectivity check, encounter generation
- [ ] **Test Build:** Gra może obsługiwać pełną mapę bez lagów

#### Sprint 6: Eventy miasta — Paczka #1 (10–15 eventów)
- [ ] Dla każdego z 10 miast: min. 1 event ogólny
- [ ] Dla każdego miasta: min. 1 event zależny od reputacji (rycerze, kupcy, kościół, lud)
- [ ] Przykład event: „Konflikt miedzy cechami" (Nürnberg, Gildhall)
- [ ] Przykład event: „Pielgrzymi szukają opory" (Prag, Church)
- [ ] **UT:** `CityEventSystemTest` — event triggering, outcome validation
- [ ] **Test Build:** Eventy fire w odpowiednich miastach

#### Sprint 7: Tereny przejściowe — Encounters
- [ ] Zaimplementuj 5 typów terrenu: Droga, Las, Góry, Rzeka, Болото
- [ ] Dla każdego: lista możliwych wrogów (np. Las: zbójcy, wilcy; Góry: trollowie)
- [ ] Szanse encounter: Las (30%), Góry (40%), Rzeka (20%)
- [ ] Liczba tur podróży zależna od terenu
- [ ] **UT:** `TerrainEncounterTest` — encounter distribution
- [ ] **Test Build:** Podróż trwa odpowiednio długo, spotkania są sensowne

#### Sprint 8: Czasy podróży i zmęczenie
- [ ] Implementuj model czasu: każdy segment = 2–6 godzin
- [ ] Zmęczenie drużyny: rośnie z czasem, wpływa na combat
- [ ] Odpoczynek: nocleg w mieście zmniejsza zmęczenie
- [ ] **UT:** `TravelFatigueTest` — fatigue accumulation, rest mechanics
- [ ] **Test Build:** UI pokazuje zmęczenie, walki są słabsze ze zmęczeniem

#### Sprint 9: Losowe lokacje (proceduralne)
- [ ] Zdefiniuj template'y: ruiny (loot), zamek raubrittera (boss), klasztor (złoto), loch (potwory)
- [ ] Generator rozmieszczający ~5 takich lokacji na mapie (seed-based)
- [ ] Każda lokacja ma: typ, wrogi, skarb, NPC
- [ ] **UT:** `ProceduralLocationTest` — deterministic generation (seed test)
- [ ] **Test Build:** Każdy load gry ma różne losowe lokacje

#### Sprint 10: Lokalne reputacje — Miasta
- [ ] Rozszerz `ReputationSystem` — per-city reputation
- [ ] Każde miasto ma 4 frakcje (Rycerze, Kupcy, Kościół, Lud)
- [ ] Reputacja wpływa na: dostęp do eventów, ceny, dostępne questy
- [ ] **UT:** `LocalReputationTest` — city-specific faction tracking
- [ ] **Test Build:** Zmiana reputacji w jednym mieście nie wpływa na inne

---

### ETAP II: Święci i Religia (Sprinty 11–16)

#### Sprint 11: Święci — Paczka #2 (do ~20 świętych)
- [ ] Dodaj ~15 nowych świętych do `SaintCatalogue`
  - [ ] Św. Jerzy (walka z potworami, odwaga)
  - [ ] Św. Mikołaj (handel, podróż)
  - [ ] Św. Barbara (ochrona, bomby)
  - [ ] Św. Rafał (ochrona podróży)
  - [ ] Św. Piotr (siła wiary)
  - [ ] Sw. Paweł (nauka)
  - [ ] Św. Krzysztof (podróż długa)
  - [ ] Św. Franciszek (natura, zwierzęta)
  - [ ] Św. Cecylia (sztuka, morale)
  - [ ] Św. Adrian (żołnierze)
  - [ ] Św. Agnieszka (czystość)
  - [ ] Św. Katarzyna (mądrość)
  - [ ] Św. Dorota (szukanie skarbu)
  - [ ] Św. Grzegorz (naukowcy)
  - [ ] Św. Helena (rzeczy zagubione)
- [ ] Dla każdego: domain, patronage, bonusy
- [ ] **UT:** `SaintCatalogueTest` — poprawna inicjalizacja, unikalność ID
- [ ] **Test Build:** Saints render poprawnie w UI

#### Sprint 12: Święci — Paczka #3 (do ~35 świętych)
- [ ] Dodaj +15 świętych
  - [ ] Św. Wacław (Czesi, walka)
  - [ ] Św. Stanisław (Polacy, prawo)
  - [ ] Św. Benon (Hamburg, handel)
  - [ ] Św. Leopold (Austria, rosnący wpływ)
  - [ ] Św. Urszula (dziewice, pielgrzymi)
  - [ ] Św. Zofia (mądrość)
  - [ ] Św. Jozafat (opatrznośc)
  - [ ] Św. Klemens (podwodny)
  - [ ] Św. Łukasz (artyści)
  - [ ] Św. Marek (uciekinierzy)
  - [ ] Św. Serafin (ogień)
  - [ ] Św. Teofil (umowy)
  - [ ] Św. Wiktoria (zwycięstwo)
  - [ ] Św. Zyta (służba)
  - [ ] Św. Augustyn (uczeni)
- [ ] **UT:** Test modlitw do każdego świętego
- [ ] **Test Build:** Brak crash'y, żaden święty ma id kolizję

#### Sprint 13: Specjalne moce świętych — Paczka #1 (5 mocy)
- [ ] Implementuj `SaintPower` interface
- [ ] Moc 1: **Teleport do bramy miasta** (Św. Krzysztof)
  - [ ] Koszt: 25 faith
  - [ ] Cooldown: 10 dni
  - [ ] Efekt: natychmiastowy przeskok do losowego miasta
- [ ] Moc 2: **Woda błogosławiona** (Św. Rafał)
  - [ ] Koszt: 15 faith
  - [ ] Efekt: przejście przez rzekę
  - [ ] Trwa: 1 dzień
- [ ] Moc 3: **Osłabienie demonów** (Św. Michał)
  - [ ] Koszt: 20 faith
  - [ ] Efekt: demony w następnej walce -25% dmg
  - [ ] Cooldown: 7 dni
- [ ] Moc 4: **Błogosławieństwo урожаю** (Św. Franciszek)
  - [ ] Koszt: 10 faith
  - [ ] Efekt: tańsze jedzenie w mieście przez 3 dni
- [ ] Moc 5: **Nocna wędrówka** (Św. Cecylia)
  - [ ] Koszt: 12 faith
  - [ ] Efekt: podróż nocą bez spotkań
- [ ] **UT:** `SaintPowerTest` — koszt, cooldown, efekty
- [ ] **Test Build:** Każda moc działa bez glitchy

#### Sprint 14: Specjalne moce świętych — Paczka #2 (5 mocy) + regionalne bonusy
- [ ] Moc 6–10 (kolejne święte)
- [ ] Zaimplementuj **lokalne bonusy** — św. lokalnie silniejszy
  - [ ] Św. Wacław silniejszy w Pradze
  - [ ] Św. Benon silniejszy w Hamburgu
  - [ ] itp.
- [ ] **UT:** `LocalSaintBonusTest` — zweryfikuj modyfikatory
- [ ] **Test Build:** Moce działają, UI pokazuje aktywne bufty

#### Sprint 15: Wotywne i zaawansowana religia
- [ ] Implementuj system **ofiar** (votive offerings)
  - [ ] Ofiara 50 złota = +5 faith
  - [ ] Ofiara przedmiotu = +15 faith
  - [ ] Ofiara broni = +25 faith + malus do walki
- [ ] Dodaj **nagrody za virtue**
  - [ ] 100 virtue = automatyczne błogosławieństwo
  - [ ] Nowe efekty specjalne
- [ ] **UT:** `VotiveOfferingTest`, `VirtueRewardsTest`
- [ ] **Test Build:** Ofiary są lidrami, nagrody fire'ują

#### Sprint 16: Religia — UI i interakcje
- [ ] Zaktualizuj `PrayerActivity` — pełny interfejs modlitw
- [ ] Pokaż dostępne moce świętych
- [ ] Pokaż cooldown mocy
- [ ] Dodaj opisów świętych (patronate, domena)
- [ ] **UT:** Nie dotyczy
- [ ] **Test Build:** UI jest responsywne, wszystkie opcje dostępne

---

### ETAP III: Kariery i Tło Społeczne (Sprinty 17–21)

#### Sprint 17: Social Background — enum i integracja
- [ ] Utwórz enum `SocialBackground`:
  - [ ] NOBILITY
  - [ ] WEALTHY_URBAN
  - [ ] TOWN_TRADES
  - [ ] COUNTRY_CRAFTS
  - [ ] URBAN_COMMONERS
  - [ ] RURAL_COMMONERS
- [ ] Dodaj pole do `Hero`:
  - [ ] background: SocialBackground
  - [ ] backgroundBonuses: Map<String, Int>
- [ ] Zaimplementuj wybór background'u w character creation
- [ ] **UT:** `SocialBackgroundTest` — background bonuses
- [ ] **Test Build:** Hero nowy ma background, bonusy się stosują

#### Sprint 18: Kariery — pełny katalog (paczka #1, Military 5+)
- [ ] Dodaj brakujące kariera militarne:
  - [ ] Recruit (Rekrut)
  - [ ] Soldier (Żołnierz)
  - [ ] Veteran (Weteran)
  - [ ] Sergeant (Sierżant)
  - [ ] Knight Officer (Oficer Rycerski)
- [ ] Dla każdego: wymagania (wiek, strength, background), efekty (atrybuty, skills)
- [ ] **UT:** `CareerRequirementsTest` — weryfikuj wymagania
- [ ] **Test Build:** Postaci mogą przechodzić łańcuchy karier

#### Sprint 19: Kariery — Civil, Religious, Academics (paczka #2)
- [ ] Civil: Scribe, Clerk, Notary, Merchant, Guild Master (5)
- [ ] Religious: Novice, Monk, Priest, Bishop, Inquisitor (5)
- [ ] Academics: Student, Scholar, Physician, Astronomer, Alchemist (5)
- [ ] **UT:** Test chain'ów (Novice → Monk → Priest)
- [ ] **Test Build:** Kariera się postępuje naturalnie

#### Sprint 20: Kariery — Trades, Underworld (paczka #3)
- [ ] Trades: Apprentice, Journeyman, Craftmaster, Guild Leader (4)
- [ ] Underworld: Thief, Smuggler, Gang Member, Crime Boss, Assassin (5)
- [ ] **UT:** `CareerChainTest` — wertyfikuj wszystkie chain'y
- [ ] **Test Build:** Wszystkie 37+ karier dostępne, bez collision ID

#### Sprint 21: Kariery — UI i procesy przejścia
- [ ] Zaimplementuj UI ekran karier (co się otwiera po levelup / w mieście)
- [ ] Pokaż możliwe następne kariery
- [ ] Pokaż bonusy / zmiany z przejścia
- [ ] **UT:** Nie dotyczy
- [ ] **Test Build:** Przejścia karier działają, bonus się stosują

---

### ETAP IV: Plotki i NPC (Sprinty 22–25)

#### Sprint 22: RumorSystem — Core
- [ ] Utwórz `Rumor` data class:
  - [ ] id: String
  - [ ] text: String
  - [ ] veracity: Float (0.0–1.0)
  - [ ] linkedQuestId: String?
  - [ ] region: String
  - [ ] sourceType: RumorSource (TAVERN, STREET, CHURCH)
  - [ ] discoveredAt: Int (dzień odkrycia)
- [ ] Utwórz `RumorSystem` object
  - [ ] getRumorsForCity(cityId): List<Rumor>
  - [ ] getRumorsForRegion(region): List<Rumor>
  - [ ] addRumor(rumor): Unit
  - [ ] clearRumors(): Unit (dla testów)
- [ ] Dodaj ~20 plotek do katalogu
- [ ] **UT:** `RumorSystemTest` — add/retrieval, filtering
- [ ] **Test Build:** Plotki generują się, brak NULL pointers

#### Sprint 23: NPC — Named Characters
- [ ] Utwórz `NamedNpc` data class:
  - [ ] id: String
  - [ ] name: String
  - [ ] role: NpcRole (TAVERN_KEEPER, PRIEST, GUARD_COMMANDER, MERCHANT, WITCH, itd.)
  - [ ] cityId: String
  - [ ] linkedRumorId: String?
  - [ ] linkedQuestId: String?
- [ ] Dodaj ~5 NPC per główne miasto (50+ total)
- [ ] Powiąż NPC z plotkami (NPC: "Słyszałem plotkę...")
- [ ] **UT:** `NpcCatalogueTest` — unikalność, powiązania
- [ ] **Test Build:** NPC render poprawnie w city events

#### Sprint 24: Eventy miasta — integracja z NPC i plotkami
- [ ] Przejrzyj istniejące eventy w 10 miastach
- [ ] Dla każdego: zamień anonimowe opisy na nazwy NPC
- [ ] Dla każdego: dodaj powiązanie z plotką (jeśli istnieje)
- [ ] Dodaj +10 eventów (1–2 per miasto, specjalne)
- [ ] **UT:** `CityEventIntegrationTest` — NPC found, rumors linked
- [ ] **Test Build:** Eventy mają nazwane postacie, plotki się pokazują

#### Sprint 25: Plotki i RumorSystem — UI
- [ ] Zaimplementuj ekran plotek (dostępny w karczmy, kościele)
- [ ] Pokaż listę plotek z kategoriami (sluch, fakt, wiadomość)
- [ ] Plotka: klik → pełna linia + wiarygodność
- [ ] Link do powiązanego questu (jeśli istnieje)
- [ ] **UT:** Nie dotyczy
- [ ] **Test Build:** UI jest responsywne, plotki są klikalne

---

### ETAP V: Quest Chainy (Sprinty 26–31)

#### Sprint 26: Quest Chain #2 — Witch Hunt (struktura)
- [ ] Zaprojektuj łańcuch:
  1. Event startowy: plotka o sabatzie (церковь)
  2. Event śledczy #1: przesłuchanie podejrzanej (miasto)
  3. Event śledczy #2: poszukiwanie dowodów w lesie
  4. Event finałowy: sabat w lesie (walka + wybór moralny)
- [ ] Zaimplementuj `WitchHuntQuestChain` w `QuestGraph`
- [ ] Każdy event: skill-checki (streetwise, read, alchemy)
- [ ] **UT:** `QuestChainStructureTest` — dependencies, progression
- [ ] **Test Build:** Quest się triggera, eventy się odwala

#### Sprint 27: Quest Chain #3 — Dwarves in Mines
- [ ] Event startowy: plotka o karłach (góry, Nürnberg)
- [ ] Event #1: spotanie z karłem (negocjacje)
- [ ] Event #2: podejście do kopalni
- [ ] Event finałowy: walka z trollem / ulfberthtami
- [ ] Nagrody: złoto, artefakt
- [ ] **UT:** Quest state machine test
- [ ] **Test Build:** Quest chain działa, nagrody się dają

#### Sprint 28: Quest Chain #4 — Plague Quest
- [ ] Event startowy: plotka o epidemii (miasto)
- [ ] Event #1: odwiedzenie szpitala, rozmowy
- [ ] Event #2: poszukiwanie lekarstwa (alchemy check)
- [ ] Event #3: zbieranie składników z terenu
- [ ] Event finałowy: przywiezienie lekarstwa / alternate ending (reputacja)
- [ ] **UT:** Quest outcomes test
- [ ] **Test Build:** Multiple endings working

#### Sprint 29: Quest Chain #5 — Raubritter (pełne rozszerzenie)
- [ ] Istniejący chain: uproszczenie
- [ ] Nowe eventy: spotkania z lokalnymi rycerzami (negocjacje / walka)
- [ ] Nowa ending: sojusz z raubritterami (zmiana reputation)
- [ ] **UT:** Expanded chain test
- [ ] **Test Build:** All endpoints reachable

#### Sprint 30: Quest Chain #6 — Endgame/Kult Baphometa (pełne)
- [ ] Istniejący chain: pełne rozszerzenie
- [ ] Eventy przejścia: plotki → śledztwo → konfrontacja
- [ ] Ending choices (dobry, pragmatyczny, odkupienie, skażenie) wpływ na świat
- [ ] **UT:** Ending determination test
- [ ] **Test Build:** All 4 endings reachable

#### Sprint 31: Quest Chain #7–8 — dodatkowe (Turniej rycerski, Szukanie relikwii)
- [ ] Turniej: start (miasto) → eliminacje → finał (nagrody: reputacja, złoto)
- [ ] Relikwie: szukanie artefaktów w losowych lokacjach
- [ ] **UT:** Quest availability tests
- [ ] **Test Build:** New chains work

---

### ETAP VI: Bestiary i AI (Sprinty 32–38)

#### Sprint 32: Bestiary — paczka nowych wrogów (ludzie)
- [ ] Dodaj brakujących typów ludzi:
  - [ ] Bandit (bandyta)
  - [ ] Mercenary (najemnik)
  - [ ] Cultist (kult)
  - [ ] Witch (czarownica)
  - [ ] Brigand Leader (przywódca zbójów)
- [ ] Dla każdego: statystyki, ekwipunek, AI flag
- [ ] **UT:** `BestiaryTest` — poprawne statystyki
- [ ] **Test Build:** Nowe wrogi się spawn'ują

#### Sprint 33: Bestiary — potwory
- [ ] Dodaj:
  - [ ] Werewolf
  - [ ] Vampire
  - [ ] Troll
  - [ ] Giant
  - [ ] Golem
- [ ] **UT:** Test special behaviors (werewolf: atak nocą silniejszy)
- [ ] **Test Build:** Potwory mają visual distinction (ASCII?)

#### Sprint 34: Bestiary — demony i bossowie
- [ ] Demon (typ podstawowy)
- [ ] Demon Baron (boss — fazy, miniony)
- [ ] Dragon (boss — fire, high HP)
- [ ] **UT:** Boss phase test
- [ ] **Test Build:** Bossowie są challenge, miniony się spawn'ują

#### Sprint 35: AI — Archer Logic (LOS)
- [ ] Zaimplementuj line of sight check dla łuczników
- [ ] Łucznik: jeśli ma LOS → strzelaj; jeśli nie → move na lepszą pozycję
- [ ] Position calculation: jeśli zablokowany → spróbuj obejść
- [ ] **UT:** `AIArcherTest` — position calculation, LOS validation
- [ ] **Test Build:** Walka z łucznikami jest trudniejsza, strategiczna

#### Sprint 36: AI — Alchemist Logic
- [ ] Alchemist: używa mikstur (koszty AP bardziej)
- [ ] Pierwsza tura: rzuć mikstrę ofensywną
- [ ] Jeśli HP < 30%: rzuć uzdrawiającą
- [ ] Jeśli AP skończy się: switch na melee
- [ ] **UT:** `AIAlchemistTest` — potion usage pattern
- [ ] **Test Build:** Walki z alchemistami są dynamiczne

#### Sprint 37: AI — Boss Logic (Phased Combat)
- [ ] Boss: 3 fazy HP
  - [ ] Faza 1 (100–66%): normal attacks
  - [ ] Faza 2 (66–33%): summon minions, AOE
  - [ ] Faza 3 (33–0%): desperation, high damage
- [ ] Zaimplementuj `BossCombatSystem`
- [ ] **UT:** `BossCombatTest` — phase transitions
- [ ] **Test Build:** Boss walki mają struktura, gracze mogą je wygrac

#### Sprint 38: Combat Balancing i Difficulty Modes
- [ ] Dodaj difficulty levels: EASY, NORMAL, HARD
- [ ] Modyfikuj stats wrogów per difficulty (health, damage)
- [ ] Testuj kilka walk (bandyci, boss, demon)
- [ ] **UT:** `DifficultyBalanceTest`
- [ ] **Test Build:** Wszystkie difficulty level'e działa sensownie

---

### ETAP VII: Ekonomia i Usługi (Sprinty 39–42)

#### Sprint 39: Ekonomia — ceny lokalne i mnożniki
- [ ] Zaimplementuj `EconomySystem` (jeśli nie ma):
  - [ ] basePrices: Map<ItemType, Int>
  - [ ] cityModifier(cityId): Float
  - [ ] reputationModifier(factionId): Float
- [ ] Ceny broni, zbroi, mikstur, jedzenia
- [ ] Reputacja Kupcami: -10% ceny
- [ ] Reputacja Kościołem: -10% usługi religijne
- [ ] **UT:** `EconomyPriceTest` — correct calculation
- [ ] **Test Build:** Ceny się dynamicznie zmieniają

#### Sprint 40: Usługi miasta (healing, training, accommodation)
- [ ] Healing service (leczenie ran): 5–20 GP per HP
- [ ] Training service (poprawa kariera): 50–200 GP
- [ ] Accommodation (noclegi): 1–5 GP per noc
- [ ] Zintegruj z quest eventami
- [ ] **UT:** Service cost test
- [ ] **Test Build:** Usługi się kupuje, efekty działają

#### Sprint 41: Bezpieczeństwo item'ów i logistyka
- [ ] Implementuj model jedzenia:
  - [ ] Każdy dzień: -1 jednostka jedzenia per hero
  - [ ] Brak jedzenia: -5 HP/dzień lub koniec podróży
- [ ] Waga ekwipunku (wpływa na szybkość podróży)
- [ ] **UT:** `ResourceManagementTest`
- [ ] **Test Build:** Gracze muszą zarządzać jedzeniem

#### Sprint 42: Trading i sprzedaż drużyny
- [ ] Zaktualizuj `TradeActivity`
- [ ] Kupno i sprzedaż przedmiotów
- [ ] Zapytaj reputacji handlowca = ceny
- [ ] Wyświetl kwotę złota drużyny
- [ ] **UT:** `TradingSystemTest`
- [ ] **Test Build:** Trading flow jest smooth

---

### ETAP VIII: Zawartość i Polishing (Sprinty 43–50)

#### Sprint 43: Miasta — paczka #3 (5+ dodatkowych miast)
- [ ] Dodaj: Gdańsk, Wrocław, Kraków, Linz, Konstancja
- [ ] Każde z: własnym characterem, eventami, NPC
- [ ] **UT:** City consistency test
- [ ] **Test Build:** 20+ miast na mapie, pełne wrażenie

#### Sprint 44: Święci — paczka #4 (finish list, 60+)
- [ ] Dodaj pozostałe święte (aż do 60+)
- [ ] Każdy: domenę, patronaż, bonusy
- [ ] **UT:** Complete saint list validation
- [ ] **Test Build:** Pełna lista świętych dostępna

#### Sprint 45: Random World Generation (proceduralne lokacje)
- [ ] Upewnij się, że losowe lokacje są seed-based
- [ ] Generator: castles, monasteries, ruins, villages, dungeons
- [ ] Zaimplementuj replayable world (seed save/load)
- [ ] **UT:** `ProceduralGenerationTest` — determinism
- [ ] **Test Build:** Każda gra ma inne losowe lokacje, ale konsekwentne w ramach seed

#### Sprint 46: Relikwie (Holy Relics) i turnieje rycerskie
- [ ] Implementuj system **relikwii**: artefakty z bonusami
  - [ ] Świętego Krzyża: +1 virtue/dzień
  - [ ] Czaszka Św. Jana: +2 w modlitwach
  - [ ] itp.
- [ ] Turnieje: start w mieście → bracket → finał
  - [ ] Nagrody: 100–500 GP, reputacja rycerska
- [ ] **UT:** `RelicSystemTest`, `TournamentTest`
- [ ] **Test Build:** Relikwie są znalezione, turnieje działają

#### Sprint 47: UI/UX — główne ekrany
- [ ] Zaktualizuj `MainActivity` — główne menu
- [ ] Dodaj: New Game, Load Game, Settings, About
- [ ] Character creation screen (uproszczone)
- [ ] Main game hub (przejście między ekranami bez crash'ów)
- [ ] **UT:** Navigation test
- [ ] **Test Build:** UI jest responsywny, wszystkie ekrany dostępne

#### Sprint 48: Save/Load Game — pełny system
- [ ] Zaimplementuj save slots (min. 3)
- [ ] Load screen: listing saves z datą, poziomem postępu
- [ ] Auto-save: co 10 minut / przed questem
- [ ] Versioning: support dla upgrade'ów save'ów
- [ ] **UT:** `SaveLoadTest` — corruption check, version migration
- [ ] **Test Build:** Gra się zapisuje i ładuje bez błędów

#### Sprint 49: Balancing i drobne poprawki
- [ ] Przejrzyj cały game loop: postęp, trudność, pacing
- [ ] Zbilansuj: ceny, damage, healing, experience
- [ ] Popraw: teksty, ikony, komunikaty
- [ ] **UT:** Full game integration test
- [ ] **Test Build:** Gra jest playable od start do endingu

#### Sprint 50: Release Candidate — finalna integracja
- [ ] Uruchom pełny test build: `./gradlew clean assembleDebug`
- [ ] Zweryfikuj CI/CD pipeline (GitHub Actions)
- [ ] Dokumentacja: README, BUILDING, CONTRIBUTING
- [ ] Szukaj ostatnich crash'ów i glitchy
- [ ] **UT:** Wszystkie unit testy muszą passować (`./gradlew test`)
- [ ] **Test Build:** Brak błędów, gra się uruchamia w emulatorze

---

## 5. LEGENDA UNIT TESTÓW (UT) I TEST BUILD

### Unit Tests (JVM)
- Każdy sprint: min. 1–2 nowe testy w `app/src/test/java/com/darklandsmobile/`
- Obejmują: core logic (walka, alchemia, systemy), nie UI
- Run: `./gradlew :app:testDebugUnitTest`
- Raport: `app/build/reports/tests/testDebugUnitTest/index.html`

### Test Build
- Każdy sprint: `./gradlew clean :app:assembleDebug`
- Weryfikuje: kompilacja, brak syntaktycznych błędów
- Artefakt: `app/build/outputs/apk/debug/app-debug.apk`
- Instalacja na emulatorze: `adb install -r app-debug.apk`
- Basic smoke test: app uruchamia się, brak crash'ów

---

## 6. TIMELINE I SZACUNKI

| Faza | Sprinty | Effort | Opis |
|---|---|---|---|
| **Setup** | 1–3 | Niski | Struktura, konwencje |
| **Świat** | 4–10 | Wysoki | Mapy, miasta, logistyka |
| **Święci** | 11–16 | Średni | Katalog + moce |
| **Kariery** | 17–21 | Niski/Średni | Enumy + UI |
| **Plotki+NPC** | 22–25 | Średni | Story layer |
| **Questy** | 26–31 | Wysoki | Zawartość fabularna |
| **Bestiary+AI** | 32–38 | Wysoki | Combat depth |
| **Ekonomia** | 39–42 | Średni | System handlu |
| **Finał** | 43–50 | Średni | Content + release |

**Szacunkowy czas (4-tygodniowe sprinty):** 50 sprintów ≈ 50 tygodni ≈ **12–15 miesięcy** dla zespołu 1–2 osób.

---

## 7. WNIOSEK

Projekt ma dobry fundament, ale wymaga **systematycznego rozbudowania świata i zawartości**. Roadmap 50 sprintów skupia się na:

1. **Świat zamiast systemów** (najważniejsze)
2. **Powiązanie systemów** (plotki, NPC, eventy)
3. **Zawartość masowa** (miasta, święci, kariery, questy)
4. **AI/Combat depth** (by gra była ciekawa)
5. **Polish & Release** (dlatego że to ma znaczenie)

Każdy sprint ma konkretne UT i test build checkpoint — dzięki temu będzie można śledzić progres i łatwo revert'ować, jeśli coś się zepsuje.

**Powodzenia! 🎮**

---

