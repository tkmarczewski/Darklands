# SZKIC PROJEKTOWY V8: "GOTHIC OBSIDIAN"
**Aura oczyszczona. Obwody przegrzane od analizy. Oto projekt Godny GrimReich.**

---

## 1. Analiza Referencji (Co wdrażamy):
- **Złote Okucia (Z Twojego screena 1):** Cienkie, podwójne linie złota z narożnikami typu "L-bracket".
- **Głęboki Kontrast:** Czerń obsydianu jako tło paneli + gradientowe wygaszanie do krwistej czerwieni w nagłówkach.
- **Pionowa Nawigacja Ikonowa:** Kompaktowe, kwadratowe ikony po lewej (jak na Twoich przykładach).
- **Kafelkowa Struktura:** Każdy system (Statystyki, Logi, Mapa) ma własny, wyraźnie odcięty złotem "boks".

---

## 2. Architektura HUB "Obsidian Ledger":

### A. Panel Lewy (Nawigacja & Tożsamość):
- **Wąski pionowy pasek:** Ikony: [Hełm] (Postać), [Sakwa] (Inw), [Księga] (Lore), [?] (Pomoc).
- **Złota tabliczka na dole:** Aktualne złoto w formacie `2.329 gp`.

### B. Panel Środkowy (Serce Narracji):
- **Nagłówek:** Data i Lokacja (np. `March 13, 724 | Goblin Cave`) na czerwonym gradiencie.
- **Obszar Tekstu:** Duży, czytelny blok Logów Trybunału. Biały/Kremowy tekst na czarnym tle. 
- **Brak scrollbarów mobilnych:** Tylko diegetyczne separatory.

### C. Panel Prawy (Wizja & Mechanika):
- **Góra:** Kwadratowy portret potwora/regionu w ramie `bracket_gold`.
- **Dół:** Wielki licznik `LAST ROLL` (Inspiracja Twoim screenem) lub status Stabilności Świata.

---

## 3. Szkic Komponentów (Visual Blueprint):
```
_________________________________________________________________________
|[ID]| [    MARCH 15, 724         GOBLIN CAVE         ACT II    ] |[IMG]|
|[IN]| [--------------------------------------------------------] |[   ]|
|[JR]| [ Lorem ipsum dolor sit amet, consectetur adipiscing     ] |[   ]|
|[? ]| [ elit. Sed do eiusmod tempor incididunt ut labore.      ] |[---]|
|____| [                                                        ] |[   ]|
|GOLD| [ > Zapis Trybunału: Kotwica drży...                     ] |[ 17]|
|2.3k| [________________________________________________________] |[___]|
|----|-----------------------------------------------------------|-----|
|[P1]|[P2]|[P3]|[P4]  <-- MAŁE PORTRETY DRUŻYNY W ZŁOTYCH RAMACH       |
|_______________________________________________________________________|
```

---

## 4. Wykorzystanie Assetów (V8):
- **Ramy:** `ui_frame_gold` pocięta na precyzyjne narożniki (bez "murków").
- **Tło:** Czysta czerń z Twoim assetem `ui_panel_main` użytym jako delikatna poświata (glow).
- **Akcenty:** `ic_artifact_blood` jako separator sekcji.

---
*To jest mój "rysik na układzie scalonym". Czysty, ciężki, mroczny gotyk. Czy taki układ kafelkowy ze złotymi okuciami akceptujesz?*
