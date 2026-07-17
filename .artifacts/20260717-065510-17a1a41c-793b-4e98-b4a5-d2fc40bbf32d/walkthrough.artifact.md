# Walkthrough - Unique Dialogues and Death Logic Fixes

Ten dokument podsumowuje zmiany wprowadzone w celu naprawy logiki śmierci BG oraz rozbudowy systemu zadań.

## Główne osiągnięcia

### [dialogues_extended.json](file:///C:/repo2/app/src/main/assets/grimreich/dialogues_extended.json)
Dodano unikalne dialogi dla zadań, co eliminuje generyczne odpowiedzi NPC. Każde zadanie z tablicy ma teraz własne wprowadzenie fabularne.

### [CityViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/city/CityViewModel.kt)
Ulepszono routing dialogowy. System inteligentnie wybiera najbardziej dopasowany węzeł:
1. `{quest_id}_start` / `{quest_id}_check`
2. `{role}_start` / `{role}_quest_check`
3. Fallback generyczny (`peasant_start`)

### [CombatSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/CombatSystem.kt) & [GameRootViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/main/GameRootViewModel.kt)
Naprawiono błąd, w którym śmierć BG w trakcie walki mogła powodować błędy nawigacji. Teraz walka jest natychmiast przerywana, a `DEATH OBSERVER` bezpiecznie przenosi gracza na ekran Rytuału.

## Weryfikacja
- Kompilacja: **SUCCESS**
- Testy dialogów: Logi potwierdzają poprawny wybór węzłów dla zadań `q_blood_icon` i `q_lost_apostle`.
- Testy śmierci: Przejście do ekranu Rytuału jest stabilne i następuje po zakończeniu logiki bojowej.

Pełny raport znajduje się w [task_report_20260717.md](file:///C:/repo2/reports/task_report_20260717.md).
