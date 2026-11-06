# Raport Audytu Kodu: Naprawa Zadania "Żniwa Mgły" i Interfejsu Użytkownika (Commit de1a423)

## Wykaz Zidentyfikowanych i Załatanych Błędów

### 1. Naprawa Blokady Zadania "Żniwa Mgły" (`q_coast_harvest`)
- **Problem:** Po przyjęciu zadania u Kupca (`merchant`), status zadania przechodził w `active`. W dialogu kupca brakowało opcji przejścia do zbierania ziół przy statusie `active` (dostępne były tylko opcje dla statusu `available` oraz `objective_met`). Dodatkowo w trybie ekspedycji dla kroków typu `StepType.investigation` brakowało otwierania dedykowanego węzła dialogowego (`harvest_herbs_coast`), przez co zadanie utykało w martwym punkcie.
- **Rozwiązanie:**
  1. W `dialogues_pilot.json` dodano opcję w dialogu Kupca przy aktywnym zadaniu: `"Szukam ziół echa w mgle (Żniwa Mgły)."` prowadzącą do węzła `harvest_herbs_coast`.
  2. W `ExpeditionViewModel.kt` zaimplementowano obsługę węzłów dialogowych dla kroków `StepType.investigation`, umożliwiając kompletne zebranie ziół również podczas ekspedycji.
  3. Przetestowano na urządzeniu: zadanie zostało pomyślnie wykonane, rozliczone u Kupca i nagrodzone.

### 2. Układ Przycisków i Przewijanie Bocznego Paska (`CityScreen.kt`, `MarketScreen.kt`, `TempleScreen.kt`, `TavernScreen.kt`)
- **Problem:** W prawych kolumnach miast, rynków, świątyń i tawern przyciski akcji oraz wyjścia ("WYJDŹ Z MIASTA", "WRÓĆ DO MIASTA", "WYJDŹ Z KAPLICY") znajdowały się w sztywnych kafelkach `.weight()`. Na urządzeniach o niższych proporcjach ekranu przyciski na dole były obcinane i niewidoczne.
- **Rozwiązanie:** Dodano `verticalScroll(rememberScrollState())` na poziomie prawej kolumny w `CityScreen`, `MarketScreen`, `TempleScreen` oraz `TavernScreen`, usuwając sztywne wagi wysokości. Wszystkie przyciski nawigacyjne i wyjścia są teraz w 100% dostępne.

### 3. Rozmieszczenie i Przewijanie Postaci NPC (`CityScreen.kt`)
- **Problem:** Wektor mieszkańców `OBECNI NPC:` używał sztywnego `Row` bez przewijania. Przy większej liczbie NPC-ów (np. 3-5 postaci) przyciski nakładały się na siebie lub wychodziły poza krawędź ekranu.
- **Rozwiązanie:** Zastąpiono układ kontenerem `Row(modifier = Modifier.horizontalScroll(rememberScrollState()))`, tworząc płynnie przewijany pasek postaci miejskich.

---

## Wyniki Suity Testowej i Weryfikacji
* **Testy:** **149 passed, 0 skipped, 0 failed**.
* **Test na fizycznym telefonie:** Zadanie "Żniwa Mgły" ukończone end-to-end, przyciski wyjścia widoczne, pasek NPC przewijalny, wygrana kampanii osiągnięta.
* **Git Master:** Commit `de1a423` wypchnięty do repozytorium.
