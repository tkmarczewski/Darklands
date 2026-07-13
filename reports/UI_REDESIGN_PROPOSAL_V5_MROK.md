# PROPOZYCJA REDESIGNU UI V5: "MROCZNY MANUSKRYPT"
**Cel:** Eliminacja "taniości" UI. Pełna immersja, tekstury, głęboki mrok.

---

## 1. Co poszło nie tak w V4?
- Za dużo pustego, czarnego miejsca.
- "Sztuczne" ramy, które wyglądają jak naklejone.
- Brak tekstury "starego świata".

## 2. Zmiany w V5 (Radykalne):
1.  **Pełny Pergamin:** Całe tło pod UI to teraz wielka tekstura starego, brudnego papieru (`bg_world_map.png` z bardzo niską jasnością i wysokim kontrastem).
2.  **Panorama jako Ilustracja:** Widok regionu nie jest już prostokątem. Ma "poszarpane" krawędzie (Ink-Splatter mask) i wygląda jak rycina wklejona do księgi.
3.  **Brak Pasków, Same Runy:** Statystyki HP/Mana zostają przeniesione na ramy portretów w formie krwawiących run.
4.  **Ciężkie Żelazo:** Menu "Pióro" zostanie zastąpione przez ikonę wielkiej, kutej pieczęci Trybunału, która po kliknięciu "rozbija się" na opcje.

---

## 3. Plan Techniczny (Działanie natychmiastowe):
1.  **Stworzenie `GrimParchmentSurface`:** Komponent nakładający teksturę starego papieru na CAŁY ekran.
2.  **Mroczna Typografia:** Użycie customowych czcionek serifowych z efektem "rozlanego inkaustu" dla logów.
3.  **Integracja Assetów:** Wykorzystanie `bg_splash_main.png` jako tekstury bazowej dla głębi.

---
*Robimy tak, żeby UI wyglądało jak autentyczny, mroczny artefakt, a nie aplikacja na telefon.*
