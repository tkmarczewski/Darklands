# Dokumentacja Etapu 1 - GameState, Save i Invariants

## Podsumowanie Zmian
Etap 1 koncentruje się na ustanowieniu `GameState` jako jedynego, spójnego źródła prawdy oraz na zapewnieniu unikalności instancji przedmiotów.

### 1. Unifikacja Lokalizacji
- Pole `world.location` w `WorldState` zostało zmienione na `locationId`.
- Pole `grimCurrentRegion` w `GameState` zostało zamienione na delegat (getter/setter) operujący na `world.locationId`.
- Zaktualizowano `GameBootstrapper`, `SessionStateDto` oraz mappery.
- **Efekt**: Usunięto dualizm danych. Każda zmiana `grimCurrentRegion` jest natychmiast widoczna w `world.locationId`.

### 2. Tożsamość Przedmiotu (`instanceId`)
- Model `Item` w `GrimModels.kt` został rozdzielony na:
    - `instanceId: String`: Unikalny identyfikator danej sztuki (np. UUID).
    - `templateId: String`: Identyfikator wzorca/typu (np. "sword_basic").
- Zaktualizowano `ItemDto` oraz mappery.
- **UWAGA**: Zmiana ta powoduje liczne błędy kompilacji w miejscach używających `item.id`. Muszą one zostać naprawione w ramach Etapu 1 poprzez przejście na `instanceId` dla operacji na konkretnych przedmiotach.

### 3. Normalizacja Stanu
- Rozszerzono `GameState.normalizeState()` o wymuszanie zakresów (clamping):
    - `gold >= 0`
    - `world.day >= 1`
    - `world.globalStability` w zakresie `0..100`
    - `world.echoIntensity` w zakresie `0..1`
    - `world.collapseProgress` w zakresie `0..1`
    - Automatyczna naprawa `activeHeroId` (sprawdzenie czy bohater żyje i istnieje w drużynie).

---

## Co dalej do zrobienia (Etap 1 - Dokończenie)

1. **Naprawa kompilacji po migracji `item.id`**:
    - Przejście po wszystkich plikach (wykrytych w Etapie 0) i zamiana `it.id == itemId` na `it.instanceId == instanceId`.
    - Aktualizacja `ItemCatalogue` i generatorów łupu, aby nadawały unikalne `instanceId` przy tworzeniu przedmiotów.
2. **Refaktoryzacja Persystencji**:
    - Ujednolicenie serializerów w `StatePersistenceManager` (migracja z Gson na Kotlinx.serialization dla slotów).
    - Wprowadzenie `Mutex` zamiast `synchronized` w metodach `suspend`.
3. **Deep Copy Fix**:
    - Zapewnienie głębokiego kopiowania dla `grimEngine` (obecnie współdzielona referencja).
4. **Finalna weryfikacja Builda**:
    - Uruchomienie `./gradlew assembleDebug`.

Dopiero po zakończeniu tych punktów będzie można przejść do **Etapu 2** (Model domenowy i centralne ownership).
