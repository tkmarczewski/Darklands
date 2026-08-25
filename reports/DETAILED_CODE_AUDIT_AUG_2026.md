# Szczegółowy Audyt Kodu - Sierpień 2026

## 1. System Zadań (Quest Engine)
*   **Problem z zadaniami powtarzalnymi**: Flaga `repeatable` w `QuestDefinition` jest całkowicie ignorowana. `evaluateDefinitionStatus` zwraca `QuestStatus.completed` bez sprawdzenia czy zadanie można powtórzyć.
*   **Brak walidacji nagród**: Nagrody w złocie i XP są przyznawane bez sprawdzenia czy nie przekraczają limitów systemowych.

## 2. Symulacja Świata (Stability & Simulation)
*   **Nieregularność efektów atmosferycznych**: `applyAtmosphericEffectsDirect` jest wywoływane tylko przy zmianie stabilności. Jeśli stabilność stoi w miejscu przez wiele dni, kary sezonowe (np. mróz w zimie) nie są aplikowane. Z kolei częste zmiany stabilności w ciągu jednego dnia kumulują kary.
*   **Nieużywany EncounterSystem**: `TravelSystem` wstrzykuje system spotkań, ale metoda `travelTo` nie wywołuje żadnych testów na spotkania losowe podczas podróży.

## 3. Zarządzanie Stanem i ViewModele
*   **Nienormalizowane ID**: W `DialogueViewModel` warunki `quest_active` i `quest_completed` nie używają `.lowercase()`, co może prowadzić do ukrycia opcji dialogowych przy literówkach w JSON.
*   **Zagrożenia w DevMenu**: Metody `startQuest` i `stepSuccess` mutowały stan poza atomowym blokiem `updateState` (poprawione w poprzednim kroku, ale wymaga weryfikacji testami).

## 4. Architektura i Purity
*   **Zagnieżdżone transakcje**: W `MutationEngine` i `EchoSystem` dochodziło do wywoływania metod z własnymi blokami `updateState` wewnątrz innych transakcji. 

## Plan Napraw i Hardeningu
1.  **QuestEngine**: Poprawa logiki `repeatable` questów.
2.  **Simulation**: Przeniesienie efektów sezonowych do daily tick w `WorldSimulation2_0`.
3.  **Travel**: Implementacja szansy na spotkanie losowe podczas podróży.
4.  **Dialogue**: Pełna normalizacja ID we wszystkich warunkach.
5.  **Tests**: Rozbudowa `QuestEngineAuditTest` i `TravelSystemTest`.
