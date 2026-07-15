# RAPORT ANALIZY FUNKCJONALNOŚCI GRIMREICH - 2026-07-15

## 1. Cel Analizy
Celem niniejszego raportu jest zidentyfikowanie i zaproponowanie innowacyjnych funkcjonalności, które wzbogacą rozgrywkę w systemie GrimReich, zwiększą immersję w mrocznym świecie (grimdark) oraz wyróżnią projekt na tle konkurencyjnych tytułów RPG.

## 2. Analiza Trendów (Research 2024/2025)
Na podstawie przeglądu nowoczesnych gier RPG z gatunku dark medieval (m.in. *Darkest Dungeon II*, *Elden Ring*, *Pathologic 2*), zidentyfikowano następujące trendy:
*   **Emergent Storytelling:** Mechaniki, w których historia buduje się sama poprzez decyzje gracza i ich nieprzewidywalne skutki.
*   **Psychological Trauma Systems:** Przeniesienie ciężaru z HP (zdrowie fizyczne) na zdrowie psychiczne i "blizny duszy".
*   **Collective World Shaping:** Systemy, w których decyzje całej społeczności graczy wpływają na stan świata (globalna zmiana paradygmatu).

## 3. Rekomendowane Funkcjonalności dla GrimReich

### A. System Traumy i Blizn Ontologicznych
*   **Opis:** Każdy kontakt z "Echem" (np. walka z elitarnymi przeciwnikami) zostawia trwały ślad w kodzie duszy bohatera.
*   **Mechanika:** Zamiast prostego uleczenia, bohater może zyskać "Bliznę", która daje bonus (np. +10% do Magii Echa), ale obniża Stabilność (NPC reagują strachem).
*   **Uzasadnienie:** Buduje poczucie, że każda decyzja i walka ma długofalowe, nieodwracalne skutki.

### B. System "Kolektywnego Wyroku" (Globalna Stabilność)
*   **Opis:** Wybory wszystkich graczy w kluczowych zadaniach (np. `q_village_judgment`) są sumowane na serwerze.
*   **Mechanika:** Jeśli większość graczy podejmuje "mroczne" decyzje, ogólny poziom "Echo Intensity" w grze wzrasta dla wszystkich, co zmienia typy pojawiających się potworów.
*   **Uzasadnienie:** Tworzy żyjący, reagujący świat i buduje więź między graczami bez bezpośredniego trybu multiplayer.

### C. Rytualny Crafting (Alchemia Krwi)
*   **Opis:** Zastąpienie standardowego okna craftingu interaktywnym rytuałem.
*   **Mechanika:** Gracz musi wybrać odpowiedni "Szyfr" (sekwencję symboli) i złożyć ofiarę (np. HP lub konkretny przedmiot). Błąd w rytuale może przywołać przeciwnika zamiast stworzyć przedmiot.
*   **Uzasadnienie:** Zwiększa immersję i sprawia, że tworzenie przedmiotów jest ekscytującym wyzwaniem, a nie nudną formalnością.

## 4. Wnioski i Następne Kroki
Najbardziej adekwatną i technicznie dostępną funkcjonalnością do wdrożenia w pierwszej kolejności jest **System Traumy**. Wymaga on rozszerzenia klasy `Character` o listę traum oraz modyfikacji logiki walki.

---
*Raport opracowany przez Agenta Stabilizacji GrimReich na podstawie analizy trendów RPG.*
