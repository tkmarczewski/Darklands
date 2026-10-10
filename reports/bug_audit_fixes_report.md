# Raport z Audytu i Naprawy Błędów (Bug Audit & Fixes Report)

Niniejszy raport zawiera wyniki szczegółowej analizy kodu źródłowego Kotlin w repozytorium **GrimReich / Darklands**, potwierdzenie istnienia zidentyfikowanych usterek oraz zestawienie wprowadzonych poprawek.

---

## 1. Potwierdzone i Naprawione Błędy w Kodzie (Confirmed & Fixed Bugs)

### 🔴 BUG #1: Niespójność Stanu Śmierci w `Hero.normalize()`
- **Plik**: `app/src/main/java/com/grimreich/core/Hero.kt`
- **Analiza**:
  Metoda `normalize()` ustawiała `isDead = true` gdy `hp <= 0`, ale nie resetowała flagi `isDead = false` gdy bohater został wyleczony/wskrzeszony i miał `hp > 0`.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**:
  ```kotlin
  if (hp <= 0) {
      isDead = true
  } else if (isDead && hp > 0) {
      isDead = false
  }
  ```

---

### 🔴 BUG #2: Zła Logika Warunku w `Combat.computeWound()`
- **Plik**: `app/src/main/java/com/grimreich/core/Combat.kt`
- **Analiza**:
  Warunek `hpPercent <= GameConstants.Combat.WOUND_THRESHOLD_SERIOUS || state.endurance < 5` używał alternatywy `||`. W efekcie bohater z niską wytrzymałością (`endurance < 5`) otrzymywał poważną ranę (`WoundType.serious`) nawet przy 100% zdrowia.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Zmieniono pojedynczy warunek `||` na spójne sprawdzanie progów HP oraz progów połączonych `&&`.

---

### 🔴 BUG #3: Brak Ograniczenia Dolnego Morale przy Regeneracji w `Combat.kt`
- **Plik**: `app/src/main/java/com/grimreich/core/Combat.kt`
- **Analiza**:
  Wyliczanie morale przy regeneracji używało tylko `.coerceAtMost(MAX_MORALE)`. Przy ujemnym przyroście (`regen < 0`) morale mogło spaść poniżej 0.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Zastosowano `.coerceIn(0, GameConstants.Combat.MAX_MORALE)`.

---

### 🔴 BUG #4: Używanie Przedmiotów na Martwych Postaciach w `InventorySystem.useItem()`
- **Plik**: `app/src/main/java/com/grimreich/systems/InventorySystem.kt`
- **Analiza**:
  Metoda `useItem` nie weryfikowała, czy aktywny bohater żyje. Martwy bohater mógł konsumować mikstury lecznicze z ekwipunku.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**:
  ```kotlin
  if (targetHero.isDead || targetHero.hp <= 0) {
      result = "Nie można użyć na martwym bohaterze!"
      return@updateState
  }
  ```

---

### 🔴 BUG #5: Brak Walidacji Istnienia Przedmiotu w `InventorySystem.transferItem()`
- **Plik**: `app/src/main/java/com/grimreich/systems/InventorySystem.kt`
- **Analiza**:
  Przekazywanie przedmiotu nie sprawdzało czy podany `instanceId` w ogóle istnieje w ekwipunku grupy przed podjęciem operacji transferu.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Dodano sprawdzanie obecności przedmiotu `state.inventory.find { it.instanceId == instanceId }`.

---

### 🔴 BUG #6: Brak Koercji Reputacji w `DialogueManager.grant_reputation`
- **Plik**: `app/src/main/java/com/grimreich/systems/DialogueManager.kt`
- **Analiza**:
  Akcja dialogowa `grant_reputation` dodawała reputację bezpośrednio do mapy bez weryfikacji granic `[-100, 100]`.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Dodano ograniczenie `.coerceIn(-100, 100)`.

---

### 🔴 BUG #7: Pomijanie `worldStatusEffects` w `CombatSystem.heroToCombatant()`
- **Plik**: `app/src/main/java/com/grimreich/systems/CombatSystem.kt`
- **Analiza**:
  Podczas inicjalizacji walki konwertowano tylko `activeStatusEffects` bohatera, całkowicie pomijając `worldStatusEffects` (efekty środowiskowe zdobyte poza walką, np. zatrucie z eksploracji).
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Połączono efekty: `(hero.activeStatusEffects + hero.worldStatusEffects)`.

---

### 🔴 BUG #8: Pomijanie Odblokowań w `MetaObservationSystem.onQuestCompleted()`
- **Plik**: `app/src/main/java/com/grimreich/systems/MetaObservationSystem.kt`
- **Analiza**:
  Użycie konstrukcji `when` powodowało, że po przekroczeniu progu (np. 9 zadań) wykonywana była tylko pierwsza pasująca gałąź, pomijając kolejne progowe wariancje meta-świadomości.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Zmieniono blok `when` na niezależne warunki `if`.

---

### 🔴 BUG #9: Kolejność Normalizacji w `RitualSystem.performResurrection()`
- **Plik**: `app/src/main/java/com/grimreich/systems/RitualSystem.kt`
- **Analiza**:
  Wskrzeszenie wywoływało `normalize()` przed ustawieniem `hp = 1`. W rezultacie `normalize()` widziało `hp = 0` i ponownie ustawiało `isDead = true`, pozostawiając wskrzeszonego bohatera z flagą `isDead = true`.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Ustawiono `hp = 1` przed wywołaniem `normalize()`.

---

### 🔴 BUG #10: Ryzyko Przepływu/Przekroczenia Zakresu Zakupu w `CommoditySystem.buyGood()`
- **Plik**: `app/src/main/java/com/grimreich/core/CommoditySystem.kt`
- **Analiza**:
  Obliczenie `totalCost` typu `Long` mogło przekroczyć zakres `Int.MAX_VALUE` i przy konwersji do `Int` wywołać nieprzewidziane operacje na złocie.
- **Status**: **POTWIERDZONY i NAPRAWIONY**.
- **Fix**: Dodano sprawdzanie warunku `totalCost > Int.MAX_VALUE`.

---

## 2. Podsumowanie Testów Automatycznych
Po wprowadzeniu wszystkich poprawek uruchomiono pełną suitę testową Gradle:
- **Komenda**: `app:testDebugUnitTest`
- **Wynik**: **93 PASSED, 0 FAILED** (100% testów zakończonych sukcesem).
- **Kompilacja**: `app:assembleDebug` zakończona powodzeniem.
