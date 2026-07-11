# Plan Implementacji - Etap 7: Potęga Echa i Rozwój Bohaterów

Celem Etapu 7 jest pogłębienie systemów rozwoju postaci oraz wprowadzenie unikalnych mechanik "Echa", które pozwalają graczowi balansować na krawędzi stabilności świata dla zyskania potężnych bonusów.

## Proponowane Zmiany

### 1. Rozszerzenie Systemu Umiejętności
Dodanie nowych, potężniejszych umiejętności wykorzystujących zasób "Favor" oraz "Echo".

#### [SkillCatalogue.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/SkillCatalogue.kt)
- Dodanie 5-7 nowych umiejętności, m.in.:
    - **echo_step**: Uniknięcie następnego ataku kosztem 0.1 echa.
    - **righteous_fury**: Potężny atak obszarowy dostępny tylko przy wysokiej wierze.
    - **mind_collapse**: Próba natychmiastowego uciszenia wroga kosztem stabilności regionu.

### 2. Drzewka Kariery (Career Progression)
Wprowadzenie wpływu kariery na statystyki i unikalne bonusy pasywne.

#### [Hero.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/Hero.kt)
- Implementacja `effectiveAgility()` i `effectiveIntelligence()` uwzględniających bonusy z kariery.
- Dodanie pola `passiveAbilities: List<String>` dla bohaterów.

### 3. Mechanika Rytuałów Echa
Umożliwienie graczowi świadomego wywoływania "Glitchy" w zamian za rzadkie surowce lub wiedzę.

#### [EchoSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/EchoSystem.kt)
- Nowa funkcja: `forceRealityLeak(regionId)` - obniża stabilność regionu o 20, ale generuje unikalny loot "Echo Dust".

---

## Plan Weryfikacji

### Testy Automatyczne
- `SkillEffectTest`: Weryfikacja czy nowe umiejętności poprawnie modyfikują stan świata (np. czy `echo_step` faktycznie pobiera echo).
- `CareerBonusTest`: Sprawdzenie czy statystyki bohatera są poprawnie przeliczane po zmianie kariery.

### Manualna Weryfikacja
- Użycie umiejętności Echo w walce i obserwacja zmian na wskaźniku stabilności.
- Sprawdzenie w Character Hub, czy bonusy z kariery są poprawnie wyświetlane.
