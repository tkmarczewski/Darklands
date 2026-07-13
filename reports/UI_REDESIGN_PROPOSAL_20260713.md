# PROPOZYCJA REDESIGNU UI: "KODEKS ONTOLOGICZNY"
**Inspiracje:** *Darklands*, *Civ Fable*, *Pentiment*, *Battle Brothers*, *Stoneshard*.

---

## 1. Filozofia Wizualna: Interfejs jako Artefakt
Zamiast standardowego "płaskiego" UI mobilnego, nowy interfejs GrimReich będzie udawał **fizyczny obiekt wewnątrz świata gry** — Ledger (Rejestr), który "pęka" wraz z rzeczywistością.

### Kluczowe Elementy:
1.  **Materialność (Battle Brothers):** Ramki nie są liniami, lecz teksturami kutego żelaza, kamienia i starego drewna (mamy assety: `Ramka cienka.png`, `ramka portretu.png`).
2.  **Narracja Typograficzna (Pentiment):** Różne fonty dla różnych stanów stabilności. Przy niskiej stabilności tekst "wycieka" poza marginesy, a litery zmieniają się w runy.
3.  **Układ Stronnicowy (Civ Fable):** Gra nie ma "okien", ma "strony". Przejścia między ekranami to animacja przewracanego pergaminu.
4.  **Diegetyczny HUD (Stoneshard):** Statystyki (HP, Mana) są zintegrowane z ramkami portretów jako naczynia wypełnione płynem, a nie paski postępu.

---

## 2. Nowa Architektura Ekranów (Layout)

### A. Ekran Główny (The Ledger)
- **Centrum:** Wielka karta pergaminu (`Las cieni - landscape.png` jako podkład narracyjny).
- **Lewa Krawędź:** Pionowy panel "zakładek" wykonany z żelaza (`panel boczny.png`), pozwalający na szybki skok do Mapy, Ekwipunku lub Kroniki.
- **Dół:** "Party Strip" — portrety bohaterów osadzone w ciężkich, kamiennych ramkach (`ramka portretu mini.png`).

### B. System Dialogowy (Storybook Mode)
- **Inspiracja Civ Fable:** Tekst dialogu pojawia się po lewej stronie, ilustracja regionu/NPC po prawej (lub jako tło z efektem sepia).
- **Interakcja:** Wybory to nie przyciski, lecz odręczne dopiski na marginesie ("Glosy").

### C. Walka (The Grimoire)
- Tło walki to `panel magii.png` zmiksowany z ilustracją potwora.
- Akcje są reprezentowane jako karty lub pieczęcie na stole dowódcy.

---

## 3. Plan Implementacji w Compose

### Krok 1: Nowe Komponenty Bazowe
- `GrimFrame`: Komponent nakładający tekstury `ui_frame_*` na kontenery.
- `OntologicalText`: Tekst, który automatycznie aplikuje efekty glitchu zależnie od `globalStability`.
- `ParchmentSurface`: Tło z teksturą starego papieru reagujące na dotyk (efekt zagnieceń).

### Krok 2: Refaktoryzacja `GameNavHost`
- Wprowadzenie animacji `PageTurn` przy zmianie `GameRoute`.

### Krok 3: Wykorzystanie Assetów
- `ic_item_*` zostaną osadzone na "szorstkich" tłach inwentarza.
- `bg_region_*` staną się pełnoekranowymi, klimatycznymi podkładami pod menu (zamiast czarnego tła).

---

## 4. Przykładowy Mockup (Logika):
```kotlin
@Composable
fun GrimLedgerPage(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.drawable.bg_parchment), contentDescription = null)
        Box(modifier = Modifier.padding(24.dp)) {
            content()
        }
        Image(painter = painterResource(R.drawable.ui_frame_iron), modifier = Modifier.matchParentSize())
    }
}
```

---
*Czy akceptujesz ten kierunek estetyczny (Brudny, Materiałowy Retro-RPG)? Jeśli tak, przystąpię do tworzenia pierwszego "żywego" prototypu na Pixel 8.*
