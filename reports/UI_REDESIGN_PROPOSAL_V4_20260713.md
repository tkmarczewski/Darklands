# PROPOZYCJA REDESIGNU UI V4: "ŻYJĄCY REJESTR"
**Kluczowe zmiany:** Integracja Dziennika (Journal) jako fundamentu Hubu.

---

## 1. Dziennik jako Serce Interfejsu (Inspiracja: Civ Fable & Darklands)
Zamiast ukrywać dziennik w osobnym menu, w V4 staje się on **stałym elementem dolnej części Hubu**.

- **Układ "Split-Page":**
    - **Górne 60%:** "Sfera Widzenia" — panorama regionu, NPC, oraz diegetyczna minimapa (V3).
    - **Dolne 40%:** "Sfera Zapisu" — Dziennik. To scrollowany obszar na pergaminie, gdzie atramentem zapisywane są logi Trybunału oraz aktualne cele questów.
- **Interakcja:** Dziennik można "rozciągnąć" na cały ekran gestem pociągnięcia za zakładkę (bookmark), aby przeczytać historię całej sesji.

---

## 2. Minimalistyczna Nawigacja: "Zakładki i Pióro"
Zgodnie z prośbą o redukcję przycisków:
- **Expandable Quill:** Mała ikona pióra w rogu. Po kliknięciu "rozpryskuje" się na 4-5 małych ikon akcji (Ekwipunek, Mapa, Statystyki).
- **Zasada:** Żadnych prostokątnych buttonów. Używamy Twoich ikon `ic_item_*` i `bulawa.png` jako okrągłych, interaktywnych symboli.

---

## 3. Diegetyczna Minimapa (Update)
- Minimapa będzie osadzona w ramce przypominającej **kompas** lub **astrolabium**, umieszczona w górnym prawym rogu "Sfery Widzenia".

---

## 4. Nowy Układ Hubu (Mockup Wizualny):
```
[       STATYSTYKI / STABILNOŚĆ        ]
[--------------------------------------]
[                                      ]
[       PANORAMA ŚWIATA / NPC          ]  <- SFERA WIDZENIA
[          (MINIMAPA W ROGU)           ]
[                                      ]
[--------------------------------------]
[    CEL: Znajdź Ravenna w Opactwie    ]  <- DZIENNIK (ZAWSZE WIDOCZNY)
[ TRIBUNAL: Kotwica poruszyła się...   ]
[--------------------------------------]
[ PORTRETY DRUŻYNY | MENU ROZWIJALNE   ]
```

---

## 5. Wykorzystanie Assetów (V4):
- **Tło Dziennika:** `panel statystyk.png` (rozciągnięty jako 9-patch pergamin).
- **Separator:** `Ramka cienka.png` (jako linia oddzielająca świat od zapisu).
- **Nawigacja:** `panel boczny.png` (jako rozwijana "okładka" menu).

---
*Czy stała obecność dziennika na dole ekranu (jak w grach fabularnych) pasuje do Twojej wizji GrimReich?*
