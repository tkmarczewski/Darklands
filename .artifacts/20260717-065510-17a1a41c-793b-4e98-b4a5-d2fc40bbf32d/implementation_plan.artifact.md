# Plan Implementacji - Naprawa błędów i audyt kodu

Celem jest usunięcie krytycznych błędów zidentyfikowanych w audycie oraz weryfikacja wszystkich fragmentów kodu oznaczonych jako "TO BE CHECKED".

## Proponowane zmiany

### 🔴 Naprawa błędów krytycznych

#### [CombatSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/CombatSystem.kt)
- **BUG-01**: Zamiana bezpośredniego dostępu do indeksu na `getOrNull` w pętli inicjatywy.
- **BUG-02**: Bezpieczne pobieranie AI z Bestiary z domyślnym fallbackiem.
- **BUG-06**: Poprawka aktualizacji efektów statusu (trucizna, krwawienie) po turze przeciwnika, aby nie były gubione przez lokalne kopie.

#### [StatePersistenceManager.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/StatePersistenceManager.kt)
- **BUG-03**: Naprawa separatora nowej linii przy wczytywaniu zapisu (zamiana `"\\n"` na `"\n"`).

#### [GameStateMappers.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/GameStateMappers.kt)
- **BUG-04**: Dodanie `runCatching` przy mapowaniu enumów (Career, Trait itp.) dla kompatybilności wstecznej.

#### [RitualSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/RitualSystem.kt)
- **BUG-05**: Naprawa logiki HP podczas rytuału – zapobieganie nieśmiertelności wynikającej z niewłaściwego wywołania `normalize()`.

### 🟠 Weryfikacja "TO BE CHECKED"
- Przegląd i finalizacja logiki w `DialogueManager.kt`, `CityViewModel.kt` oraz `GameRootViewModel.kt`.

## Plan Weryfikacji

### Testy Automatyczne
- `gradle_build("app:assembleDebug")` – sprawdzenie kompilacji.

### Weryfikacja Manualna (Logi)
- Uruchomienie gry jako "Zloty" (nie Felix).
- Testowanie walki i śmierci bez użycia DEV menu.
- Analiza logcat pod kątem tagów `TRIBUNAL` i błędów systemowych.
