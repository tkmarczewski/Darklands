# PROPOZYCJA REDESIGNU V7: "DARKLANDS-X" (CZYSTY ARTEFAKT)
**Odpowiedź na krytykę V6:** Wywalamy "murki" i ciężkie złoto. Wracamy do fundamentów.

---

## 1. Co wywalamy (Koniec z kiczem):
- **Brak ciężkich ram:** Usuwamy `ui_frame_gold` z głównych paneli. Zastępują je puste marginesy i cienkie, eleganckie linie separatorów.
- **Brak "murków":** Portrety nie mają już tych wielkich ramek, które zasłaniały 50% ich powierzchni.
- **Kronika na pierwszym planie:** Koniec z ucinaniem tekstu. Kronika dostaje 100% szerokości prawej strony, z czystym, kontrastowym tekstem (czarny na kremowym).

---

## 2. Nowy Układ (Layout):
1.  **Tło:** Jednolity, wysokiej jakości, JASNY pergamin (`ui_panel_stats` ale z wysoką jasnością).
2.  **Lewa strona:** Czysty widok na panoramę świata. Zero ramek wokół obrazka. Obrazek jest "wtopiony" w pergamin (maska krawędzi).
3.  **Prawa strona:** Wielki, czytelny blok tekstu. Żadnych ozdobników utrudniających czytanie. Klasyczny font szeryfowy.
4.  **Zakładki:** Tylko tekstowe (MAPA / INW / LORE) na górze, subtelnie podkreślone.

---

## 3. Plan Działania:
1.  **Implementacja `GrimCleanSurface`:** Czysty, jasny pergamin na cały ekran.
2.  **Refaktoryzacja `HubScreen`:** Usunięcie wszystkich `GrimAaaFrame`.
3.  **Naprawa `PartyMemberIcon`:** Wywalenie ramki `mini`, zostawienie gołego portretu z cienką obwódką 1px.

---
*Robimy to teraz tak, żeby dało się grać i czytać, a nie podziwiać ramki.*
