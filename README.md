# Grimreich

[![Android CI](https://github.com/tkmarczewski/Darklands/actions/workflows/android-ci.yml/badge.svg)](https://github.com/tkmarczewski/Darklands/actions/workflows/android-ci.yml)
[![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Grimreich 1.0 — mobilna gra RPG na Androida osadzona w mrocznym średniowiecznym świecie Grimreich.

## Opis

Grimreich to mobilna gra RPG, w której gracz wciela się w bohatera wędrującego przez krainę Grimreich. Musi on zmierzyć się z wyzwaniami wiary, reputacji, starzenia i walki, dążąc do jednego z czterech możliwych zakończeń: Oczyszczenia, Gorzkiego Zwycięstwa, Odkupienia lub Skażenia.

## Funkcje

- System postaci z karierami, starzeniem i atrybutami
- System walki z moraleSystemem i ranami (WoundType)
- System reputacji frakcji krainy Grimreich
- System religii i łaski
- Graf zadań (QuestGraph) z zależnościami
- System ekwipunku: broń, zbroja, przedmioty
- System zapisu gry z autozapisem i migracją wersji
- Cztery zakończenia: Oczyszczenie, Gorzkie Zwycięstwo, Odkupienie, Skażenie

## Budowanie i uruchamianie

Pełna instrukcja (wymagania, komendy `assembleDebug` / `test`, instalacja przez `adb`, częste problemy) znajduje się w [BUILDING.md](BUILDING.md).

## Technologie

- Kotlin (Android)
- Android SDK 34, minSdk 24
- ViewBinding
- Gradle 8.3

## Zależności (licencje)

| Biblioteka | Wersja | Licencja |
|---|---|---|
| androidx.core:core-ktx | 1.12.0 | Apache 2.0 |
| androidx.appcompat:appcompat | 1.6.1 | Apache 2.0 |
| com.google.android.material | 1.11.0 | Apache 2.0 |
| androidx.constraintlayout | 2.1.4 | Apache 2.0 |

Wszystkie zależności korzystają z licencji Apache 2.0, która jest zgodna z MIT.

## Licencja

Ten projekt jest objęty licencją **MIT** — szczegóły w pliku [LICENSE](LICENSE).

Copyright (c) 2026 tkmarczewski
