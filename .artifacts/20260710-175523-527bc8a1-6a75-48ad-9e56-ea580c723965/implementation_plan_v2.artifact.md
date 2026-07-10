# Plan Implementacji - Etap 2: Model Domenowy i Centralne Ownership

Celem Etapu 2 jest uporządkowanie logiki biznesowej poprzez wprowadzenie centralnych systemów zarządzania stanem oraz naprawę błędów w silniku questów i pętli gry.

## Proponowane Zmiany

### 1. Centralizacja Mutacji Świata
Wprowadzenie `WorldStabilitySystem` jako jedynego miejsca mutacji parametrów świata.

#### [NEW] [WorldStabilitySystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/WorldStabilitySystem.kt)
- Centralne API: `changeStability(delta, reason)`, `changeEcho(delta, reason)`, `advanceCollapse(delta, reason)`.
- Logowanie zmian do `state.logEntries`.
- Zapewnienie atomowości zmian.

### 2. Refaktoryzacja QuestEngine
Naprawa błędów logicznych zidentyfikowanych w audycie.

#### [QuestEngine.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/QuestEngine.kt)
- **Usunięcie filtra `cityId`** w `getActiveQuestsForCity` - aktywne questy mają być widoczne globalnie.
- **Obsługa `minWorldDay`** w `getStatus` - blokowanie questów przed odpowiednim dniem.
- **Naprawa `failQuestDirect`** - usuwanie z `progress` lub dodawanie do `failedQuestIds`.
- **Idempotentność `completeQuestDirect`** - dodanie guarda przed podwójną nagrodą.

### 3. Poprawa Pętli Gry i Podróży

#### [GameLoopController.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/GameLoopController.kt)
- **Naprawa `travelToQuest`** - zmiana celu z `quest.cityId` na cel bieżącego kroku (`QuestStep.targetId`).
- **Reset Registry** - dodanie `questEngine.clearRegistry()` i `questManifest.seed()` do metody `bootstrap`.

---

## Plan Weryfikacji

### Testy Automatyczne
- `WorldStabilitySystemTest` (nowy): Weryfikacja clampingu i logowania.
- `QuestEngineTest` (istniejący): Testy `minWorldDay` i globalnej widoczności.

### Weryfikacja Manualna
- Sprawdzenie w UI miasta czy questy z `minWorldDay` pojawiają się dopiero we właściwym dniu.
- Sprawdzenie w ekspedycji czy questy z innego miasta są widoczne.
