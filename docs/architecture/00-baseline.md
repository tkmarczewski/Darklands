# Baseline Architektury - Etap 0

## Status Kompilacji i Testów (Baseline)
Data: 2026-07-10
Build: **FAILED**

### Błędy Kompilacji (Unit Tests)
- `DependencyInjectionFixTest.kt`: Brak parametru `chronicleSystem`.
- `ContentValidationTest.kt`: Brak parametru `chronicleSystem`, brak metod `listMissingTargets`, `hasNode`.

---

## Pełna Inwentaryzacja Problemów

### 1. Bezpośrednie Mutacje GameState (Direct Mutations)
Wykryto liczne miejsca, gdzie systemy domenowe bezpośrednio modyfikują pola `GameState` zamiast korzystać z dedykowanych metod lub wyzwalaczy:
- **Statystyki Świata**: `OntologicalEngine.kt`, `StabilitySystem.kt`, `MutationSystem.kt`, `EncounterSystem.kt`, `ChurchSystem.kt`, `RitualSystem.kt`, `RandomEventManager.kt`.
- **Ekonomia**: `CommoditySystem.kt` (TradingEngine), `MarketViewModel.kt`.
- **Postacie**: `CombatSystem.kt` (HP, statusy), `InjurySystem.kt`, `AgingSystem.kt`, `ExperienceSystem.kt`.
- **Logika UI**: `ExpeditionViewModel.kt` (init), `CityViewModel.kt`.

### 2. Dualizm Lokalizacji (Duplicate Source of Truth)
- `grimCurrentRegion` (w głównym `GameState`): Używane przez UI, system Audio, Mutacje, Travel i Dialogi.
- `world.location` (w `WorldState`): Używane tylko w `GameBootstrapper.kt`.
- **Decyzja**: Unifikacja do `world.locationId` w Etapie 1.

### 3. Identyfikacja Przedmiotów (`item.id` usage)
System nie rozróżnia typu przedmiotu od jego unikalnej instancji.
- Wyszukiwanie przedmiotu w ekwipunku odbywa się prawie wyłącznie przez `.find { it.id == itemId }`.
- Ryzyko: Sprzedaż lub użycie niewłaściwego przedmiotu, gdy gracz posiada kilka sztuk tego samego typu.
- **Decyzja**: Rozdzielenie na `instanceId` i `templateId` w Etapie 1.

### 4. Magiczne Stringi (Protocol Strings)
Flow gry opiera się na parsowaniu stringów w celu określenia akcji po walce lub dialogu:
- `"FINALIZE:"`
- `"COMBAT_WIN:"`
- `"POJEDYNEK:"`
- **Decyzja**: Zastąpienie przez `PendingWorldAction` (Sealed Interface) w Etapie 2/3.

### 5. Równoległe Systemy (Concurrent Systems)
- **Ceny**: `TradingEngine` (CommoditySystem) posiada własną logikę cen niezależną od `EconomySystem`.
- **Reputacja**: `FactionReputationSystem` trzyma własną mapę `entries`, która nie jest synchronizowana z `GameState.reputation` ani nie jest zapisywana. `ReputationSystem` to trzeci, niezależny byt.
- **Decyzja**: Konsolidacja do jednego właściciela w Etapie 2 i 4.

### 6. Niekontrolowana Losowość
Użycie `Random.Default` i `.random()` w:
- `HeroPool.kt`, `LootSystem.kt`, `Travel.kt`, `MutationSystem.kt`, `CombatSystem.kt`.
- **Decyzja**: Wprowadzenie `RandomProvider` dla deterministycznych testów w Etapie 5/6.

### 7. Degeneracja Świata (Collapse Logic)
- `CollapseEngine.tick()` zwiększa progres collapse o stałą wartość przy każdym wywołaniu, co uzależnia tempo gry od liczby odświeżeń ekranu.
- Scenariusze (np. `BLOOD_RUIN`) zadają obrażenia na każdym "ticku", co może zabić drużynę podczas bezczynności w menu.
- **Decyzja**: Przejście na model oparty o zdarzenia (Event-driven) w Etapie 5.
