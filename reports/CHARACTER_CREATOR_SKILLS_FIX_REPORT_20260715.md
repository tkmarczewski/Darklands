# RAPORT: NAPRAWA WIDOCZNOŚCI SPECJALIZACJI - 2026-07-15

## 1. STATUS OPERACYJNY: ZGODNY
Naprawiono krytyczny błąd w kreatorze postaci, który uniemożliwiał wybór specjalizacji umiejętności.

## 2. ZREALIZOWANE ZMIANY

### A. Poprawa Filtrowania Umiejętności
*   **Problem:** Logika filtrowania była zbyt restrykcyjna dla niektórych klas, co w połączeniu z brakiem inicjalizacji stanu początkowego skutkowało pustą listą na etapie SKILLS.
*   **Naprawa:** 
    *   Wprowadzono mechanizm **Fallback Skills**. Jeśli wybrana klasa ma przypisane mniej niż 5 unikalnych umiejętności, system automatycznie rozszerza listę o podstawowe zdolności przetrwania i walki.
    *   Naprawiono mapowanie `HeroSkill.ALCH` (używano porównania stringów zamiast typu enum).

### B. Inicjalizacja Stanu Kreatora
*   **Zmiana:** W bloku `init` ViewModelu wymuszono wywołanie `selectCareer(Career.PAGE)`. Gwarantuje to, że nawet jeśli gracz nie kliknie ręcznie w pierwszą dostępną klasę, lista umiejętności zostanie poprawnie wygenerowana w tle.

## 3. WYNIKI WERYFIKACJI
*   **Test Emulator:** 
    1. Start kreatora -> Etap Kariery -> Etap Atrybutów -> Etap Umiejętności.
    2. **Wynik:** Lista umiejętności (np. Ostrza, Obuchowe, Leczenie dla Pazia) jest teraz poprawnie wyświetlana i pozwala na wybór 3 specjalizacji.
    3. Przycisk "DALEJ" aktywuje się poprawnie po wybraniu punktów.

---
**WYRYTO W KRZEMIE PRZEZ:** *Agenta Stabilizacji GrimReich*
**STATUS REPOZYTORIUM:** `SYNCHRONIZED (MASTER)`
