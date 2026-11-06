# Raport Audytu Kodu: Naprawa Postępu Zadań, Pętli Czasu Ekspedycji i Walidacji Cech (Commit 4b6bdb9)

## Wykaz Załatanych Podatności i Łatek

### 1. [P1/P2] Zabezpieczenie przed Wyzerowaniem Postępu Aktywnego Zadania (`QuestEngine.kt`)
- **Problem:** Wywołanie `activateQuestDirect()` dla zadania znajdującego się już w zbiorze `activeQuestIds` generowało nowy obiekt `QuestProgress(questId, QuestStatus.active)` i nadpisywało istniejącą pozycję w mapie `progress`, co **resetowało wskaźnik wykonanego kroku (`currentStepIndex`) z powrotem do 0**.
- **Rozwiązanie:** W `QuestEngine.kt` dodano weryfikację: jeśli zadanie znajduje się już na liście aktywnych, aktywacja zostaje zignorowana (`return`), chroniąc dotychczasowy postęp gracza.

### 2. [P2] Prawidłowy Upływ Czasu po Ekspedycji (`ExpeditionViewModel.kt`)
- **Problem:** Powrót z ekspedycji (`OnBackClick`) wyłączał jedynie flagę `isExpeditionActive`, nie powiadamiając `WorldStabilitySystem` o upływie dnia. Dzień w grze stał w miejscu (`DZIEŃ 1`), a efekty statusowe na świecie oraz progresja zapaści nie aktualizowały się podczas zwykłej gry.
- **Rozwiązanie:** Podłączono `worldStabilitySystem.advanceDayDirect(s, "Powrót z ekspedycji")` przy powrocie z eksploracji. Upływ czasu, wygasanie efektów świata oraz mechaniki zapaści działają teraz w pełni automatycznie.

### 3. [P2] Ochrona Premii Dziedzictwa `SCRIBES_EYE` przy Zmianie Profesji i Losowaniu (`CharacterCreatorViewModel.kt`)
- **Problem:** Wybór nowej profesji (`selectCareer()`) oraz losowanie statystyk (`randomizeAttributes()`) resetowały atrybuty do domyślnej bazy, wymazując odblokowaną w metagrze premię dziedzictwa (+5 INT).
- **Rozwiązanie:** W obu metodach wbudowano ponowne nakładanie premii `SCRIBES_EYE` z `persistentMeta` po resecie bazy atrybutów.

### 4. [P2] Wymuszanie Wymagań Cech przy Pustej Drużynie (`ExpeditionViewModel.kt`)
- **Problem:** Wymagania cech w opcjach ekspedycji były sprawdzane wyłącznie przy obecności aktywnego bohatera. Gdy w drużynie braknęło żywych członków, kod pomijał walidację i natychmiast wykonywał efekt.
- **Rozwiązanie:** W `ExpeditionViewModel.kt` brak aktywnego bohatera przy wyborze wymagającym cechy zwraca komunikat błędu `"Brak aktywnego bohatera do wykonania tej akcji."`.

### 5. [P2] Bezpieczeństwo Wątków i Izolacja Referencji w Rejestrze Echa (`EchoSystem.kt`)
- **Problem:** Lista `eternalHeroes` była modyfikowana bez synchronizacji między wątkami (IO vs UI). Dodatkowo rejestrowano bezpośrednie referencje do obiektów `Hero`, co pozwalało na mutację zarejestrowanego echa w pamięci runtime.
- **Rozwiązanie:** Opakowano operacje na `eternalHeroes` w zsynchronizowany blok `synchronized(lock)` oraz zastosowano głębokie kopiowanie (`hero.deepCopy()`).

---

## Wyniki Suity Testowej
* **Wszystkie testy jednostkowe i integracyjne:** 149 passed, 0 skipped, 0 failed.
* **Git Master:** Commit `4b6bdb9` został wysłany do zdalnego repozytorium.
* **Status:** Kod jest w pełni spójny, deterministyczny i bezpieczny.
