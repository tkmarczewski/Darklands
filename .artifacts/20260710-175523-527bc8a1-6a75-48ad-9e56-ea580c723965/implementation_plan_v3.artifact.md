# Plan Implementacji - Etap 3: UI / UX Refaktor

Celem Etapu 3 jest modernizacja warstwy prezentacji zgodnie z najlepszymi praktykami Jetpack Compose oraz unifikacja zarządzania postaciami.

## Proponowane Zmiany

### 1. Modernizacja City i Expedition
Zastosowanie wzorca `UiState`, `UiEvent`, `UiEffect` oraz rozdzielenie `Route` od `Content`.

#### [CityViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/city/CityViewModel.kt)
- Wprowadzenie `UiEffect` dla nawigacji (dialogi, rynek itp.).
- Uproszczenie `refresh()` - mapowanie z `GameState` bez pobierania dodatkowych snapshotów.

#### [ExpeditionViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/main/ExpeditionViewModel.kt)
- Zamiana wielu nullable pól w `ExpeditionUiState` na jeden sealed `content: ExpeditionContentState`.
- Obsługa `UiEffect` dla przejść do walki i dialogów.

### 2. Character Hub (Unifikacja)
Połączenie rozproszonych ekranów bohatera, drużyny i ekwipunku w jeden spójny system.

#### [NEW] [CharacterHubViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/character/CharacterHubViewModel.kt)
- Wspólny ViewModel dla wszystkich zakładek postaci.
- Zarządzanie aktywnym bohaterem i kolejnością drużyny.

#### [NEW] [CharacterHubScreen.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/character/CharacterHubScreen.kt)
- Główny ekran z zakładkami: Przegląd | Ekwipunek | Drużyna.
- Współdzielone UI models (np. `HeroUi`).

---

## Plan Weryfikacji

### Manualna Weryfikacja
- Sprawdzenie czy nawigacja między zakładkami Character Hub jest płynna i zachowuje stan (np. wybrany bohater).
- Weryfikacja czy efekty glitcha w mieście odświeżają się poprawnie przy zmianie stabilności.

### Testy UI
- Renderowanie podglądów Compose dla nowych komponentów `CharacterHub`.
