# Raport z Głębokiego Audytu Logiki - Wrzesień 2026

Ten raport podsumowuje najnowsze znaleziska z pełnego audytu kodu, skupiając się na błędach "cichych" (silent bugs), które nie powodują crashy, ale niszczą integralność rozgrywki i zapisu stanu.

## 1. Krytyczne Błędy Persystencji (Save/Load)
*   **AUD-01: Utrata `worldFlags` w DTO**: Klasa `QuestStateDto` (używana do serializacji zapisu) nie posiada pola `worldFlags`. Oznacza to, że wszystkie flagi fabularne (np. postęp kampanii Werdyktu, odblokowane przejścia) są **bezpowrotnie tracone** przy każdym zapisie i odczycie gry. Jest to błąd o najwyższym priorytecie.
*   **AUD-03: Błędne lookupy w ekonomii**: W `EconomySystem.kt` fallback dla frakcji to `"MERCHANTS"` (wielkie litery), podczas gdy klucze w mapie reputacji są małe (`"merchants"`). Powoduje to, że ceny sprzedaży zawsze ignorują reputację u Kupców, jeśli miasto nie ma jawnie przypisanej frakcji.

## 2. Błędy Walidacji i Mechaniki
*   **AUD-02: Ominięcie wymagań atrybutów**: W `ExpeditionViewModel.kt`, mimo że UI blokuje wizualnie opcje (`[LOCKED]`), metoda `handleEncounterChoice` nie sprawdza wymagań przed wykonaniem efektu. Gracz może wymusić akcję, która powinna być zablokowana.
*   **AUD-04: Martwe systemy zdarzeń**: `RandomEventManager` posiada logikę dla zdarzeń w miastach (`triggerCityEvent`) i w hubie (`triggerHubEvent`), ale nie są one podpięte do żadnego cyklu symulacji ani ViewModelu. Zdarzenia te obecnie nigdy się nie wyzwalają.

## 3. Architektura i Wyścigi (Race Conditions)
*   **AUD-05: Ryzyko inicjalizacji `GameRepository`**: Metoda `sync()` (ładująca bestiariusz i przedmioty) jest uruchamiana jako asynchroniczny job w `init`. Jeśli gra wystartuje bardzo szybko (np. na szybkim urządzeniu), ViewModel może spróbować pobrać dane z Bestiariusza, zanim ten zostanie załadowany, co skończy się błędami "Błąd Paradygmatu".
*   **AUD-06: Niekompletne wzorce `when`**: W `MutationSystem.kt` i mapperach występują klauzule `else` dla wyczerpujących enumów. Maskuje to potencjalne błędy przy dodawaniu nowych typów (kompilator nie ostrzeże o braku obsługi nowego enuma).

## Podsumowanie Statusu
| ID | Priorytet | Status | Lokalizacja |
| :--- | :--- | :--- | :--- |
| **AUD-01** | **KRYTYCZNY** | Oczekuje na fix | `GameState.kt` / `GameStateMappers.kt` |
| **AUD-02** | **WYSOKI** | Oczekuje na fix | `ExpeditionViewModel.kt` |
| **AUD-03** | **WYSOKI** | Oczekuje na fix | `EconomySystem.kt` |
| **AUD-04** | Średni | Oczekuje na integrację | `WorldSimulationCoordinator.kt` |
| **AUD-05** | Średni | Do poprawy | `GameRepository.kt` |

**Rekomendacja**: Natychmiastowa implementacja poprawek dla AUD-01 i AUD-03 w celu ratowania systemu zapisu i progresji.
