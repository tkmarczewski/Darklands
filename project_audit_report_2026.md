# Raport z Audytu Projektu GrimReich - Czerwiec 2026

## 1. Status Ogólny
Projekt przeszedł znaczącą modernizację (wersja 2.0). Architektura opiera się na Jetpack Compose z Single Activity (`MainActivity`). Systemy gry są zmodularyzowane w `com.grimreich.systems`, a stan zarządzany przez `GameRepository`.

## 2. Co działa (Kluczowe funkcjonalności)
- **CI/CD**: Potok GitHub Actions jest skonfigurowany i działa (testy, linter, build APK).
- **Testy Jednostkowe**: Podstawowe UTS dla logiki pór roku i silnika ontologicznego.
- **Nawigacja**: Pełny `GameNavHost` obsługujący ekrany: Menu, Kreator, Hub, Miasto, Walka, Dziennik, Ekwipunek.
- **Systemy Core**: Silnik ontologiczny, system mutacji, zarządzanie drużyną i zasobami (złoto, HP).
- **Zapisy**: `StatePersistenceManager` z obsługą serializacji do JSON.

## 3. Zidentyfikowane Braki (Co nie działa / Czego brakuje)

### A. Braki w Logice Gry (Gameplay Gaps)
1. **Ewolucja Mutacji**: Mamy `MutationRegistry`, ale system nie obsługuje "Tiers" (Dormant -> Manifested). Mutacje są nadawane, ale brak mechaniki ich pogłębiania.
2. **System Eksedycji (OtherSide)**: Ekran `ExpeditionScreen` istnieje, ale integracja z `GrimWorldEngine` i systemem warstw rzeczywistości jest szczątkowa.
3. **Zbalansowanie Walki**: Walka jest bardzo uproszczona. Brak wpływu statystyk (np. `Perception`, `Charisma`) na przebieg starcia poza podstawowym atakiem.
4. **Interakcje w Mieście**: Ekran świątyni (`SaintsScreen`) i rynku (`MarketScreen`) są obecne, ale brakuje w nich głębi interakcji (np. ofiary dla świętych wpływające na stabilność świata).

### B. Braki Techniczne (Technical Debt)
1. **Złożoność Metod (Detekt)**: Linter zgłasza błędy w `Combat.kt`, `GrimBuilders.kt`, `DialogueManager.kt` (zbyt długie funkcje, za dużo parametrów).
2. **Wildcard Imports**: Wiele plików UI używa `import .*`, co jest niezgodne ze stylem projektu.
3. **Magic Numbers**: W plikach UI i generatorach (np. `HeroPool.kt`) twardo wpisane wartości liczbowe zamiast stałych.
4. **Brak UTS dla Systemów**: Brak testów dla `MutationSystem`, `InventorySystem` oraz `QuestSystem`.

## 4. Rekomendacje (Kolejne Kroki)
- [ ] **Refaktoryzacja `Combat.kt`**: Rozbicie `resolveRound` na mniejsze funkcje.
- [ ] **Rozbudowa UTS**: Dodanie testów dla `MutationSystem` (weryfikacja szans na mutację).
- [ ] **Naprawa stałych**: Przeniesienie magicznych liczb do `GameConstants.kt`.
- [ ] **Integracja Świątyni**: Połączenie systemu `SaintCatalogue` z UI, aby modlitwy miały realny wpływ na stan gry.

---
*GrimReich: Fundamenty są solidne, czas na szlifowanie mechanik.*
