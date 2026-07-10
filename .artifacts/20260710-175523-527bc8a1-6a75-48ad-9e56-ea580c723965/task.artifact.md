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

## Etap 4: Content Validator i Integralność Danych [/]
- [ ] Implementacja `ContentValidator.kt`
- [ ] Integracja z `GameBootstrapper.kt` (fail-fast w debug)
- [ ] Naprawa niespójności w obecnych plikach JSON
- [ ] Testy jednostkowe walidatora
