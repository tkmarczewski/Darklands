# RAPORT: NAPRAWA LOGIKI ZADAŃ I INTERFEJSU MIAST - 2026-07-15

## 1. STATUS OPERACYJNY: ZGODNY
Zoptymalizowano interfejs miast, usunięto zbędne elementy z Huba oraz naprawiono błędy w logice zadań "Dezerter" i "List żelazny".

## 2. ZREALIZOWANE ZMIANY

### A. Poprawa Interfejsu (Accessibility)
*   **Hub:** Usunięto przycisk "MAPA", aby uprościć nawigację i uniknąć pomyłek (dostęp do mapy jest w mieście/ekspedycji).
*   **Miasto:** Dodano `verticalScroll` oraz proporcjonalne skalowanie do panelu lokacji. Przycisk **ALCHEMIK** (oraz inne, jeśli dojdą) jest teraz zawsze widoczny i dostępny.

### B. Naprawa Logiki Zadań
*   **Quest Dezertera:** Naprawiono błąd uniemożliwiający zakończenie zadania. `DialogueManager` poprawnie mapuje teraz polskie nazwy ról (np. `STRAZNIK`), co pozwala na odblokowanie węzłów raportowania.
*   **List Żelazny:** Usunięto błąd pozwalający na oddanie listu na Wybrzeżu. Logika sprawdzania `cityId` w `QuestEngine` została zaostrzona.
*   **Twierdza Żelazna (Cisza NPC):** Rozszerzono system mapowania portretów i ról o polskie odpowiedniki. NPC w Twierdzy (Straznik) posiadają teraz poprawnie przypisane identyfikatory, co eliminuje "ciszę" przy próbie interakcji.

## 3. WYNIKI WERYFIKACJI
*   **UI:** Panel lokacji w mieście przewija się płynnie. Alchemik jest dostępny.
*   **Quest:** Felix może teraz poprawnie zameldować o dezerterze strażnikowi na Wybrzeżu.
*   **Twierdza:** NPC reagują na obecność bohatera i posiadane przedmioty questowe.

---
**WYRYTO W KRZEMIE PRZEZ:** *Agenta Stabilizacji GrimReich*
**STATUS REPOZYTORIUM:** `SYNCHRONIZED (MASTER)`
