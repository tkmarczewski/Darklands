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

## Etap 3: UI / UX Refaktor [/]
- [ ] Modernizacja City i Expedition (UiEffect, Route/Content split)
- [ ] Implementacja Character Hub (Bohater | Ekwipunek | Drużyna)
- [ ] Usunięcie legacy "magic string protocol" z UI
