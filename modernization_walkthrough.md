# Dokumentacja Modernizacji Grimreich (UI 2.0 & Czas)

## 1. Systemy Czasu Rzeczywistego
- **RealTimeEventManager:** Gra teraz śledzi czas rzeczywisty. Jeśli gracz wróci po ponad 24h, następuje pobranie żołdu (strata złota). Krótsze przerwy (8h+) regenerują HP drużyny.
- **Zdarzenia Kalendarzowe:** 
    - **Niedziela:** Zwiększony loot, silniejsi wrogowie.
    - **Piątek:** Skuteczniejsza modlitwa w Kaplicy.

## 2. Nowy Interfejs (Modern UI)
- **Eliminacja ramek:** Usunięto statyczne grafiki `ui_frame_gold`. Zastąpiono je dynamicznymi, półprzezroczystymi tłami systemowymi (#80000000), co zapewnia idealne skalowanie na każdym ekranie.
- **Pasek Drużyny:** Dodano portrety postaci (`ivCharPortrait`) obok imion i pasków życia.
- **Ujednolicone nazewnictwo:**
    - Hub -> **EKRAN GŁÓWNY**
    - Inv -> **PLECAK**
    - Święci -> **KAPLICA**
- **Czyste Nagłówki:** Usunięto zbędne słowa ("Lokacja", "morning"). Teraz: `Wybrzeże Północne | Dzień 1 | Poranek`.

## 3. Automatyzacja i Dynamika
- **Auto-save:** Przyciski Zapisz/Wczytaj zostały usunięte z HUBa. Gra zapisuje się automatycznie przy każdej istotnej zmianie (Resume w HUBie).
- **Zdarzenia Losowe:** Wprowadzono `RandomEventManager`. Przy wejściu do Miasta lub Hubu istnieje szansa na wystąpienie zdarzenia narracyjnego (np. kieszonkowiec, znalezisko).
- **Mapa:** Powiększono przyciski regionów i naprawiono zawijanie długich nazw (np. "Pogranicze Stepowe").

## 4. Weryfikacja
- Budowanie: SUKCES.
- Testy jednostkowe: 38/38 zaliczonych.
- Emulator: Potwierdzono stabilność i poprawność wyświetlania na nowych layoutach.
