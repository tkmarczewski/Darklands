# RAPORT Z EMULATORA (Pixel 8) - BIEG 003 (ANTI-CHEAT / BACK LOCK)
**Data:** 13 lipca 2026
**Cel:** Eliminacja możliwości oszukiwania poprzez systemowy przycisk BACK.

---

## 1. Zastosowane Rozwiązania Techniczne
W pliku `MainActivity.kt` wdrożono mechanizm `BackHandler` zintegrowany ze stanem nawigacji:
- **Zasada:** Przycisk BACK jest całkowicie blokowany we wszystkich ekranach rozgrywki (Hub, Miasto, Walka, Dialogi).
- **Wyjątek:** Przycisk BACK działa wyłącznie w `MAIN_MENU`, umożliwiając standardowe wyjście z aplikacji.
- **Logika:** Każda próba użycia przycisku BACK w trakcie sesji jest przechwytywana i logowana jako: `[TRIBUNAL] Back action blocked. World stability must be maintained.`

## 2. Weryfikacja na Pixel 8
1.  **Instalacja:** Pomyślnie zainstalowano nową wersję z blokadą (RUN_003).
2.  **Test Operacyjny:**
    - Uruchomienie nowej gry -> Wejście do ekranu tworzenia postaci.
    - Wysłanie sygnału `keyevent 4` (System BACK).
    - **Wynik:** Aplikacja nie zareagowała. Ekran nie cofnął się do menu głównego.
3.  **Dowód Wizualny:** Zrzut ekranu po próbie "powrotu" zapisany jako `reports/pixel8_back_test.png`.

## 3. Status Ontologiczny
Mechanika non-linearna została zabezpieczona. Gracz nie może już "cofnąć czasu" po podjęciu błędnej decyzji w dialogu lub przegranej walce bez zresetowania całej pętli.

---
*Kolejny bieg (RUN_004) skupi się na optymalizacji czasu ładowania assetów.*
