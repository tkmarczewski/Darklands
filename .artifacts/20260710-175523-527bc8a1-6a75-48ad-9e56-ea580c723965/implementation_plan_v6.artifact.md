# Plan Implementacji - Etap 6: Ekspansja Fabularna i Cienie Towarzyszy

Celem Etapu 6 jest znaczące wzbogacenie zawartości gry oraz wdrożenie unikalnej mechaniki "Cieni Towarzyszy", która łączy śmierć bohaterów z narracją o upadku rzeczywistości.

## Proponowane Zmiany

### 1. Masowa Rozbudowa Zawartości
Dodanie nowych zadań i dialogów, aby zapełnić świat gry (wykorzystując nowy system shufflingu).

#### [Assets] [quests_extended.json](file:///C:/repo2/app/src/main/assets/grimreich/quests_extended.json)
- Dodanie 10-15 nowych zadań, w tym:
    - **q_coast_harvest**: Proste zadanie zbierackie na start.
    - **q_deserter**: Moralny wybór w Porcie Mglistym.
    - **q_fortress_letter**: Zadanie kurierskie łączące miasta.
    - **q_echo_whispers**: Zadanie typu META odblokowujące wiedzę o upadku.

#### [Assets] [dialogues_pilot.json](file:///C:/repo2/app/src/main/assets/grimreich/dialogues_pilot.json)
- Rozszerzenie dialogów dla NPC: Dezerter, Dowódca Ferrum, oraz Mistyk.

### 2. Cienie Towarzyszy (Companion Shadows)
Wdrożenie mechaniki, w której polegli bohaterowie nie znikają całkowicie, lecz nawiedzają drużynę jako "usterki".

#### [GameState.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/GameState.kt) (logika)
- Wykorzystanie istniejącej listy `companionShadows`.
- Automatyczne przenoszenie bohatera do tej listy po śmierci (jeśli nie zostanie wskrzeszony w Kaplicy).

#### [ProceduralNpcGenerator.kt](file:///C:/repo2/app/src/main/java/com/grimreich/world/ProceduralNpcGenerator.kt)
- Szansa na spotkanie Cienia Towarzysza w miastach o niskiej stabilności.
- Cienie będą miały zniekształcone imiona (np. "ECHO_FELIX") i specjalne linie dialogowe.

### 3. Rozszerzenie Rytuałów

#### [RitualSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/RitualSystem.kt)
- Dodanie rytuału "Utrwalenie Echa": Pozwala na stałe zapisać statystyki poległego bohatera jako bonus dla całej drużyny (kosztem stabilności świata).

---

## Plan Weryfikacji

### Testy Automatyczne
- `ContentValidator`: Uruchomienie walidatora po dodaniu nowej zawartości (musi przejść bez bzędów).
- `ShadowSpawnTest`: Weryfikacja czy polegli bohaterowie trafiają do listy cieni.

### Manualna Weryfikacja
- Sprawdzenie czy nowe zadania pojawiają się na tablicy ogłoszeń.
- Celowe uśmiercenie bohatera i próba spotkania go jako ECHO w mieście o niskiej stabilności.
