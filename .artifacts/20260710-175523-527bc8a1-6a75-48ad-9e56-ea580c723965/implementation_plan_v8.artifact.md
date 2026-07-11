# Plan Implementacji - Etap 8: Ostateczny Szlif i Imersja (Final Polish)

Celem Etapu 8 jest domknięcie wszystkich drobnych braków, optymalizacja wydajności oraz wzmocnienie klimatu gry poprzez dźwięk i wizualia.

## Proponowane Zmiany

### 1. Optymalizacja Wydajności UI
Zapewnienie płynności działania na słabszych urządzeniach poprzez ograniczenie zbędnych przerysowań.

#### [CityViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/city/CityViewModel.kt) (i inne VM)
- Wprowadzenie `distinctUntilChanged()` na przepływach stanu.
- Upewnienie się, że `NPC Generator` nie uruchamia się przy każdej drobnej zmianie złota (tylko przy wejściu do miasta lub zmianie dnia).

### 2. Pełna Implementacja Audio
Powiązanie zdarzeń systemowych z `AudioEngine`.

#### [AudioEngine.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/AudioEngine.kt)
- Dodanie brakujących sampli dla:
    - Aktywacji Glitcha (niskie stability).
    - Użycia umiejętności Echo.
    - Śmierci bohatera i pojawienia się Cienia.

### 3. System Końcowy (Ending System)
Przygotowanie struktury pod różne zakończenia gry zależne od stanu świata.

#### [NEW] [EndingManager.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/EndingManager.kt)
- Logika sprawdzająca warunki zwycięstwa/porażki (np. całkowity Collapse vs Oczyszczenie Świata).
- Integracja z `GameRootViewModel`.

---

## Plan Weryfikacji

### Testy Automatyczne
- `EndingConditionsTest`: Sprawdzenie czy gra poprawnie rozpoznaje moment osiągnięcia danego zakończenia.

### Manualna Weryfikacja
- Test "stresu" UI: Szybkie zmienianie zakładek w Character Hub i obserwacja zużycia zasobów.
- Weryfikacja dźwięków przy użyciu nowych umiejętności z Etapu 7.
