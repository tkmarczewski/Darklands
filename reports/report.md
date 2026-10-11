# Raport Szczegółowego Audytu 25 Błędów Kodowych (BUG-01 do BUG-25 Report)

Niniejszy raport podsumowuje pełny audyt 25 błędów zidentyfikowanych w strukturze aplikacji **GrimReich / Darklands** (warstwy `Core`, `Systems`, `World`, `UI`, `ViewModels` oraz `StatePersistence`), potwierdzenie ich obecności w kodzie oraz opis wprowadzonych i przetestowanych poprawek.

---

## 1. Wykaz i Analiza Błędów Krytycznych (Severity: CRITICAL)

1. **BUG-01: Odejmowanie statystyki `piety` zamiast zasobu `divineFavor` (`Combat.kt`)**
   - **Analiza**: W ewaluatorze rundy `CombatRound.resolveRound()` koszt czarów religijnych był odejmowany bezpośrednio ze stałego atrybutu postaci `piety`, zamiast odnawialnego zasobu `divineFavor`.
   - **Poprawka**: Dodano pole `divineFavor` do `CombatantState`, uzupełniane w `CombatSystem.heroToCombatant()`, oraz zmieniono rozliczanie czarów w `CombatRound` na `attacker.divineFavor -= skill.favorCost`.

2. **BUG-02: Wyścig Zapisu w `SaveSystem.saveToPersistence()` (`SaveSystem.kt`)**
   - **Analiza**: `saveToPersistence()` uruchamiało zapis asynchroniczny na puli `Dispatchers.IO` bez współbieżnej kolejki, co groziło nadpisaniem nowszej migawki przez wolniejszy starszy zapis.
   - **Poprawka**: Dodano sekwencyjny `persistenceLock = Mutex()` w `SaveSystem`.

3. **BUG-03: Brak Pól `enemyType` i `lastWealthDrainDay` w DTO (`GameState.kt` & `GameStateMappers.kt`)**
   - **Analiza**: Pola `enemyType` w `CombatState` oraz `lastWealthDrainDay` w `WorldState` istniały w modelu gry, ale były pomijane w DTO serializacji.
   - **Poprawka**: Dodano `val enemyType: String? = null` do `CombatStateDto` oraz `val lastWealthDrainDay: Long = 0L` do `WorldStateDto` wraz z pełnym mapowaniem w `GameStateMappers.kt`.

4. **BUG-04: Kontratak Ignorujący Siłę Obrońcy (`Combat.kt`)**
   - **Analiza**: Formuła ataku zwykłego uwzględniała `defender.strength / 2`, podczas gdy `resolveCounterAttack()` brało tylko `defender.attackBase`.
   - **Poprawka**: Zaktualizowano wzór na `((defender.attackBase + defender.strength / 2) * defenderStatus.attackModifier() * ...)`.

5. **BUG-05: Ryzyko Błędnego Zakresu Losowego w `postCombatRecovery()` (`Combat.kt`)**
   - **Analiza**: `randomProvider.nextInt(min, max + 1)` nie weryfikował relacji `min <= max`.
   - **Poprawka**: Dodano bezpieczną weryfikację `if (maxHeal >= minHeal)` oraz podniesiono limit odzysku wytrzymałości do `100`.

6. **BUG-06: Niepełna Normalizacja Stanu w `GameState.normalizeState()` (`GameState.kt`)**
   - **Analiza**: `normalizeState()` pomijało ograniczenie `world.fatigue` oraz `world.collapseProgress`.
   - **Poprawka**: Dodano `world.fatigue = world.fatigue.coerceIn(0, 100)` oraz `world.collapseProgress = world.collapseProgress.coerceIn(0f, 1f)`.

7. **BUG-07: Suma Kontrolna Stanu w `SaveSnapshot` (`SaveSystem.kt`)**
   - **Analiza**: `checksum` w `SaveSnapshot` pozostawał `null` przy zapisie.
   - **Poprawka**: Zapis wylicza i dołącza hash stanu `SaveIntegrity.computeStateHash(state).toString()`.

---

## 2. Wykaz Błędów o Wysokim i Średnim Priorytecie (BUG-08 do BUG-25)

8. **BUG-08**: Kontratak w walce rozliczany zgodnie z mechaniką kontrowania starcia.
9. **BUG-09**: Kompatybilność wsteczna `reachedThresholds = emptyList()` w `WorldStateDto`.
10. **BUG-10**: Inicjalizacja węzłów dialogowych w `DialogueManager.kt` chroniona przez bloki synchronizacyjne `loadLock`.
11. **BUG-11**: Akcja `pay_gold` w dialogach waliduje posiadaną ilość złota gracza.
12. **BUG-12**: Pętla sprawdzania statusów w `QuestEngine.kt` operuje na lokalnych gałęziach odwiedzin.
13. **BUG-13**: Zakresy kar wiekowych w `AgingSystem.kt` rozbite na sekwencję `if ... else if`.
14. **BUG-14**: Przekazywanie ekwipunku w `InventorySystem.transferItem()` dba o zdjęcie obecnego przedmiotu ze slotu odbiorcy bez jego usunięcia.
15. **BUG-15**: `CombatSystem.handleHeroDeath()` zapobiega spadek `currentTurnIndex` poniżej 0 po śmierci pierwszego bohatera w kolejce.
16. **BUG-16**: Potrącanie kosztów Echa skonsolidowane w `CombatRound`.
17. **BUG-17**: Wskrzeszenie `RitualSystem.performResurrection()` ustawia `hp = 1` przed wywołaniem normalizacji.
18. **BUG-18**: Waga przedmiotu questowego `quest_corpse` zmniejszona do 10.0 kg, unikając paraliżu udźwigu drużyny.
19. **BUG-19**: Modyfikatory udźwigu i przeciążenia w `InventorySystem`.
20. **BUG-20**: Przeciwnicy w `CombatSystem.getEnemyCombatant()` inicjalizują pole `echo` z posiadanych statusów echa.
21. **BUG-21**: Logika sztucznej inteligencji NPC w `NpcAI.kt` oparta na deterministycznym ziarnie z dnia świata.
22. **BUG-22**: Usunięto niebezpieczne wywołanie `gameRepository.close()` z `MainActivity.onDestroy()`.
23. **BUG-23**: Zablokowano pętlę przechodzenia do ekranu rytuału w `GameRootViewModel` po powrocie do menu głównego.
24. **BUG-24**: Zapis zapobiega równoległym kolizjom I/O w `GameRootViewModel`.
25. **BUG-25**: Zbalansowano odejmowanie atrybutów w kreacji postaci.

---

## 3. Podsumowanie Weryfikacji
- **Testy jednostkowe**: `app:testDebugUnitTest` -> **93 PASSED, 0 FAILED**.
- **Kompilacja**: `app:assembleDebug` -> **BUILD SUCCESSFUL**.
- **Wdrożenie Git**: Wszystkie poprawki zostały zapisane i spushowane do gałęzi `master`.
