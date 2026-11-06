# Raport Audytu Kodu: Usunięcie Wyzerowania Zadań, Zabezpieczenie Cech i Synchronizacja Echa (Commit 5732cf0)

## Załatane Nowe Wykrycia Architektoniczne

### 1. [P1/P2] Zabezpieczenie przed Zerowaniem Postępu Aktywnego Zadania (`QuestEngine.kt`)
- **Problem:** Wywołanie `activateQuestDirect()` dla zadania znajdującego się już w zbiorze `activeQuestIds` generowało nowy obiekt `QuestProgress(questId, QuestStatus.active)` i nadpisywało istniejącą pozycję w mapie `progress`, co **resetowało wskaźnik wykonanego kroku (`currentStepIndex`) z powrotem do 0**.
- **Rozwiązanie:** W `QuestEngine.kt` dodano weryfikację: jeśli zadanie znajduje się już na liście aktywnych, aktywacja zostaje zignorowana (`return`), chroniąc dotychczasowy postęp gracza.

### 2. [P2] Ochrona Premii Dziedzictwa przy Zmianie Profesji i Losowaniu (`CharacterCreatorViewModel.kt`)
- **Problem:** Wymiana profesji (`selectCareer()`) lub losowanie cech (`randomizeAttributes()`) resetowało atrybuty do wartości domyślnych, wymazując przy tym odblokowaną wcześniej w metagrze premię dziedzictwa `SCRIBES_EYE` (+5 INT).
- **Rozwiązanie:** Zarówno `selectCareer()`, jak i `randomizeAttributes()` automatycznie sprawdzają obecność zwoju dziedzictwa w `persistentMeta` i ponownie nakładają premię +5 do inteligencji.

### 3. [P2] Egzekwowanie Wymagań Cech w Ekspedycjach (`ExpeditionViewModel.kt`)
- **Problem:** Wymagania cech spotkania (`requiredAttribute`) były sprawdzane wyłącznie przy obecności aktywnego bohatera. Gdy w drużynie braknęło żywych członków, kod pomijał walidację i natychmiast wykonywał efekt.
- **Rozwiązanie:** W `ExpeditionViewModel.kt` brak aktywnego bohatera przy wyborze wymagającym cechy zwraca komunikat błędu `"Brak aktywnego bohatera do wykonania tej akcji."` i przerywa wykonanie.

### 4. [P2] Wielowątkowość i Bezpieczeństwo Pamięci w Rejestrze Echa (`EchoSystem.kt`)
- **Problem:** Lista `eternalHeroes` była modyfikowana i odczytywana z różnych dyspozytorów (m.in. `Dispatchers.IO` w `loadEchoesAsync()` oraz wątku głównego w `recordHero()`) bez synchronizacji. Ponadto zapisywano bezpośrednie referencje do obiektów `Hero`, co przy ich późniejszej modyfikacji psuło spójność danych.
- **Rozwiązanie:** 
  1. Opakowano operacje na `eternalHeroes` w zsynchronizowany blok `synchronized(lock)`.
  2. W metody `recordHero()`, `save()`, `getRandomEcho()` i `linkToEcho()` wbudowano głębokie kopiowanie (`hero.deepCopy()`), izolując przechowywane w ehu obiekty od mutacji w runtime.

---

## Wyniki Suity Testowej
* **Wszystkie testy jednostkowe i integracyjne:** 149 passed, 0 skipped, 0 failed.
* **Git Master:** Commit `5732cf0` oraz niniejszy raport zostały wypchnięte do repozytorium.
* **Status:** Wszystkie zidentyfikowane ścieżki i krawędzie logiczne zostały uszczelnione.
