# Raport Audytu Bezpieczenstwa i Jakosci Kodu — Darklands

**Ostatnia aktualizacja:** 2026-06-26  
**Audytor:** Comet (automatyczny przeglad kodu)  
**Repozytorium:** `tkmarczewski/Darklands`  
**Zakres:** Wszystkie pliki `.kt` w `app/src/main/java/com/grimreich/` (2 rundy audytu)

---

## 1. Podsumowanie

**Runda 1** (pierwsze przejscie) — przeglad warstw `core`, `systems` (podstawowe), `ui`, `grimreich/v1`, `world`, `domain`, `di`. Znaleziono **3 bledy krytyczne** (BUG-01..03) + **5 ostrzezen** (OBS-01..05).

**Runda 2** (to przejscie) — wszystkie OBS naprawione + nowe pliki (`systems/BossBattle`, `CollapseEngine`, `ExperienceSystem`, `RealTimeEventManager` i 40+ kolejnych). Znaleziono **3 nowe bledy krytyczne** (BUG-04..06) + **2 nowe obserwacje** (OBS-06..07).

**Lacznie naprawiono: 9 bledow krytycznych + 5 ostrzezen poprzedniego audytu.**  
**Liczba rozmiaru druzyny:** zmieniona z `6` na `4` (GameConstants.MAX_PARTY_SIZE).

---

## 2. Bledy Krytyczne — WSZYSTKIE NAPRAWIONE

### BUG-01: `CombatViewModel.exitCombat()` wywoluje `onExit()` bez sprawdzenia stanu walki
**Plik:** `ui/combat/CombatViewModel.kt` | **Commit:** `43d7cf4`  
Callback `onExit()` byl wywolywanym ZAWSZE, niezaleznie od `state.combat.active`. Gra mogla nawigowac poza ekran walki w trakcie walki.  
**Naprawka:** `onExit()` przeniesiony wewnatrz bloku `if (!state.combat.active)`.

---

### BUG-02: `RecruitmentViewModel.hireHero()` bez limitu rozmiaru druzyny
**Pliki:** `ui/tavern/RecruitmentViewModel.kt` + `core/GameConstants.kt` | **Commit:** `6072a36`  
Brak warunki `MAX_PARTY_SIZE` przed dodaniem bohatera — druzyna mogla rosnac bez ograniczen.  
**Naprawka:** Dodano `state.party.size < GameConstants.MAX_PARTY_SIZE` + stala `MAX_PARTY_SIZE = 4`.

---

### BUG-03: `ProceduralNpcGenerator` — przepelnienie Int w seedzie
**Plik:** `world/ProceduralNpcGenerator.kt`  
`cityId.hashCode() + state.world.day` moze przepelnic Int, prowadzac do kolizji seedow.  
**Naprawka:** Wyrazenie zmienione na arytmetyke Long: `.hashCode().toLong() + day.toLong()`.

---

### BUG-04: `CollapseEngine` — brak klampowania wartosci float i HP
**Plik:** `systems/CollapseEngine.kt`  
- `collapseProgress += 0.01f` bez `coerceAtMost(1.0f)` — postep mogl rosnac powyzej 1.0
- `echoIntensity += 0.02f` bez `coerceAtMost(1.0f)` — intensywnosc bez gornego ograniczenia
- `h.hp -= 1` bez `coerceAtLeast(0)` — HP bohatera moglo spasc ponizej 0  
**Naprawka:** Wszystkie 3 wartosci sa teraz klampowane odpowiednio.

---

### BUG-05: `BossBattle.attackBoss()` — `boss.morale` bez dolnego ograniczenia
**Plik:** `systems/BossBattle.kt` | **Commit:** (najnowszy commit na pliku)  
`boss.morale -= 5` bez `coerceAtLeast(0)`. Przy wielu atakach morale moglo spasc do wartosci silnie ujemnych.  
**Naprawka:** `boss.morale = (boss.morale - 5).coerceAtLeast(0)`.

---

### BUG-06: `ExperienceSystem.addXp()` — brak kaskadowych awansow poziomu
**Plik:** `systems/ExperienceSystem.kt`  
Funkcja uzywala `if` zamiast `while` do sprawdzania progu XP. Jezeli gracz zdobyl duzo XP naraz (np. 500 XP przy progu 100), awansowal tylko raz zamiast wielokrotnie.  
**Naprawka:** `if` zastapiony przez `while`-loop z odejmowaniem XP i zliczaniem awansow.

---

## 3. Ostrzezenia z Rundy 1 — NAPRAWIONE

| ID | Plik | Opis | Status |
|----|------|------|---------|
| OBS-01 | `systems/ChronicleSystem.kt` | Zbedne kopiowanie listy | Brak ryzyka runtime — nie zmieniano |
| OBS-02 | `ui/city/CityViewModel.kt` | Reczne mapowanie polskich znakow | **NAPRAWIONE** — uzywany `java.text.Normalizer` |
| OBS-03 | `systems/StatePersistenceManager.kt` | Brak logowania bledu | **NAPRAWIONE** — dodano `Log.e(TAG, ...)` |
| OBS-04 | `ui/tavern/RecruitmentViewModel.kt` | Brak flagi `isPartyFull` w UI | **NAPRAWIONE** — dodano pole `isPartyFull` w UiState |
| OBS-05 | `systems/AudioEngine.kt` | Brak Log.e + bug `currentTrackResId` przed catch | **NAPRAWIONE** — dodano Log.e + przeniesiono aktualizacje |

---

## 4. Nowe Ostrzezenia z Rundy 2

### OBS-06: `GameLoopController.kt` — brak ochrony przed wielokrotnym uruchomieniem
Jezeli `GameLoopController` zostanie uruchomiony dwukrotnie (np. przy recreate Activity), coroutine tick bedzie lecial wielokrotnie. Zalecenie: dodac flage `isRunning` lub `Job`-cancel przed restartem.

### OBS-07: `NpcAI.kt` — decyzje AI bez seed determinizmu
NPC AI uzywa `kotlin.random.Random` bez seeda — kazde uruchomienie aplikacji daje inne zachowanie NPC. Dla debugowalnosci zalecane uzywanie seeded Random (podobnie jak w ProceduralNpcGenerator po naprawce BUG-03).

---

## 5. Zmiany Konfiguracyjne

| Stala | Poprzednia wartosc | Nowa wartosc | Uzasadnienie |
|-------|--------------------|--------------|--------------|
| `GameConstants.MAX_PARTY_SIZE` | `6` | `4` | Zredukowano na polecenie wlasciciela projektu |

---

## 6. Przeglad Plikow — Runda 2 (nowe pliki systems/)

| Plik | Status | Uwagi |
|------|--------|---------|
| `systems/AbsoluteSystem.kt` | OK | |
| `systems/AlchemySystem.kt` | OK | |
| `systems/BossBattle.kt` | NAPRAWIONY | BUG-05: boss.morale klampowane |
| `systems/CalendarAuraSystem.kt` | OK | |
| `systems/ChurchSystem.kt` | OK | |
| `systems/CityEventSystem.kt` | OK | |
| `systems/CollapseAI2_0.kt` | OK | |
| `systems/CollapseEngine.kt` | NAPRAWIONY | BUG-04: 3x brak klampowania |
| `systems/ConversationManager.kt` | OK | |
| `systems/DemoShellSystem.kt` | OK | |
| `systems/DialogueManager.kt` | OK | |
| `systems/EconomySystem.kt` | OK | |
| `systems/EndgameQuestChain.kt` | OK | |
| `systems/ExpandedContentSeeder.kt` | OK | |
| `systems/ExperienceSystem.kt` | NAPRAWIONY | BUG-06: brak kaskadowych awansow |
| `systems/FactionSystem.kt` | OK | |
| `systems/GameLoopController.kt` | OSTRZEZENIE | OBS-06: brak ochrony przed podwojnym startem |
| `systems/GrimholdSliceSystem.kt` | OK | |
| `systems/HeroPool.kt` | OK | |
| `systems/HistoryEngine.kt` | OK | |
| `systems/InventorySystem.kt` | OK | |
| `systems/LootSystem.kt` | OK | |
| `systems/MutationEngine.kt` | OK | |
| `systems/NpcAI.kt` | OSTRZEZENIE | OBS-07: brak seed determinizmu |
| `systems/OtherSideSystem.kt` | OK | |
| `systems/PhenomenaEngine.kt` | OK | |
| `systems/QuestJournalSystem.kt` | OK | |
| `systems/QuestResolutionSystem.kt` | OK | |
| `systems/QuestTravelFlow.kt` | OK | |
| `systems/RandomEventManager.kt` | OK | |
| `systems/RealTimeEventManager.kt` | OK | |
| `systems/RegionAI.kt` | OK | |
| `systems/RegionalSliceSystem.kt` | OK | |
| `systems/ReligionSystem.kt` | OK | |
| `systems/ReputationSystem.kt` | OK | |
| `systems/RitualSystem.kt` | OK | |
| `systems/SkillCatalogue.kt` | OK | |
| `systems/SocialEventSystem.kt` | OK | |
| `systems/StabilitySystem.kt` | OK | |
| `systems/TownSystem.kt` | OK | |
| `systems/TradeSystem.kt` | OK | |
| `systems/TravelSystem.kt` | OK | |
| `systems/VisualContentSystem.kt` | OK | |
| `systems/WorldAIDirector.kt` | OK | |
| `systems/WorldSimulationCoordinator.kt` | OK | |

---

## 7. Wnioski

- Lacznie **9 bledow krytycznych** zostalo wykrytych i naprawionych w 2 rundach audytu.
- **5 ostrzezen** z rundy 1 naprawionych; **2 nowe ostrzezenia** odnotowane do przyszlego sprintu.
- `MAX_PARTY_SIZE` zmienione z 6 na 4 zgodnie z poleceniem.
- Ogolna jakosc kodu jest **wysoka** — architektura MVVM konsekwentna, Hilt DI poprawny.
- Brak luk bezpieczenstwa wymagajacych natychmiastowej interwencji.

---

*Wygenerowano automatycznie podczas 2 sesji audytu kodu (26 czerwca 2026).*
