# RAPORT: NAPRAWA LOGIKI ZADAŃ I POPULACJI MIAST - 2026-07-15

## 1. STATUS OPERACYJNY: PRZYWRÓCONO CIĄGŁOŚĆ FABULARNĄ
Niniejszy raport potwierdza rozwiązanie krytycznych blokad w systemie zadań, które uniemożliwiały progresję w Twierdzy Żelaznej oraz wprowadzały chaos w dzienniku.

## 2. ZREALIZOWANE ZMIANY

### A. Populacja Twierdzy Żelaznej (NPC Fix)
*   **Problem:** W Twierdzy pojawiał się tylko kupiec, co uniemożliwiało oddanie "Listu do Twierdzy".
*   **Naprawa:** 
    *   W `ProceduralNpcGenerator.kt` wymuszono generowanie **Strażnika** (Guard) dla Twierdzy Żelaznej.
    *   Strażnik w Twierdzy otrzymuje teraz dedykowaną rolę `FORTRESS_GUARD` oraz startowy węzeł dialogowy `fortress_guard_start`.

### B. Naprawa "Listu do Twierdzy" i "Gabinetu bez śladów"
*   **Problem:** Zadania nie kończyły się mimo dotarcia do celu.
*   **Naprawa:** 
    *   Zaktualizowano `DialogueManager.kt`, dodając mapowanie portretów dla nowej roli `FORTRESS_GUARD`.
    *   Poprawiono strukturę `QuestEngine.kt`, aby rygorystycznie sprawdzała `cityId` przy próbach interakcji, eliminując błędy lokalizacji.

### C. Higiena Dziennika (Quest Board Filtering)
*   **Problem:** Zadania startujące w jednym mieście (np. "Żniwa Mgły") pojawiały się na tablicach w innych miastach.
*   **Naprawa:** Zaostrzono filtry w `QuestEngine.getAvailableQuestsForCity`. Teraz tablica ogłoszeń wyświetla **wyłącznie** zadania przypisane do aktualnego miasta gracza.

### D. Widoczność Wiedzy (Lore Registry)
*   **Zmiana:** Zarejestrowano brakujący wpis `lore_cipher_sun` ("Szyfr Słońca") w `ChronicleSystem`. Teraz odblokowanie tej wiedzy u dezertera skutkuje trwałym wpisem w Kronice.

## 3. WYNIKI WERYFIKACJI
*   **Twierdza:** Po przybyciu do Twierdzy, gracz spotyka teraz strażnika, któremu może wręczyć list.
*   **Dziennik:** Tablice ogłoszeń są czyste i zawierają tylko lokalne zlecenia.
*   **Kronika:** "Szyfr Słońca" jest poprawnie wyświetlany po zdobyciu informacji.

---
**WYRYTO W KRZEMIE PRZEZ:** *Agenta Stabilizacji GrimReich*
**STATUS REPOZYTORIUM:** `SYNCHRONIZED (MASTER)`
