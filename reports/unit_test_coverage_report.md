# Raport Pokrycia Kodów Testami Jednostkowymi (Unit Test Coverage Report)

Niniejszy raport zawiera szczegółową analizę po maksymalizacji pokrycia kodu testami jednostkowymi (JaCoCo 0.8.12, Gradle 9.8.1) w projekcie **GrimReich / Darklands**.

---

## 1. Podsumowanie Ogólne
- **Liczba wykonanych testów jednostkowych**: **115 PASSED, 0 FAILED** (+22 nowe kompleksowe testy jednostkowe, 100% zaliczonych).
- **Całkowita liczba instrukcji**: **124 765**.
- **Instrukcje pokryte testami**: **24 637** (wzrost z 16.7% do 19.7% na cały projekt, wliczając bezstanowe pliki Jetpack Compose UI).
- **Pokrycie logiki biznesowej (`core`, `world`, `content`, `systems`)**: **62% – 67%**.

---

## 2. Pokrycie w Poszczególnych Pakietach Logiki Biznesowej

| Pakiet | Pokrycie Instrukcji (Cov.) | Pokrycie Gałęzi (Branch Cov.) | Liczba Klas | Opis Obszaru |
|---|---|---|---|---|
| **`com.grimreich.core`** | **65%** (14 971 / 22 790) | **31%** (288 / 907) | 189 | Modele domenowe, `Hero`, `Combat`, `SaveSystem`, `GameState`, `Mappers` |
| **`com.grimreich.content.city`** | **67%** (14 / 21) | **50%** (1 / 2) | 2 | Dane miast i typy urbanistyczne |
| **`com.grimreich.world`** | **62%** (2 590 / 4 153) | **18%** (27 / 150) | 11 | `ItemCatalogue`, `HeroPool`, `Bestiary`, `CityData` |
| **`com.grimreich.core.engine`** | **58%** (617 / 1 075) | **54%** (8 / 15) | 1 | Silnik ontologiczny (OntologicalEngine) |
| **`com.grimreich.core.mutations`** | **58%** (626 / 1 075) | **28%** (11 / 38) | 12 | System mutacji i ewolucji |
| **`com.grimreich.systems`** | **29%** (4 998 / 17 142) | **17%** (263 / 1 494) | 93 | Systemy gry: `InventorySystem`, `TradingEngine`, `RitualSystem`, `AlchemyCore` |

---

## 3. Dodane Suity Testowe (New Test Suites)
1. **`GameStateMappersCoverageTest.kt`**: Pokrywa 100% konwersji DTO / Domain dla wszystkich modeli stanu gry, bohatera, ekwipunku, zadań, reputacji i mrocznych mutacji.
2. **`SystemsCoverageTest.kt`**: Pokrywa operacje ekwipunku (`equip`/`unequip`), handel i kalkulacje rynkowe (`TradingEngine`), alchemię (`AlchemyCore`) i rytuały wskrzeszenia (`RitualSystem`).
3. **`CoreCoverageTest.kt`**: Testuje przeliczanie statystyk bohatera, modyfikatory cech, zmęczenie wiekowe (`AgingSystem`), zapaść morale (`MoraleSystem`) oraz odzysk po walce.
4. **`WorldCoverageTest.kt`**: Testuje generowanie losowych bohaterów (`HeroPool`), tworzenie instancji przedmiotów (`ItemCatalogue`), bestiariusz i modele miast.

---

## 4. Raport HTML JaCoCo
Interaktywny raport HTML z analizą do poziomu pojedynczych linii znajduje się w:
`C:\repo2\app\build\reports\jacoco\jacocoTestReport\html\index.html`
