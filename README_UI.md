# GrimReich UI — Layout Package

## Pliki

| Plik | Opis |
|---|---|
| `res/layout/activity_main_game.xml` | Hub główny: statusbar, log, party strip, 6 przycisków nawigacyjnych |
| `res/layout/strip_party.xml` | Pasek 4 postaci z HP i fenomenem (Aelion, Xyrel, Sereth, Vorn) |
| `res/layout/activity_travel.xml` | Atlas świata — lista 7 regionów GrimReich |
| `res/layout/activity_party.xml` | Ekran drużyny z RecyclerView |
| `res/layout/item_character_card.xml` | Kafelek postaci w RecyclerView |
| `res/layout/activity_combat.xml` | Ekran walki z akcjami fenomenów |
| `res/drawable/btn_nav_bg.xml` | Selector: ciemny przycisk z czerwonym obramowaniem |
| `res/drawable/btn_region_bg.xml` | Selector: przycisk regionu ze złotym obramowaniem |
| `res/drawable/ic_nav_placeholder.xml` | Placeholder — zamień na VectorDrawable |
| `res/values/colors.xml` | Paleta GrimReich (grimBg, grimMist, grimBlood, grimReflection...) |
| `res/values/styles.xml` | Style: GrimNavButton, GrimRegionButton, GrimCombatButton, GrimSmallButton |

## Paleta kolorów

- `grimBg` = `#0D0D0D` — tło główne
- `grimParchment` = `#D4C8A8` — tekst
- `grimGold` = `#B8860B` — akcenty złote, nagłówki
- `grimMist` = `#7B9CBF` — Fenomen Mgły
- `grimBlood` = `#CC0000` — Fenomen Krwi / HP
- `grimReflection` = `#9B59B6` — Fenomen Odbicia
- `grimAccent` = `#8B0000` — obramowania przycisków

## Regiony w Atlasie

1. Wybrzeże Północne — Mgła / Zakon Świtu
2. Serce Krainy — Krew / Pełnia
3. Równiny Odbicia — Odbicie / Cień
4. Ashfeld — Krew / Zakon
5. Dreadmoor — Mgła / Cień
6. Iron Hollow — Krew / Pełnia
7. Krawędź Próżni — Odbicie / Absolut

## Wdrożenie

1. Skopiuj pliki do odpowiednich katalogów projektu
2. W `AndroidManifest.xml` zmień `theme` na `@style/Theme.GrimReich`
3. Zastąp `ic_nav_placeholder` docelowymi VectorDrawable
4. Podmień `strip_party.xml` na RecyclerView gdy party będzie dynamiczne
