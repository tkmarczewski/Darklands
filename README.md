# GrimReich: Ontological RPG

GrimReich to mroczny system RPG typu survival-narrative, osadzony w świecie pękającej rzeczywistości. Projekt łączy klasyczne mechaniki RPG z unikalnym systemem **Reality Leak** (Wycieki Rzeczywistości) i głęboką fabułą ontologiczną opartą na 8 rozdziałach upadku świata.

## 🌌 Kluczowe Cechy (Faza 5: Final)

- **Ontologiczny Cykl Narracyjny**: Pełna historia Chapters I-VIII zaimplementowana w `DialogueManager`.
- **System Reality Leak**: Dynamiczne efekty glitchu (szum, drżenie obrazu, aberracja chromatyczna) narastające wraz ze spadkiem Stabilności Świata i Poczytalności bohatera.
- **7 Kanonicznych Regionów**: Każdy z unikalnym Prorokiem, fenomenem (Mgła, Krew, Odbicie itd.) i wysokiej jakości krajobrazem `bg_region_*`.
- **Kotwica Rzeczywistości**: Mechanika bohatera jako jedynego stabilnego bytu w sferze fenomenów.
- **System Atlasu**: Interaktywna mapa świata z travel confirmation i synchronizacją czasu nonlinearnego.

## 🛠 Architektura Techniczna

- **Platforma**: Android (Kotlin, Jetpack Compose / XML Views).
- **Core Engine**: `com.grimreich.core` – zarządzanie stanem (`GameState`), systemem zapisu i walką.
- **Narrative Engine**: `com.grimreich.systems.DialogueManager` – obsługuje ponad 100 węzłów dialogowych z dynamicznym glitchowaniem tekstu.
- **World System**: `com.grimreich.world.CityCatalogue` – centralny rejestr lore i assetów regionalnych.
- **Visuals**: `GlitchOverlayView` – niestandardowy widok renderujący błędy ontologiczne bezpośrednio na UI.

## 📜 Lore: 7 Proroków i Absolut

Gra prowadzi przez konfrontację z siedmioma ideologiami Proroków:
1. **Aelion (Wybrzeże)**: Pamięć i Zapomnienie.
2. **Xyrel (Równiny)**: Krew i Wojna.
3. **Mira (Serce)**: Prawda i Odbicia.
4. **Sereth (Ruiny)**: Świadomość Pełni.
5. **Ferrun (Góry)**: Materia i Głębia.
6. **Noctyros (Stepy)**: Pęknięcie i Pustka.
7. **Anomalia (Ziemie Dzikie)**: Czysty Chaos.

## 🚀 Instalacja i Budowanie

```powershell
# Sklonuj repozytorium
git clone https://github.com/tkmarczewski/Darklands.git

# Zbuduj projekt
./gradlew assembleDebug

# Uruchom testy (38 przypadków PASS)
./gradlew test
```

## 📂 Struktura Assetów

Wszystkie zasoby znajdują się w `app/src/main/res/drawable-nodpi/` dla zachowania ostrości:
- `bg_region_*`: Krajobrazy regionów.
- `port_*`: Portrety NPC/Bohaterów.
- `ui_frame_*`: Elementy interfejsu 3D (Złoto/Kamień).
- `ic_item_*`: Ikony ekwipunku.

---
**Status Projektu**: Technicznie stabilny, wizualnie kompletny, fabularnie domknięty.
*GrimReich - Świat jest snem fenomenów. Ty jesteś jego jedyną kotwicą.*
