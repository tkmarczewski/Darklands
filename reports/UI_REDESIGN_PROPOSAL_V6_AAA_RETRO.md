# PROPOZYCJA REDESIGNU UI V6: "GRYMUAR KRÓLEWSKI" (AAA RETRO)
**Inspiracje:** *Diablo II Resurrected*, *Pillars of Eternity*, *Baldur's Gate 3* (Inventory), *Darklands*.

---

## 1. Filozofia "AAA Retro"
Koniec z płaskimi kolorami i prostymi gradientami. Każdy element musi mieć **teksturę, cień i ciężar**.

### Kluczowe Zmiany Wizualne:
1.  **Luksusowy Pergamin:** Tło nie jest czarne. To gruby, fakturowany pergamin (`panel statystyk.png` z nałożonym ziarnem), który ma wyraźne, zniszczone krawędzie.
2.  **Rzeźbione Złoto i Kamień:** Wykorzystanie `ui_frame_gold.png` nie jako całej ramki, ale jako elementów konstrukcyjnych (narożniki, zdobione nity, okucia).
3.  **Głębia i Cień:** Każdy tekst ma delikatny "ink-shadow", a ilustracje regionów są osadzone w głębokich, kamiennych wnękach.
4.  **Diegetyczne Zakładki:** Zamiast menu "Pióra", wprowadzamy **fizyczne zakładki (tabs)** wystające z boku Ledgeru, wykonane ze skóry i metalu.

---

## 2. Układ "Otwarty Grymuar" (2-Page Spread)
Dla ekranów panoramicznych (Pixel 8) interfejs udaje rozłożoną księgę:
- **Lewa Strona:** "Sfera Obserwacji" (Panorama regionu, Minimapa, Dynamiczny status świata).
- **Prawa Strona:** "Sfera Zapisu" (Dziennik, Logi Trybunału, Aktywne Questy).
- **Łącznik:** Na środku widać grzbiet księgi z metalowymi okuciami.

---

## 3. Techniczne Podniesienie Jakości:
- **Custom Shader Effects:** Wprowadzenie efektu "pływającego pyłu" i "migotania świecy" na tekście (Subtelne animacje Alpha).
- **9-Patch Pro:** Precyzyjne pocięcie `ui_frame_gold` dla uzyskania efektu ciężkiej, trójwymiarowej ramy.

---
*To będzie interfejs, który wygląda jakby kosztował fortunę i ważył 10 kilogramów.*
