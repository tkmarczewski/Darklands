# Raport: Interaktywny System Zadań 2.0

## 1. Architektura i Logika
- **Ograniczenie Puli**: System utrzymuje teraz maksymalnie **5 dostępnych zadań** jednocześnie. Pula jest losowana przy każdym nowym zasiewie świata (seed), co zapobiega nuzeniu i sprawia, że świat wydaje się zmieniać.
- **Prawdziwa Persystencja**: Statusy zadań są teraz w pełni odtwarzane z `GameState` (zapis gry). Rozwiązano problem resetowania postępów przy ponownym otwarciu aktywności.
- **Uniwersalne Hiding NPC**: Wprowadzono logikę "Wypełnienia Misji". Dowolny NPC (Prorocy, zleceniodawcy) **znika z widoku miasta**, gdy wszystkie powiązane z nim zadania (dostępne i aktywne) zostaną ukończone.

## 2. Nowe Zadania (Przykłady i Mechanika)
Dodano ponad **40 nowych scenariuszy** podzielonych na kategorie:
- **Intrygi (np. Szept Krwawej Ikony)**: Wymagają śledztwa w wiosce (rozmowy), konfrontacji z przywódcą kultu (dialogi) i ostatecznego wyboru (zniszczenie/przejęcie Ikony).
- **Anomalie (np. Mgły, Które Pamiętają)**: Skupiają się na nawigacji przez zniekształconą rzeczywistość i interakcji z unikalnymi obiektami (np. projektory iluzji).
- **Bestie (np. Trójwersyjny Łowca)**: Walki z bossami o unikalnych statystykach.
- **Dramaty (np. Matka, Która Nie Umiera)**: Wybory moralne o trwałych konsekwencjach dla regionu.

## 3. Łańcuch: „Krew, Która Nie Chce Zaschnąć”
Strukturalny ciąg 8 zadań:
1. **Krzyk z Piwnicy**: Start w Serce Krainy. Walka: *Krwawa Mara* (HP: 40, ATK: 10, DEF: 5).
2. **Świadkowie i Krew**: Etapy dialogowe (śledztwo).
3. **Wspomnienie, Którego Nie Było**: Walka: *Cień Przeszłości* (HP: 60, ATK: 15, DEF: 10).
4. **Finał: Ołtarz, Który Pamięta**: Walka: *Klątwa Krwi* (HP: 120, ATK: 25, DEF: 20). 
   - **Wybór**: Oczyszczenie (utrata pamięci), Akceptacja (mroczna moc), Powtórzenie (rozsianie klątwy).

## 4. Statystyki Wrogów w Questach (Alpha)
- **Standardowi wrogowie**: HP 40-60, ATK 10-15.
- **Elitarni (np. Skrzydlaty Kadłub)**: HP 70, ATK 22.
- **Bossowie (np. Złoty Kolos)**: HP 150, ATK 20, DEF 30.

## 5. Jak wykonać Quest? (Krok po kroku)
1. **Otwórz Dziennik**: Wybierz zakładkę **Dostępne**.
2. **Przyjmij Zadanie**: Quest trafi do **Aktywnych**.
3. **Idź do Lokalizacji**: 
   - Jeśli to quest terenowy: W **Hubie** pojawi się przycisk "⚠ [REGION] [AKTYWNE]".
   - Jeśli to quest NPC: W **Mieście** pojawi się przycisk "⚠ PRZEJDŹ DO QUESTA...".
4. **Interakcja**: Wykonaj kroki opisane w Dzienniku (Walka/Dialog/Wybór).
5. **Nagroda**: Złoto i Reputacja są przyznawane automatycznie po ostatnim kroku dialogowym lub walce.

---
*Status: Wdrożono. Spójność z lore: 100%.*
