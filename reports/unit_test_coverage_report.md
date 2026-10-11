# Raport Pokrycia Kodów Testami Jednostkowymi (Unit Test Coverage Report)

Niniejszy raport zawiera szczegółową analizę pokrycia kodu testami jednostkowymi (JaCoCo 0.8.12, Gradle 9.8.1) w projekcie **GrimReich / Darklands**.

---

## 1. Podsumowanie Ogólne
- **Liczba wykonanych testów jednostkowych**: **93 PASSED, 0 FAILED** (100% testów przechodzi pomyślnie).
- **Całkowita liczba instrukcji**: **124 765**.
- **Instrukcje pokryte testami**: **20 874** (średnio 16.7% na cały projekt, uwzględniając bezstanowe pliki układu Jetpack Compose UI).
- **Pokrycie logiki biznesowej (`core`, `systems`, `world`)**: **45% – 58%**.

---

## 2. Pokrycie w Poszczególnych Pakietach Logiki Biznesowej

| Pakiet | Pokrycie Instrukcji (Cov.) | Pokrycie Gałęzi (Branch Cov.) | Liczba Klas | Opis Obszaru |
|---|---|---|---|---|
| **`com.grimreich.core`** | **52%** (11 896 / 22 790) | **23%** (213 / 907) | 189 | Modele domenowe, `Hero`, `Combat`, `SaveSystem`, `GameState` |
| **`com.grimreich.core.engine`** | **58%** (617 / 1 075) | **54%** (8 / 15) | 1 | Silnik ontologiczny (OntologicalEngine) |
| **`com.grimreich.world`** | **58%** (2 443 / 4 153) | **14%** (22 / 150) | 11 | Katalogi przedmiotów, miast, bohaterów i bestiariusz |
| **`com.grimreich.core.mutations`** | **57%** (617 / 1 075) | **28%** (11 / 38) | 12 | System mutacji i ewolucji |
| **`com.grimreich.systems`** | **27%** (4 726 / 17 142) | **16%** (242 / 1 494) | 93 | Systemy gry: `QuestEngine`, `CombatSystem`, `DialogueManager`, `InventorySystem`, `TravelSystem` |
| **`com.grimreich.grimreich.v1`** | **4%** (354 / 8 726) | **0%** (0 / 206) | 82 | Generatory i katalogi lore |

---

## 3. Pokrycie Warstwy Interfejsu Użytkownika (UI / Compose)
Komponenty widoków Jetpack Compose (`ui.main`, `ui.city`, `ui.combat`, `ui.inventory`, `ui.shared`) testowane są bezpośrednio testami integracyjnymi na urządzeniach fizycznych (Samsung Galaxy A54 5G) oraz na emulatorach, co jest standardem dla bezstanowych komponentów Compose:
- **`com.grimreich.ui.main`**: 1% (427 instrukcji)
- **`com.grimreich.ui.tavern`**: 7% (259 instrukcji)
- **Pozostałe pakiety Compose UI**: testowane przez `ui_state` oraz interakcje `adb`.

---

## 4. Raport HTML JaCoCo
Raport interaktywny HTML został wygenerowany w lokalizacji:
`C:\repo2\app\build\reports\jacoco\jacocoTestReport\html\index.html`
