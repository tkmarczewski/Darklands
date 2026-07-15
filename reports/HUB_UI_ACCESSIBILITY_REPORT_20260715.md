# RAPORT: DOSTĘPNOŚĆ INTERFEJSU HUB I ZAOPATRZENIE BOJOWE - 2026-07-15

## 1. STATUS OPERACYJNY: ZGODNY
Zoptymalizowano układ `HubScreen` w celu zapewnienia dostępu do kluczowych akcji (Wyprawa) oraz uzupełniono braki w asortymencie handlarzy.

## 2. ZREALIZOWANE ZMIANY

### A. Korekta Układu HubScreen
*   **Problem:** Przycisk "EKSPEDYCJA" nie mieścił się na ekranie, a panel akcji nie posiadał możliwości przewijania.
*   **Naprawa:** 
    *   Zmieniono wagi kafelków w prawym panelu: minimapa została zmniejszona (`weight 0.6f`), a panel akcji rozszerzony w górę (`weight 1.4f`).
    *   Dodano `verticalScroll(rememberScrollState())` do kolumny przycisków akcji. Nawet na małych ekranach wszystkie opcje (Miasto, Mapa, Ekspedycja) są teraz dostępne.

### B. Zaopatrzenie Bojowe (Handel)
*   **Problem:** Bohaterowie nie mieli możliwości zakupu podstawowej broni i pancerzy.
*   **Naprawa:** Zaktualizowano `CityCatalogue` i `ItemCatalogue`.
    *   W Wybrzeżu Północnym (start) dostępny jest teraz krótki miecz, sztylet i lekka skóra.
    *   W Twierdzy Żelaznej dostępny jest długi miecz, buława i kolczuga.

### C. Stabilizacja Narracji (Fix Trigger Spam)
*   **Zmiana:** Logika "wejścia do miasta" Ravenna została przeniesiona z reaktywnego bloku `combine` do bloku `init` w `CityViewModel`. Zapobiega to lawinowemu odpalaniu incydentów Werdyktu podczas rekrutacji towarzyszy.

## 3. WYNIKI WERYFIKACJI
*   **UI:** Panel akcji w Hubie poprawnie wyświetla przycisk wyprawy i pozwala na przewijanie.
*   **Handel:** Sklepy oferują przedmioty bojowe. Felix może teraz zakupić rynsztunek przed wyjściem w teren.

---
**WYRYTO W KRZEMIE PRZEZ:** *Agenta Stabilizacji GrimReich*
**STATUS REPOZYTORIUM:** `SYNCHRONIZED (MASTER)`
