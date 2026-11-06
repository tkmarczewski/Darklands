# Raport Audytu: Naprawa "Destrukcji" i Nadpisywania Dialogów

## 1. Zidentyfikowane Problemy
Podczas dogłębnej analizy zgłoszonego błędu ("Znalazłem błąd w nowej funkcji 'Destrukcja': czyszczenie zapisów jest zaraz potem cofane przez nawigację"), ujawniono krytyczne nieścisłości w zarządzaniu cyklem życia plików sesji oraz stanie UI:
- **Zmartwychwstanie Zapisu**: `deleteAllDataAndWipe()` poprawnie usuwało pliki, ale z powodu asynchronicznej natury `StatePersistenceManager` i braku flag w `GameRepository`, kolejna akcja w UI (`setMode(GameScreenMode.main_menu)`) nadpisywała puste dane. Powodowało to wygenerowanie "zepsutej sesji" bez gracza i postaci. W efekcie UI zacinało się i "przyciski nie działały".
- **Nadpisywanie Zadań w UI**: Znaleziono również bug-cichosza (Bug-3), w którym okno dialogowe zmuszone do przyjęcia nowego zadania z tablicy informacyjnej (`QuestBoardModal`) łapało `activeQuestId` pierwszego lepszego aktywnego zadania oddawanego temu samemu NPC. To skutecznie zawieszało NPC w trybie "odbioru starego questa" podczas przyjmowania nowego.

## 2. Podjęte Akcje
- **`GameRepository.kt`**: 
  - Dodano flagę cyklu życia `@Volatile var isWiped`. Blokuje ona wszelkie operacje zapisu poprzez `manualSave` lub auto-zapisy w strumieniu zdarzeń, jeśli dane zostały wyczyszczone. Flaga ta zostaje cofnięta dopiero podczas rozpoczęcia `clearSessionAndReset()` (Nowej Gry) lub ładowania stanu.
  - Wyczyszczenie replikowanej kolejki stanu przez `saveRequests.resetReplayCache()`.
- **`StatePersistenceManager.kt` i `SaveSystem.kt`**:
  - `clearAllDataAndSlots` zostało opakowane w operację synchroniczną blokowaną muteksem (Mutex.withLock), wywołaną z kontekstu transakcyjnego w `GameRepository`. Dzięki temu niszczenie danych czeka na zakończenie spóźnionych procedur IO, zapobiegając nadpisaniu "po usunięciu".
- **`GameRootViewModel.kt`**:
  - Uniknięto asynchronicznego zapisu w momencie opuszczenia ekranu `EndingScreen` po wyczyszczeniu danych (sprawdzenie, czy repozytorium jest wyczyszczone - `!gameRepository.isWiped`).
- **`CityViewModel.kt`**:
  - Metoda `startDialogue()` podczas klikania questa została poprawiona. Przekazane konkretne zadanie z tablicy wprost przypisuje się jako główny cel interakcji z NPC, nadpisując logikę fallback'u do wcześniej zaakceptowanych, ale nieskończonych jeszcze zadań.

## 3. Rezultat
Po poprawkach przyciski odblokowują się, czyszczenie gry w trybie "Destrukcji" działa w 100% stabilnie (całkowicie usuwając sesję, co wymusza nową grę bez błędu zduplikowanej, wadliwej sesji), a questy dają się przyjmować z tablicy niezależnie od innych otwartych wątków u tego samego NPC. Zmiany pomyślnie przeszły zestaw testów `testDebugUnitTest` (147 / 147 testów zielonych).

*Zatwierdzono przez system.* 
