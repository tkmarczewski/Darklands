# GrimReich: Oficjalna Dokumentacja Systemowa (V1.0-ALPHA)

GrimReich to mroczne RPG oparte na paradygmacie ontologicznej niestabilności. Niniejszy dokument opisuje architekturę systemów po 15 rundach stabilizacji.

---

## 🏗️ 1. Architektura Stanu (State Engine)

Centralnym punktem gry jest `GameState`, zarządzany przez `GameRepository`.

### 1.1 Atomowość i Concurrency
Wszystkie zmiany stanu muszą przechodzić przez `updateState { ... }`. 
- **Thread Safety**: Operacje są synchronizowane (`synchronized`), co zapobiega race conditions.
- **Normalizacja**: Każda zmiana kończy się wywołaniem `normalizeState()`, która klamruje statystyki bohaterów i czyści logi.

### 1.2 Głęboka Kopia (Deep Integrity)
Aplikacja korzysta z rygorystycznego systemu `deepCopy()`. Gwarantuje to, że UI oraz system zapisu operują na niezależnych migawkach danych, eliminując błędy typu "Shared Mutable State".

---

## 💾 2. System Persystencji (Persistence V3)

Silnik zapisu został zaprojektowany z myślą o maksymalnej odporności na awarie.

### 2.1 Multi-Slot Saving
Obsługiwane przez `SaveSystem` i `StatePersistenceManager`.
- **Format**: JSON (Kotlin Serialization dla sesji, Gson dla slotów).
- **Bezpieczeństwo**: Zastosowano bezpośrednie strumieniowanie (`FileOutputStream`) z wymuszonym `fsync()` (pobranie deskryptora pliku i synchronizacja fizyczna z nośnikiem).

### 2.2 Migracja DTO
Dane są mapowane przez warstwę DTO (`SessionStateDto`). Test `DtoIntegrityTest` (wbudowany w suite testową) potwierdza 100% integralności atrybutów bohaterów i świata.

---

## ⚔️ 3. Mechanika RPG i Walka

### 3.1 System Atrybutów
Bohaterowie opisani są przez 7 atrybutów pierwotnych oraz statystyki pochodne:
- **Poczytalność (Sanity)**: 0-100. Spada przy kontakcie z "Drugą Stroną".
- **Wiara (Divine Favor)**: Waluta dla zdolności sakralnych.
- **Wytrzymałość (Endurance)**: Konsumowana przez ataki fizyczne i alchemię.

### 3.2 Silnik Walki (CombatRound)
- **SkillResult**: Zdolności zwracają ustrukturyzowany wynik, co pozwala na precyzyjne logowanie efektów.
- **Determinizm**: Walka korzysta z `CombatRandomProvider`, co umożliwia pełne testowanie scenariuszy bitewnych.

---

## 👁️ 4. Projekt Cipher (Meta-Narracja)

Świat GrimReich reaguje na statystykę `globalStability`.

### 4.1 Glitch Layer
Gdy stabilność spada poniżej 40%, aktywują się anomalie:
- **Dialogi**: Tekst ulega korupcji (jitter tekstowy).
- **Zadania**: Nazwy celów mogą zostać podmienione na `UNKNOWN_ENTITY`.
- **Audio**: Muzyka zmienia tonację (Pitch Wobble).

### 4.2 System Zadań (QuestEngine)
Obsługuje łańcuchy zadań (`Quest Chains`). Zadania podrzędne są automatycznie blokowane (`LOCKED`), dopóki zadania nadrzędne nie zostaną ukończone.

---

## 🧪 5. Weryfikacja i Jakość

Projekt posiada zintegrowaną suitę testową (JUnit):
- `CombatRoundTest`: Logika bitewna.
- `TradingEngineTest`: Ekonomia i handlarze.
- `SaveSystemTest`: Integralność zapisów.
- `ContentValidationTest`: Spójność mapy świata i dialogów.

**Aktualny Status Testów**: 26/26 PASS.

---
*GrimReich Dev Team - 2026*
