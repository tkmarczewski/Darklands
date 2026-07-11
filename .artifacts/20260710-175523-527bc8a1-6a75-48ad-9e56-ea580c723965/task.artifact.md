# Status Projektu - Darklands Refactor

## Etap 1: State Ownership & Persistence [ZAKOŃCZONO]
- [x] Unifikacja lokalizacji (`world.locationId`)
- [x] Migracja `item.id` -> `item.instanceId`
- [x] Refaktoryzacja `StatePersistenceManager` (Kotlinx, Mutex)
- [x] Uzupełnienie `normalizeState()`

## Etap 2: Model Domenowy i Centralne Ownership [ZAKOŃCZONO]
- [x] Implementacja `WorldStabilitySystem.kt`
- [x] Naprawa `QuestEngine.kt` (filtry, `minWorldDay`, idempotentność)
- [x] Poprawa `GameLoopController.kt` (podróż do celu questa, reset registry)
- [x] Integracja systemów z resztą kodu

## Etap 3: UI / UX Refaktor [ZAKOŃCZONO]
- [x] Modernizacja City i Expedition (UiEffect, Route/Content split)
- [x] Implementacja Character Hub (Bohater | Ekwipunek | Drużyna)
- [x] Usunięcie legacy "magic string protocol" z UI

## Etap 4: Content Validator [ZAKOŃCZONO]
- [x] Implementacja `ContentValidator.kt`
- [x] Dev Error Panel w UI

## Etap 5: Inicjatywa i Shuffling [ZAKOŃCZONO]
- [x] System Inicjatywy w walce
- [x] Shuffling zadań na tablicy

## Etap 6: Fabuła i Cienie [ZAKOŃCZONO]
- [x] Dodanie brakujących zadań fabularnych
- [x] Mechanika Cieni Towarzyszy

## Etap 7: Potęga Echa i Rozwój [ZAKOŃCZONO]
- [x] Rozszerzony katalog umiejętności (Echo/Faith)
- [x] Statystyki zależne od kariery
- [x] Rytuały Reality Leak (Echo Dust)
