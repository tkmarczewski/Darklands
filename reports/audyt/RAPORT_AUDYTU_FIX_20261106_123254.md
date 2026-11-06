# Raport Audytu Kodu: Mapowanie Frakcji, Łatki Handlowe i Złoto (Commit 4ba5ed6)

## Wykaz Odnalezionych i Załatanych Wycieków Logicznych

### 1. Naprawa Cichego Błędu Reputacji Frakcji (`FactionSystem.kt`)
- **Problem:** `FactionSystem.mapToFactionId()` zwracało identyfikatory pisane wielkimi literami (np. `"CHURCH"`, `"PEASANTS"`). Tymczasem słownik `FactionCatalogue.factions` rejestruje identyfikatory w małych literach (`"church"`, `"peasants"`). W rezultacie wywołanie `modifyReputation()` po cichu rzucało komendę `"Nieznana frakcja: CHURCH"` i porzucało zmianę reputacji.
- **Rozwiązanie:** Zmodyfikowano `mapToFactionId()` na właściwe małe litery, dzięki czemu zmiana reputacji u frakcji zapisuje się bezbłędnie.

### 2. Zabezpieczenie Slota Ekwipunku i Złota w Silniku Handlu (`TradingEngine.kt`)
- **Problem:**
  1. Wywołanie `sellItem()` przy sprzedaży wyposażonego przedmiotu używało `hero.equipment.entries.removeIf { it.value == itemId }`, co fizycznie wycinało klucz slota (np. `"weapon"`) z mapy ekwipunku bohatera zamiast przypisać `null`.
  2. Złoto ze sprzedaży było dodawane bezpośrednio (`state.gold += sellPrice`), co mogło wywołać przepełnienie `Int.MAX_VALUE`.
- **Rozwiązanie:**
  1. Odpinanie sprzętu ustawia teraz wartość slota na `null` (`hero.equipment[slot] = null`), utrzymując strukturę kluczy.
  2. Saldo złota jest ubezpieczone konwersją do typu `Long` i ograniczeniem do `Int.MAX_VALUE`.

### 3. Zabezpieczenie Złota z Tabel Łupów (`LootSystem.kt`)
- **Problem:** `awardLootFromTableDirect()` dodawało złoto do stanu gracza bez sprawdzania przepełnienia.
- **Rozwiązanie:** Dodano ubezpieczenie stanu złota `(state.gold.toLong() + gold.toLong()).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()`.

---

## Wyniki Suity Testowej
* **Testy:** 149 passed, 0 skipped, 0 failed.
* **Git Master:** Commit `4ba5ed6` opublikowany pomyślnie.
* **Status:** Wszystkie kluczowe ścieżki spójności danych są zablokowane i stabilne.
