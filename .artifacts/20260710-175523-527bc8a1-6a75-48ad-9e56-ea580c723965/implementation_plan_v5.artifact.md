# Plan Implementacji - Etap 5: Dynamiczny Świat i Zaawansowana Walka

Celem Etapu 5 jest zwiększenie głębi mechanicznej gry oraz rozwiązanie problemów z brakiem losowości (shufflingu) w świecie.

## Proponowane Zmiany

### 1. System Inicjatywy (Combat)
Zastąpienie uproszczonej wymiany ciosów pełną kolejką tur.

#### [CombatState.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/CombatState.kt)
- Dodać `initiativeOrder: List<InitiativeSlot>`.
- Dodać `currentTurnIndex: Int`.
- `InitiativeSlot` będzie zawierać `combatantId` i typ (BOHATER/WRÓG).

#### [CombatSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/CombatSystem.kt)
- Obliczanie inicjatywy na początku walki: `agility * 2 + bonusy + random(0, 3)`.
- Sortowanie uczestników walki w jednej kolejce.
- Przesuwanie tury po każdej akcji.
- Wróg atakuje konkretnego bohatera wyznaczonego przez kolejkę (lub logikę aggro), a nie `random()`.

### 2. Losowanie Tablicy Zadań (Quest Board)
Naprawa problemu "pierwsze trzy się nie losują".

#### [QuestEngine.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/QuestEngine.kt)
- Wprowadzenie `seed` zależnego od `cityId` i `world.day`.
- Ograniczenie widocznych zadań na tablicy do podzbioru (np. max 3-5 zadań jednocześnie).
- Zadania będą się zmieniać co kilka dni świata.

### 3. Proceduralne Opisy i NPC
Poprawa wariancji miast.

#### [ProceduralNpcGenerator.kt](file:///C:/repo2/app/src/main/java/com/grimreich/world/ProceduralNpcGenerator.kt)
- Wprowadzenie "nastroju miasta" (Mood), który zmienia generowane role NPC.
- Losowe przydomki dla NPC (np. "Aldous Smętny" vs "Aldous Chciwy").

---

## Plan Weryfikacji

### Testy Automatyczne
- `CombatInitiativeTest`: Sprawdzenie czy kolejka tur jest poprawnie generowana i czy kolejność się zgadza ze statystykami.
- `QuestBoardShufflingTest`: Weryfikacja czy tablica zadań zmienia się w zależności od dnia świata.

### Manualna Weryfikacja
- Przejście kilku dni w grze i obserwacja zmian na tablicy ogłoszeń.
- Walka z bandytami i obserwacja paska tur (Initiative Bar).
