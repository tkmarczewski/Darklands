# PROPOZYCJA REDESIGNU UI V2: "KODEKS DYNAMICZNY"
**Cele:** Redukcja szumu wizualnego, responsywność ramek, minimalistyczna nawigacja.

---

## 1. Rozwiązanie Problemu Skalowania (Frame Fix)
Zamiast używać statycznych plików graficznych jako pełnych ramek (co psuje proporcje na Pixel 8), wprowadzam **System 9-Patch w Compose**:
- **Technika:** Rozcinamy `Ramka cienka.png` na narożniki i krawędzie.
- **Efekt:** Ramka "rozciąga się" płynnie niezależnie od rozmiaru ekranu, zachowując detal na rogach bez rozmycia.

---

## 2. Nawigacja: "Żelazne Okucie" (Drawer Menu)
Zamiast 6-8 przycisków na środku ekranu, wprowadzamy **Zintegrowany Panel Akcji**:
- **Ukryty Stan:** Na ekranie widać tylko mroczną ilustrację regionu (`Las cieni.png`) i log Trybunału.
- **Aktywacja:** Kliknięcie w małą ikonę pieczęci (lub gest swipe od lewej) wysuwa **"Żelazne Okucie"** (pionowy panel boczny).
- **Zawartość:** Elegancka, pionowa lista kategorii (Mapa, Inwentarz, Kronika, Drużyna) ukryta w jednym miejscu.

---

## 3. Interfejs Kontekstowy (Contextual UI)
Zamiast stałych buttonów "Kupuj", "Handluj", "Rozmawiaj":
- **Orb Menu:** Po kliknięciu w NPC/Lokalizację pojawia się mały pierścień opcji wokół punktu dotyku (Inspiracja *Stoneshard* / *Baldur's Gate 3 Mobile*).
- **Zaleta:** UI zajmuje miejsce tylko wtedy, gdy jest potrzebne.

---

## 4. Statystyki: "Krwawe Zdobienia"
- Statystyki drużyny są teraz częścią dolnej krawędzi ekranu.
- **Brak pasków:** HP i Mana są renderowane jako "wypalenia" lub "krwawe nacieki" na rogach portretów. Mniej "growo", bardziej diegetycznie.

---

## 5. Plan Pracy:
1.  **Dzień 1:** Implementacja `ScalableGrimFrame` (System 9-patch).
2.  **Dzień 2:** Budowa wysuwanego Panelu Akcji ("Żelazne Okucie").
3.  **Dzień 3:** Refaktoryzacja HUB-u — usunięcie wszystkich statycznych buttonów na rzecz nowej nawigacji.

---
*Czy koncepcja ukrytego panelu bocznego (Drawer) zamiast buttonów na środku Ci odpowiada?*
