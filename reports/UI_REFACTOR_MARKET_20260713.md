# Raport Wdrażania UI V9: MarketScreen Refactor
**Data:** 2026-07-13

## Zmiany w Szyfrze:
1.  **MarketViewModel.kt**: Rozszerzono MarketUiState o listę drużyny (party), umożliwiając renderowanie dolnego paska w standardzie V9.
2.  **MarketScreen.kt**: Całkowita przebudowa na model 3-kafelkowy:
    - **Lewy Kafel**: Twoje Zapasy (Ekwipunek gracza). Pozwala na szybką sprzedaż przedmiotów.
    - **Środkowy Kafel**: Oferta Handlarza. Czytelna lista towarów z nowym systemem MarketItemRowV9.
    - **Prawy Kafel**: Dziennik transakcji i nawigacja powrotna.
    - **Dół**: Pasek drużyny z portretami i HP, zachowujący ontologiczną ciągłość "Command Center".

## Status Techniczny:
- Ekran Rynku jest teraz wizualnie identyczny z Hubem i Miastem pod względem ram, kolorystyki i typografii.
- Poprawiono czytelność cen i stanów posiadania złota.
