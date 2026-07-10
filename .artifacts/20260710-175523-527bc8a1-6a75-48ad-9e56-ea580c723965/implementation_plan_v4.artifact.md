# Plan Implementacji - Etap 4: Content Validator i Integralność Danych

Celem Etapu 4 jest stworzenie systemów ochronnych, które będą automatycznie weryfikować spójność nowej zawartości (zadania, dialogi, NPC, przedmioty) przed jej udostępnieniem w grze.

## Proponowane Zmiany

### 1. Centralny Content Validator
Stworzenie systemu, który przeskanuje wszystkie zarejestrowane definicje i zgłosi błędy (np. odniesienia do nieistniejących NPC).

#### [NEW] [ContentValidator.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/ContentValidator.kt)
- **Walidacja Questów**: Sprawdzenie czy `cityId`, `originNpcId` oraz `prerequisiteQuestId` istnieją w rejestrach.
- **Walidacja Dialogów**: Weryfikacja czy wszystkie `targetNodeId` prowadzą do istniejących węzłów.
- **Walidacja Przedmiotów**: Sprawdzenie czy rynki (`CityMarket`) nie oferują przedmiotów, których nie ma w `ItemCatalogue`.
- **Wykrywanie Cykli**: Automatyczne znajdowanie błędnych pętli w wymaganiach zadań (prerequisites).

### 2. Narzędzia Deweloperskie (Dev-Only)
Integracja walidatora z procesem startu gry w trybie debugowania.

#### [GameBootstrapper.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/GameBootstrapper.kt) (modyfikacja)
- Uruchomienie pełnej walidacji po załadowaniu wszystkich definicji.
- Zatrzymanie startu gry (crash w trybie debug) przy wykryciu błędów krytycznych, aby programista natychmiast wiedział o błędzie w JSON.

### 3. Poprawa Spójności JSON
Przegląd obecnych plików JSON i naprawa ewentualnych brakujących referencji wykrytych przez nowy system.

---

## Plan Weryfikacji

### Testy Automatyczne
- `ContentValidatorTest`: Testowanie walidatora na specjalnie przygotowanych "zepsutych" definicjach.

### Weryfikacja Manualna
- Celowe wprowadzenie literówki w `quests_extended.json` i sprawdzenie, czy system poprawnie zgłosi błąd przy starcie gry.
