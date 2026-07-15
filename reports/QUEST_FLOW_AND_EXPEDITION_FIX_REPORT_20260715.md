# RAPORT: NAPRAWA PRZEPŁYWU ZADAŃ I EKSPEDYCJI - 2026-07-15

## 1. STATUS OPERACYJNY: PRZYWRÓCONO SPÓJNOŚĆ NARRACYJNĄ
Wyeliminowano błędy w logice zadań, które powodowały przedwczesną aktywację kampanii Trybunału oraz brak dostępności ekspedycji.

## 2. ZREALIZOWANE ZMIANY

### A. Narracyjne Wprowadzenie Trybunału (Ravenn Ambush)
*   **Problem:** Zadanie `q_verdict_1` pojawiało się automatycznie w dzienniku, omijając spotkanie z Ravennem.
*   **Naprawa:**
    *   Usunięto `q_verdict_1` z tablicy ogłoszeń w miastach (`QuestEngine`).
    *   Zaimplementowano mechanizm "zaczepienia" (ambush) w `CityViewModel`. Gdy system incydentów osiągnie próg (7 wizyt), Ravenn automatycznie przerywa wejście do miasta, wywołując dedykowany dialog.
    *   Dodano nowe węzły dialogowe: `ravenn_ambush_suspect` oraz `ravenn_ambush_investigator`.

### B. Filtrowanie Tablicy Ogłoszeń
*   **Problem:** Na tablicy w każdym mieście widoczne były zadania z całego świata.
*   **Naprawa:** `CityViewModel` filtruje teraz `allAvailableQuests`, ograniczając je wyłącznie do obecnej lokalizacji gracza.

### C. Dostępność Ekspedycji w Hubie
*   **Problem:** Przycisk wyprawy był niewidoczny dla gracza, uniemożliwiając wykonanie zadań bojowych/eksploracyjnych.
*   **Naprawa:** Skorygowano warunek widoczności w `HubScreen.kt`. Ekspedycja jest teraz zawsze dostępna w Dniu 1 (jako tutorial) oraz gdy gracz posiada aktywne zadania wymagające wyjścia w teren.

### D. Spójność Inicjalizacji (Race Condition Fix)
*   **Zmiana:** Zastosowano `syncMutex` w `GameRepository` podczas `replaceState`, co gwarantuje, że katalogi zadań i miast są w pełni załadowane przed startem UI.

## 3. WYNIKI WERYFIKACJI
*   **Test Emulator:** 
    *   Tablica ogłoszeń w Wybrzeżu Północnym pokazuje tylko 3 lokalne questy.
    *   Przycisk `EKSPEDYCJA` jest widoczny od początku gry.
    *   Ravenn nie aktywuje się do momentu zebrania 7 incydentów/wizyt (budowanie napięcia).

---
**WYRYTO W KRZEMIE PRZEZ:** *Agenta Stabilizacji GrimReich*
**STATUS REPOZYTORIUM:** `SYNCHRONIZED (MASTER)`
