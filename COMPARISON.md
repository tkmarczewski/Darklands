# Darklands Mobile vs Darklands (1992) — audyt zgodności

**Data analizy:** 2026-05-24  
**Zakres:** porównanie aktualnego stanu projektu z mechanikami i contentem oryginalnego Darklands, z naciskiem na to, co już jest, czego brakuje i co powinno wejść do kolejnych sprintów. [file:28][file:32][web:50]

| Obszar | Co już jest w kodzie | Jak to wygląda w Darklands | Czego brakuje / co poprawić | Priorytet |
|---|---|---|---|---|
| **Świat i mapa** | Jest `WorldMap`, kilka lokacji pobocznych, 1 główne miasto (Magdeburg) i 8 dzielnic miejskich. [file:28] | Oryginał ma bardzo rozbudowaną mapę świata z wieloma miastami, połączeniami, terenami i regionalnymi różnicami. [web:50][web:62] | Dodać kolejne miasta, sensowną sieć połączeń, lokalne cechy miast, różne typy terenu i regionalne modyfikatory. | **Krytyczny** |
| **Miasta** | Obecnie praktycznie tylko Magdeburg jako pełne miasto. [file:28] | Miasta są jednym z głównych filarów gry; służą do handlu, plotek, zadań, religii, leczenia i karier. [web:50][web:65] | Dodać co najmniej 10–15 głównych miast w pierwszym kroku, a potem rozbudowywać do większej liczby lokacji. | **Krytyczny** |
| **Dzielnice miejskie** | Jest 8 typów dzielnic: Market, Church, Inn, Blacksmith, Alchemist, Guildhall, Castle, Slums. [file:28] | Darklands używa lokacji miejskich jako funkcjonalnych punktów gry; marketplace/markt jest istotnym miejscem handlu, banku i questów. [web:65] | Rozszerzyć użycie dzielnic o więcej interakcji, NPC i eventów przypisanych do konkretnych miejsc. | **Wysoki** |
| **Święci** | Jest `SaintCatalogue` z 5 świętymi. [file:28] | W oryginale lista świętych jest bardzo duża; święci mają konkretne efekty, często bardzo wyspecjalizowane i powiązane z reputacją lub sytuacją. [web:58][web:66] | Rozbudować katalog świętych do kilkudziesięciu pozycji, dodać zróżnicowane efekty, lokalne bonusy i warunki użycia. | **Krytyczny** |
| **Lokalna reputacja** | Jest `ReputationState` i reputacja frakcji; z dokumentów wynika też plan podłączenia do ekonomii. [file:28][file:30] | Darklands ma lokalną reputację w konkretnych miastach, osobno od globalnych relacji, i wpływa ona na reakcje świata. [web:59] | Doprecyzować i wykorzystać lokalną reputację w eventach, sklepach, NPC i świętych. | **Wysoki** |
| **Kariery** | Jest `CareerChain` z 8 karierami. [file:28] | Oryginał ma 37 karier w kilku grupach: wojskowe, cywilne, religijne, akademickie, rzemieślnicze i underworld. [file:28][web:54] | Dodać brakujące łańcuchy karier, tło społeczne i pełniejsze wymagania awansu. | **Krytyczny** |
| **Tło społeczne** | W roadmapie jest plan dodania `SocialBackground`. [file:32] | W Darklands pochodzenie społeczne wpływa na sensowność i dostępność części karier. [web:54] | Wprowadzić i zintegrować z karierami, create character i wymaganiami questów. | **Wysoki** |
| **Walki** | Jest `Combat`, morale, rany, recovery i podstawy walki. [file:28] | Walki w Darklands są taktyczne, z naciskiem na morale, obrażenia, rodzaj przeciwników i kontekst spotkania. [web:62] | Dopisać więcej wariantów przeciwników, lepsze AI i więcej kontekstowych encounterów. | **Średni** |
| **Alchemia** | Jest `Alchemy.kt`, 22 mikstury, składniki, quality points i typy efektów. [file:28] | Alchemia w Darklands jest jedną z najważniejszych mechanik i ma duże znaczenie taktyczne. [web:54][web:62] | Wzmacniać integrację alchemii z questami, walką i ekonomią, a nie tylko sam katalog mikstur. | **Średni** |
| **Umiejętności** | Jest `HeroSkills` z 18 umiejętnościami. [file:28] | Oryginał mocno opiera się na skill-checkach w różnych kontekstach: dialog, podróż, walka, religia, alchemia. [web:54] | Dodać więcej punktów użycia skill-checków w eventach, NPC i questach. | **Wysoki** |
| **Religia i łaska** | Jest `Religion.kt`, `DivineFavor`, `VirtueSystem`. [file:28] | Religia jest centralna: modlitwa, święci, łaska, cnoty, grzechy i specjalne błogosławieństwa wpływają na przebieg gry. [web:55][web:58] | Rozszerzyć katalog świętych i dodać więcej rzeczywistych decyzji moralno-religijnych w świecie. | **Krytyczny** |
| **Questy** | Jest `QuestGraph` i dwa łańcuchy: Raubritter oraz Endgame/Kult Baphometa. [file:28] | Darklands działa mocno przez łańcuchy zadań, plotki, ślady w miastach i wydarzenia powiązane z mapą. [web:50][web:62] | Dodać 3–6 kolejnych quest chainów i lepiej spiąć je z plotkami, NPC i regionami. | **Krytyczny** |
| **Plotki** | W roadmapie jest plan `RumorSystem`. [file:32] | Plotki są jednym z głównych sposobów generowania i prowadzenia przygód. [web:50] | Wprowadzić plotki jako osobny system wejścia do questów, z wiarygodnością i regionem. | **Wysoki** |
| **NPC** | W roadmapie planowany `NamedNpc`. [file:32] | Oryginał bazuje na konkretnych postaciach w miastach i lokalizacjach, nie tylko na anonimowych opisach. [web:50] | Dodać nazwanych NPC, ich role i powiązania z plotkami, questami i reputacją. | **Wysoki** |
| **Ekonomia** | README sugeruje system ekwipunku i reputacji, ale w COMPARISON brakuje pełnego systemu cen i handlu. [file:28][file:30] | W Darklands ceny, dostępność i usługi zależą od miejsca, reputacji i kontekstu miasta. [web:65][web:59] | Dodać `EconomySystem`, lokalne mnożniki cen, sprzedaż/kupno i wpływ reputacji. | **Wysoki** |
| **Czas dobowy** | Jest `TimeOfDay` i `DayNightSystem`. [file:28] | Pora dnia wpływa na eksplorację, spotkania, dostępność usług i rodzaj zagrożeń. [web:50][web:62] | Połączyć dzień/noc z eventami, zamykaniem lokacji i typami encounterów. | **Średni** |
| **Bestiary** | Jest `BestiaryAndEncounters` z 16 typami przeciwników i 9 encounterami. [file:28] | Darklands ma szeroki zestaw ludzi, potworów, demonów i bossów, a spotkania są ważnym elementem mapy. [web:62] | Rozbudować listę przeciwników, dodać lepsze dopasowanie do regionów i pory dnia. | **Wysoki** |
| **Save system** | Jest autosave, sloty, walidacja i migracja wersji. [file:28] | To obszar czysto techniczny, ale ważny dla jakości gry i nie jest wyróżnikiem oryginału jako content. | Raczej utrzymać i testować, niż rozbudowywać. | **Niski** |
| **Random world generation** | W roadmapie jako plan na później. [file:32] | Oryginał miał charakter sandboxowy i korzystał z dużej różnorodności lokacji i przygód. [web:62] | Dodać generowane lokacje, ale dopiero po domknięciu podstawowego contentu. | **Średni** |
| **Turnieje i relikwie** | Na razie w roadmapie, bez potwierdzenia w rdzeniu. [file:32] | To tematy zgodne z klimatem gry, ale nie są jeszcze podstawą core loop. | Traktować jako rozszerzenia po zbudowaniu świata i questów. | **Niski** |

## Najważniejsze luki do zamknięcia

| Kolejność | Brak | Dlaczego to ważne |
|---|---|---|
| 1 | Więcej miast i pełna mapa świata. | Bez tego gra nadal wygląda jak rdzeń systemowy, a nie pełna wersja Darklands. [file:28][web:50] |
| 2 | Rozbudowany katalog świętych. | To jedna z najbardziej charakterystycznych mechanik oryginału. [web:58][web:66] |
| 3 | Pełniejsze kariery i tło społeczne. | To wpływa na tworzenie postaci, progresję i wiarygodność świata. [web:54] |
| 4 | Plotki i nazwani NPC. | Bez tego questy są zbyt „techniczne” i za mało osadzone w świecie. [web:50] |
| 5 | Ekonomia i lokalne ceny. | To spina miasta, reputację i wyposażenie w jeden system. [web:59][web:65] |
| 6 | Więcej quest chainów. | Obecnie zawartość fabularna jest zbyt mała względem skali oryginału. [file:28] |
| 7 | Lepsze powiązanie dnia/nocy z gameplayem. | W Darklands kontekst czasu ma znaczenie dla eksploracji i eventów. [web:50][web:62] |

## Sugestia priorytetów

| Sprint | Cel | Minimalny zakres |
|---|---|---|
| Sprint 1 | Mapa i miasta. | 10 nowych miast, połączenia, podstawowe dane miast, lokalne eventy. |
| Sprint 2 | Święci i religia. | 20–30 nowych świętych, kilka mocnych efektów, lokalne bonusy. |
| Sprint 3 | Kariery i tło społeczne. | Dołożenie brakujących karier i `SocialBackground`. |
| Sprint 4 | Plotki, NPC, ekonomia. | `RumorSystem`, `NamedNpc`, ceny lokalne, reakcje reputacji. |
| Sprint 5 | Quest chainy i world content. | 3–6 nowych łańcuchów, lepsze spięcie z mapą i plotkami. |

## Wniosek końcowy

Projekt ma już sensowny **core**, ale w porównaniu z Darklands największym brakiem jest jeszcze nie „silnik”, tylko **świat, dane i powiązania między systemami**. Jeśli chcesz zbliżyć się do charakteru oryginału, to następny duży krok powinien iść w miasta, świętych, kariery, plotki i lokalną ekonomię, bo właśnie tam Darklands buduje swoją tożsamość. [file:28][file:30][web:50][web:54][web:55]
