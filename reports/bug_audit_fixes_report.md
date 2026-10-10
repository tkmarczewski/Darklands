# Raport z Audytu i Naprawy Błędów (Bug Audit & Fixes Report)

Niniejszy raport zawiera wyniki szczegółowej analizy kodu źródłowego Kotlin w repozytorium **GrimReich / Darklands**, potwierdzenie istnienia zidentyfikowanych usterek oraz zestawienie wprowadzonych poprawek.

---

## 1. Potwierdzone i Naprawione Błędy w Kodzie (Confirmed & Fixed Bugs)

### 🔴 BUG #1: Niespójność Stanu Śmierci w `Hero.normalize()`
- **Plik**: `app/src/main/java/com/grimreich/core/Hero.kt`
- **Analiza**:
  Przed poprawką metoda `normalize()` ustawiała `isDead = true` gdy `hp <= 0`, ale nie resetowała flagi `isDead = false` gdy bohater został wyleczony/wskrzeszony i miał `hp > 0`.
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

## 2. Podsumowanie Testów Automatycznych
Po wprowadzeniu poprawek uruchomiono pełną suitę testową Gradle:
- **Komenda**: `app:testDebugUnitTest`
- **Wynik**: **93 PASSED, 0 FAILED** (100% testów zakończonych sukcesem).
- **Kompilacja**: `app:assembleDebug` zakończona powodzeniem.
