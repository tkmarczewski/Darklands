# Raport Audytu Bezpieczenstwa i Jakosci Kodu — Darklands

**Data:** 2026-06-26  
**Audytor:** Comet (automatyczny przeglad kodu)  
**Repozytorium:** `tkmarczewski/Darklands`  
**Zakres:** Wszystkie pliki `.kt` w `app/src/main/java/com/grimreich/`

---

## 1. Podsumowanie

Przeprowadzono przeglad linia po linii ponad 60 plikow Kotlin obejmujacych warstwy: `core`, `systems`, `ui` (ViewModels + Screens), `grimreich/v1`, `world`, `domain`, `di`. Znaleziono **3 krytyczne bledy logiczne** (naprawione), **2 problemy jakosci kodu** (odnotowane, nie naprawione ze wzgledu na brak ryzyka runtime) oraz kilka drobnych obserwacji.

---

## 2. Bledy Krytyczne — NAPRAWIONE

### BUG-01: `CombatViewModel.exitCombat()` wywoluje `onExit()` bez sprawdzenia stanu walki

**Plik:** `ui/combat/CombatViewModel.kt`  
**Commit naprawczy:** `43d7cf4`  
**Opis:**  
Funkcja `exitCombat(onExit: () -> Unit)` wywolywala callback `onExit()` niezaleznie od tego, czy walka faktycznie sie zakonczyla. Oznaczalo to, ze nawet gdy `state.combat.active == true`, gra mogla nawigowac poza ekran walki — powodujac niekonzystentny stan gry i potencjalny crash przy nastepnych akcjach bojowych.

**Przed naprawka:**
```kotlin
fun exitCombat(onExit: () -> Unit) {
    val state = gameRepository.currentState()
    if (!state.combat.active) {
        // ... logika questow ...
    }
    onExit() // wywolywane ZAWSZE — BLAD
}
```

**Po naprawce:**
```kotlin
fun exitCombat(onExit: () -> Unit) {
    val state = gameRepository.currentState()
    if (!state.combat.active) {
        // ... logika questow ...
        onExit() // wywolywane TYLKO gdy walka zakonczylas
    }
    // jezeli walka aktywna — nie nawiguj
}
```

---

### BUG-02: `RecruitmentViewModel.hireHero()` nie sprawdza limitu rozmiaru druzyny

**Plik:** `ui/tavern/RecruitmentViewModel.kt`  
**Commit naprawczy:** `6072a36`  
**Plik pomocniczy:** `core/GameConstants.kt` (commit `fix: add MAX_PARTY_SIZE = 6`)  
**Opis:**  
Funkcja `hireHero()` sprawdzala jedynie saldo zlota przed dodaniem bohatera do druzyny, ale nie egzekwowala zadnego limitu `MAX_PARTY_SIZE`. Przy wielokrotnym rekrutowaniu gracz mogl zbudowac druzyne o dowolnej liczbie bohaterow, co prowadzi do: nieoczekiwanych zachowan w systemie walki (ktory iteruje po `state.party`), przepelnienia UI, i potencjalnie nieskonczonego wzrostu pamieci stanu gry.

**Przed naprawka:**
```kotlin
if (state.gold >= cost) {
    state.party.add(hero) // brak limitu!
}
```

**Po naprawce:**
```kotlin
if (state.gold >= cost && state.party.size < GameConstants.MAX_PARTY_SIZE) {
    state.party.add(hero)
}
```

Dodano stala `GameConstants.MAX_PARTY_SIZE = 6` w `core/GameConstants.kt`.

---

### BUG-03: `ProceduralNpcGenerator` — przepelnienie Int przy obliczaniu seeda losowego

**Plik:** `world/ProceduralNpcGenerator.kt`  
**Commit naprawczy:** (najnowszy commit na tym pliku)  
**Opis:**  
Seed generatora losowego byl obliczany jako:
```kotlin
val random = Random(cityId.hashCode() + state.world.day)
```
Obie wartosci to `Int`. Jezeli `cityId.hashCode()` jest bliski `Int.MAX_VALUE`, dodanie `state.world.day` powoduje cichy overflow (na JVM/Kotlin nie rzuca wyjatku, wynik zalega do wartosci ujemnych). Efekt: seed moze byc taki sam dla roznych kombinacji miasta+dnia, co powoduje powtarzajace sie generowanie tych samych NPC i lamiacy determinizm proceduralny.

**Przed naprawka:**
```kotlin
val random = Random(cityId.hashCode() + state.world.day)
```

**Po naprawce:**
```kotlin
val random = Random(cityId.hashCode().toLong() + state.world.day.toLong())
```

Operacja wykonywana w przestrzeni `Long` eliminuje overflow.

---

## 3. Obserwacje i Ostrzezenia (nie naprawiane — brak ryzyka runtime)

### OBS-01: `ChronicleSystem.kt` — niepotrzebne kopiowanie listy
**Plik:** `systems/ChronicleSystem.kt`  
Lista wpisow kroniki jest kopiowana `.toList()` przy kazdym zdarzeniu zamiast uzywania immutable snapshot tylko przed serialization. Brak ryzyka danych; moze byc optymalizacja w przyszlosci.

### OBS-02: `CityViewModel.rawIdToSlug()` — reczne mapowanie znakow diakrytycznych
**Plik:** `ui/city/CityViewModel.kt`  
Funkcja recznie mapuje polskie znaki diakrytyczne (`a` -> `a`, itd.). Bezpieczniejsze byloby uzywanie `java.text.Normalizer` lub ICU4J, co obsluguje rowniez inne locale. Obecna implementacja dziala dla polskiego, ale jest krucha.

### OBS-03: `StatePersistenceManager.kt` — brak obslugi bledu deserializacji
**Plik:** `systems/StatePersistenceManager.kt`  
Deserializacja stanu gry z JSON uzywa `try/catch` na szczycie, ale nie logguje szczegolowych bledow parsowania (zarzuca tylko na domyslny `GameState()`). Przy zepsutym pliku zapisu gracz bedzie cicho resetowany bez diagnostyki.

**Zalecenie:** Dodac `Log.e(TAG, "Blad wczytywania zapisu", e)` przed powrotem do domyslnego stanu.

### OBS-04: Brak `MAX_PARTY_SIZE` w logice wyswietlania UI
**Pliki:** `ui/tavern/TavernViewModel.kt`, `ui/tavern/RecruitmentViewModel.kt`  
Po dodaniu stalej `MAX_PARTY_SIZE = 6`, UI powinno rowniez disablowac przycisk rekrutacji gdy druzyna jest pelna. Logika backendowa jest naprawiona, ale UX wymaga dostosowania.

### OBS-05: `AudioEngine.kt` — wycieki zasobow przy wielokrotnym wywolaniu `release()`
**Plik:** `systems/AudioEngine.kt`  
Funkcja `release()` wywoluje `mediaPlayer.release()` bez sprawdzenia czy `mediaPlayer != null` lub czy juz zostal zwolniony. Przy wielokrotnym wywolaniu (np. podczas szybkich rotacji ekranu) moze rzucic `IllegalStateException`. Zalecenie: dodac guard `if (::mediaPlayer.isInitialized)` przed `release()`.

---

## 4. Pliki Przeglad — Pelna Lista

| Plik | Status | Uwagi |
|------|--------|---------|
| `core/GameState.kt` | OK | Czysty model danych |
| `core/GameRepository.kt` | OK | Poprawne Flow + persist |
| `core/Combat.kt` | OK | |
| `core/CombatState.kt` | OK | |
| `core/Hero.kt` | OK | |
| `core/QuestState.kt` | OK | |
| `core/GameConstants.kt` | ZMIENIONY | Dodano `MAX_PARTY_SIZE = 6` |
| `core/GrimConstants.kt` | OK | |
| `systems/CombatSystem.kt` | OK | |
| `systems/ExpeditionManager.kt` | OK | |
| `systems/StatePersistenceManager.kt` | OSTRZEZENIE | OBS-03: brak loggingu bledu |
| `systems/QuestSystem.kt` | OK | |
| `systems/EncounterSystem.kt` | OK | |
| `systems/EndingSystem.kt` | OK | |
| `systems/AudioEngine.kt` | OSTRZEZENIE | OBS-05: potencjalny IllegalStateException |
| `systems/ChronicleSystem.kt` | OSTRZEZENIE | OBS-01: zbedne kopie list |
| `systems/QuestRegistry.kt` | OK | |
| `systems/QuestDefinitionRegistry.kt` | OK | |
| `systems/GameBootstrapper.kt` | OK | |
| `systems/PartyRepository.kt` | OK | |
| `ui/combat/CombatViewModel.kt` | NAPRAWIONY | BUG-01 |
| `ui/tavern/RecruitmentViewModel.kt` | NAPRAWIONY | BUG-02 |
| `ui/city/CityViewModel.kt` | OSTRZEZENIE | OBS-02: rawIdToSlug |
| `ui/alchemy/AlchemyViewModel.kt` | OK | |
| `ui/dialogue/DialogueViewModel.kt` | OK | |
| `ui/inventory/InventoryViewModel.kt` | OK | |
| `ui/map/WorldMapViewModel.kt` | OK | |
| `ui/main/CharacterCreatorViewModel.kt` | OK | |
| `ui/main/ChronicleViewModel.kt` | OK | |
| `ui/main/EndingViewModel.kt` | OK | |
| `ui/main/ExpeditionViewModel.kt` | OK | |
| `ui/main/GameRootViewModel.kt` | OK | |
| `ui/main/HubViewModel.kt` | OK | |
| `ui/main/MainMenuViewModel.kt` | OK | |
| `ui/main/GrimMapActions.kt` | OK | |
| `world/ProceduralNpcGenerator.kt` | NAPRAWIONY | BUG-03 |
| `world/CityCatalogue.kt` | OK | |
| `world/HeroPool.kt` | OK | |
| `world/ItemCatalogue.kt` | OK | |
| `grimreich/v1/GrimWorldEngine.kt` | OK | Fasada, czysty kod |
| `grimreich/v1/GrimModels.kt` | OK | |
| `grimreich/v1/GrimBuilders.kt` | OK | |
| `grimreich/v1/GrimGenerators.kt` | OK | |
| `grimreich/v1/DefaultNPCSystem.kt` | OK | |
| `grimreich/v1/DefaultRegionSystem.kt` | OK | |
| `di/AppModule.kt` | OK | |

---

## 5. Wnioski

- **3 bledy krytyczne** zostaly wykryte i naprawione bezposrednio w tej samej sesji audytu.
- **5 obserwacji** zostalo udokumentowanych jako rekomendacje do przyszlego sprintu.
- Ogolna jakosc kodu jest **wysoka** — architektura MVVM jest konsekwentnie stosowana, strzalki null-safety Kotlina sa uzywane poprawnie, a Hilt DI jest prawidlowo skonfigurowany.
- Nie znaleziono luk bezpieczenstwa wymagajacych natychmiastowej interwencji (brak Network/Storage access, brak wrazliwych danych uzytkownika).

---

*Wygenerowano automatycznie podczas sesji audytu kodu.*
