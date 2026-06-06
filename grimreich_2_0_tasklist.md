# GrimReich 2.0 — pełny plan implementacji i tasklista ticków

## Cel dokumentu

Dokument opisuje pełny plan implementacji GrimReich 2.0 jako warstwy docelowej nad wcześniejszym etapem 1.5, zakładając, że 1.5 jest realizowane osobno i nie stanowi zakresu tego planu.[file:2]
Celem 2.0 jest uruchomienie świata jako jednej żywej symulacji synchronizującej pętle NPC, regionów i Collapse oraz silniki historii, mutacji i fenomenów, z nadrzędną warstwą Absolutu, Regionu AbsoluteVersion i Lyssy AbsoluteVersion.[file:2]

## Założenia wejściowe

GrimReich 2.0 składa się z 12 głównych modułów: Collapse AI 2.0, Collapse Scenarios 2.0, NPC Advanced AI 2.0, Region Advanced AI 2.0, Region AbsoluteVersion, NPC Lyssa AbsoluteVersion, Phenomena Engine 2.0, History Engine 2.0, Mutation Engine 2.0, Expedition Generator 2.0, Other Side Layers 2.0 i World Simulation 2.0.[file:2]
Dokument źródłowy określa też priorytety systemowe: Absolut, Collapse AI, Region AbsoluteVersion, NPC AbsoluteVersion, Phenomena, Region AI, NPC AI, History Engine, Mutation Engine, Expedition Generator, Other Side i World Simulation.[file:2]
To oznacza, że implementacja 2.0 musi być projektowana jako architektura nadrzędna nad niższymi systemami, a nie jako zbiór niezależnych feature’ów.[file:2]

## Granica zakresu 2.0

Zakres 2.0 nie obejmuje tworzenia uproszczonych systemów przejściowych ani ponownego wdrażania elementów planowanych w 1.5.[file:2]
Zakres 2.0 obejmuje trzy rzeczy: zdefiniowanie kontraktów integracyjnych nad światem po 1.5, wdrożenie pełnych modułów domenowych 2.0 oraz ich zsynchronizowanie w jednym deterministycznym modelu symulacji.[file:2]
W praktyce 1.5 może dostarczać świat operacyjny, ale 2.0 ma dostarczyć świat ontologicznie pełny: wielowersyjny, mutujący, historycznie rozszczepialny i nadpisywalny przez warstwy absolutne.[file:2]

## Architektura wykonawcza

Pełen program wdrożenia 2.0 należy rozbić na osiem głównych programów implementacyjnych: kontrakty 2.0, rdzeń ontologii świata, orkiestrator czasu i pętli, Collapse AI ze scenariuszami, pełne Region AI, pełne NPC AI, History i Mutation Engine oraz warstwę Absolut/AbsoluteVersion/Lyssa/Other Side.[file:2]
Takie rozbicie jest zgodne z dokumentem, który opisuje zarówno odrębne moduły domenowe, jak i ich pełną integrację w World Simulation 2.0.[file:2]
Kolejność implementacji powinna wynikać z zależności systemowych, a nie z atrakcyjności designerskiej poszczególnych mechanik.[file:2]

## Program 0 — kontrakty integracyjne 2.0

### Cel

Pierwszym krokiem jest ustalenie, jakie dane i zdarzenia świata po 1.5 muszą być dostępne, aby systemy 2.0 mogły działać bez silnego sprzężenia z implementacją przejściową.[file:2]
Na tym etapie nie implementuje się jeszcze pełnej logiki fenomenów, historii czy absolutnych bytów; powstają wyłącznie kontrakty i modele wejścia do warstwy 2.0.[file:2]

### Deliverables

- `WorldSnapshot`
- `SimulationTickContext`
- `RegionStateContract`
- `NpcStateContract`
- `ScenarioStateContract`
- `HistoryStateContract`
- `MutationStateContract`
- `PhenomenaStateContract`
- `AbsoluteLayerContract`

### Tasklista

- Zdefiniować canonical read model świata dla 2.0.[file:2]
- Rozdzielić stan runtime od stanu zapisywalnego do save/load.[file:2]
- Ustalić, które dane są authoritative, a które są pochodne.[file:2]
- Zdefiniować wspólny format eventów emitowanych przez moduły.[file:2]
- Zdefiniować world diff i commit contract dla centralnego resolvera.[file:2]
- Zaprojektować wersjonowanie kontraktów, aby 2.0 można było rozwijać bez łamania save’ów.[file:2]

### Ticki wykonawcze

1. Spisać wszystkie byty obecne po 1.5 i zmapować je na oczekiwane byty 2.0.[file:2]
2. Utworzyć interfejs `WorldSnapshotProvider`.[file:2]
3. Utworzyć interfejs `WorldMutationResolver`.[file:2]
4. Utworzyć kontrakt `SimulationTickContext` z rozróżnieniem Micro/Meso/Macro.[file:2]
5. Zdefiniować minimalny zestaw pól regionu, NPC, historii i Collapse wymagany przez 2.0.[file:2]
6. Dodać testy kompatybilności kontraktów.[file:2]

## Program 1 — model domeny 2.0

### Cel

Drugi etap polega na formalizacji wszystkich bytów opisanych w dokumentacji jako pełnego modelu domenowego 2.0.[file:2]
Dokument źródłowy zawiera już bazowe struktury danych, w tym `CollapseAI`, `NPCAdvancedAI`, `RegionAdvancedAI`, `CollapseScenario2`, `HistoryEngine`, `MutationEngine`, `OtherSide` i `WorldSimulation`, więc zadaniem implementacyjnym jest przekształcenie ich w stabilny model produkcyjny.[file:2]

### Zakres modeli

- Fenomeny: Mist, Blood, Reflection, Fullness, Chaos, Zero, Absolut.[file:2]
- Scenariusze: Mist Oblivion, Blood Ruin, Reflection Reckoning, Fullness Ascension, Chaos Dominion, Zero End.[file:2]
- Mutacje: fizyczne, emocjonalne, pamięciowe, tożsamościowe, historyczne, absolutne.[file:2]
- Wersje Lyssy: MistVersion, ChaosVersion, ZeroVersion, AbsoluteVersion.[file:2]
- Warstwy Other Side: MistSide, BloodSide, ReflectionSide, FullnessSide, ChaosSide, ZeroSide, AbsoluteSide.[file:2]

### Tasklista

- Zdefiniować sealed hierarchie dla fenomenów, scenariuszy i warstw Other Side.[file:2]
- Utworzyć agregaty domenowe dla NPC, regionu, Collapse i historii.[file:2]
- Rozdzielić command model, query model i event model.[file:2]
- Zdefiniować invarianty domenowe dla mutacji, aktywnej wersji historii i aktywnej wersji NPC.[file:2]
- Zaprojektować identyfikatory stabilne między save/load, replay i debug tools.[file:2]

### Ticki wykonawcze

1. Utworzyć pakiety domenowe dla `phenomena`, `collapse`, `npc`, `region`, `history`, `mutation`, `absolute`, `otherside`.[file:2]
2. Zaimplementować enumy/sealed classes dla wszystkich podstawowych typów.[file:2]
3. Zaimplementować modele danych zgodne z dokumentem.[file:2]
4. Dodać walidatory invariantów domenowych.[file:2]
5. Dodać snapshot serialization tests.[file:2]

## Program 2 — orkiestrator czasu i symulacji

### Cel

World Simulation 2.0 ma synchronizować NPC loops, region loops, collapse loops, history engine, mutation engine i phenomena engine, dlatego przed implementacją zaawansowanych mechanik trzeba zbudować deterministyczny scheduler świata.[file:2]
Dokument rozróżnia pętle Micro, Meso i Macro dla Collapse, NPC i regionów, więc orkiestrator musi obsługiwać te trzy skale czasu oraz zależności między nimi.[file:2]

### Tasklista

- Zbudować `WorldSimulationCoordinator`.[file:2]
- Zdefiniować kolejność wykonywania etapów ticka.[file:2]
- Dodać pre-tick snapshot i post-tick commit.[file:2]
- Dodać event queue i conflict resolver.[file:2]
- Dodać replay mode i seed-based determinism.[file:2]
- Dodać profiling per subsystem.[file:2]

### Proponowana kolejność jednego pełnego cyklu

1. Snapshot wejściowy świata.[file:2]
2. Aktualizacja aktywnych fenomenów.[file:2]
3. Tick Collapse AI.[file:2]
4. Tick regionów.[file:2]
5. Tick NPC.[file:2]
6. Tick historii.[file:2]
7. Tick mutacji.[file:2]
8. Tick generatora ekspedycji.[file:2]
9. Resolve warstw Other Side.[file:2]
10. Commit i publikacja world diff.[file:2]

### Ticki wykonawcze

1. Utworzyć `TickPhase` dla wszystkich faz symulacji.[file:2]
2. Utworzyć scheduler Micro/Meso/Macro.[file:2]
3. Zaimplementować deterministyczną kolejkę eventów.[file:2]
4. Dodać centralny commit resolver.[file:2]
5. Dodać log world diff do debugowania.[file:2]
6. Dodać testy replay dla identycznego seeda.[file:2]

## Program 3 — Collapse AI 2.0 i Collapse Scenarios 2.0

### Cel

Collapse AI 2.0 składa się z warstw poznawczych MistMind, BloodBody, ReflectionSoul, FullnessHeart, ChaosFlux i ZeroHollow, działa w pętlach Micro/Meso/Macro i używa funkcji VectorShift, ScenarioOverride, CollapsePulse oraz CollapsePause.[file:2]
Scenariusze Collapse mają własne osobowości, cele, mechaniki, eventy, zakończenia i przejścia, a dokument podaje też przykładową mapę przejść między scenariuszami.[file:2]

### Tasklista

- Zaimplementować pełny model `CollapseCognition`.[file:2]
- Zaimplementować `CollapseMotivation`, `CollapseVector`, `CollapseScenarioState`.[file:2]
- Zbudować graf przejść scenariuszy.[file:2]
- Zaimplementować `VectorShift()` na bazie dominującej warstwy poznawczej.[file:2]
- Zaimplementować `CollapsePulse()` jako globalny generator zdarzeń światowych.[file:2]
- Zaimplementować `ScenarioOverride()` i `CollapsePause()` jako mechanizmy nadrzędne.[file:2]
- Powiązać scenariusze z regionami, NPC i historią.[file:2]

### Ticki wykonawcze

1. Utworzyć model danych `CollapseAI`.[file:2]
2. Utworzyć model danych `CollapseScenario2`.[file:2]
3. Zakodować wszystkie scenariusze i ich przejścia.[file:2]
4. Dodać evaluator dominującej warstwy poznawczej.[file:2]
5. Dodać engine przejść scenariuszowych.[file:2]
6. Dodać emiter `CollapsePulseEvent`.[file:2]
7. Dodać testy zgodności przejść z mapą dokumentu.[file:2]
8. Podłączyć output Collapse do regionów, NPC i ekspedycji.[file:2]

## Program 4 — Phenomena Engine 2.0

### Cel

Każdy z siedmiu fenomenów wpływa według dokumentu na NPC, regiony, historię, mutacje i sam Collapse, więc potrzebny jest osobny silnik propagacji ich skutków.[file:2]
Phenomena Engine musi być niezależny od warstwy prezentacji i pracować jako wspólny dostawca efektów do pozostałych modułów.[file:2]

### Tasklista

- Zaimplementować modele wszystkich fenomenów.[file:2]
- Zdefiniować wspólny interfejs efektów fenomenów.[file:2]
- Zaimplementować lokalne i globalne oddziaływanie fenomenów.[file:2]
- Zaimplementować wpływ fenomenów na scenariusze, historię i mutacje.[file:2]
- Dodać obsługę współwystępowania kilku fenomenów.[file:2]

### Ticki wykonawcze

1. Utworzyć `PhenomenaEngine`.[file:2]
2. Utworzyć modele `MistPhenomenon`, `BloodPhenomenon`, `ReflectionPhenomenon`, `FullnessPhenomenon`, `ChaosPhenomenon`, `ZeroPhenomenon`, `AbsolutePhenomenon`.[file:2]
3. Dodać evaluator natężenia wpływu na region, NPC i historię.[file:2]
4. Dodać pipeline nakładania efektów fenomenów.[file:2]
5. Dodać testy konfliktów między fenomenami.[file:2]

## Program 5 — Region Advanced AI 2.0

### Cel

Region Advanced AI 2.0 obejmuje geometrię, osobowość, mutacje, historię, relacje z NPC i relacje z Collapse oraz behavior trees: RegionObserver, RegionMutator, RegionEraser, RegionCreator i RegionJudge.[file:2]
Region w 2.0 jest aktywnym bytem symulacyjnym, a nie tylko kontenerem encounterów i parametrów.[file:2]

### Tasklista

- Zaimplementować finalny model `RegionAdvancedAI`.[file:2]
- Zaimplementować `RegionGeometry`, `RegionPersonality`, `RegionMutations`, `RegionHistory`, `RegionBehaviorTrees`.[file:2]
- Zakodować pięć ról behavior tree lub ich deterministyczne odpowiedniki.[file:2]
- Powiązać region z historią, mutacjami, fenomenami i Collapse.[file:2]
- Dodać trzy poziomy ewolucji: lokalny, strukturalny i długoterminowy.[file:2]

### Ticki wykonawcze

1. Utworzyć model danych regionu.[file:2]
2. Utworzyć pipeline dla Micro/Meso/Macro loop regionów.[file:2]
3. Zaimplementować `RegionObserver`.[file:2]
4. Zaimplementować `RegionMutator`.[file:2]
5. Zaimplementować `RegionEraser`.[file:2]
6. Zaimplementować `RegionCreator`.[file:2]
7. Zaimplementować `RegionJudge`.[file:2]
8. Dodać resolver wpływu regionu na scenariusz Collapse.[file:2]
9. Dodać resolver wpływu regionu na mutacje NPC.[file:2]
10. Dodać testy ewolucji regionu w długim horyzoncie.[file:2]

## Program 6 — NPC Advanced AI 2.0

### Cel

NPC Advanced AI 2.0 obejmuje warstwy Cognition, Emotion, Motivation, Memory, Mutation i CollapseRole oraz mechaniki IdentitySplit, MemoryEcho, MutationCascade i ZeroShift.[file:2]
Dokument opisuje też szczegółowo modele `NPCCognition`, `NPCEmotion`, `NPCMemory` i wariantów tożsamości, co oznacza, że w 2.0 NPC muszą obsługiwać wiele wersji siebie i pamięć alternatywnych historii.[file:2]

### Tasklista

- Zaimplementować finalne `NPCAdvancedAI`.[file:2]
- Zaimplementować wielowersyjną pamięć i aktywną wersję pamięci.[file:2]
- Zaimplementować `IdentitySplit`.[file:2]
- Zaimplementować `MemoryEcho`.[file:2]
- Zaimplementować `MutationCascade` i `ZeroShift`.[file:2]
- Zintegrować NPC z regionem, fenomenami, historią i Collapse.[file:2]

### Ticki wykonawcze

1. Utworzyć model danych NPC.[file:2]
2. Utworzyć pipeline dla Micro/Meso/Macro loop NPC.[file:2]
3. Dodać `NPCCognition`.[file:2]
4. Dodać `NPCEmotion`.[file:2]
5. Dodać `NPCMemory` z `versions` i `activeVersion`.[file:2]
6. Zaimplementować `IdentitySplit`.[file:2]
7. Zaimplementować `MemoryEcho`.[file:2]
8. Zaimplementować `MutationCascade`.[file:2]
9. Zaimplementować `ZeroShift`.[file:2]
10. Dodać testy stabilności tożsamości i pamięci.[file:2]

## Program 7 — History Engine 2.0

### Cel

History Engine 2.0 składa się z timeline’ów, ParadoxHistory i AbsoluteHistory oraz obsługuje HistorySplit, HistoryMerge, HistoryErase, ParadoxHistory i AbsoluteHistory.[file:2]
Dokument wskazuje, że świat GrimReich ma wiele wersji historii, z których każda może stać się główną, co czyni historię pełnoprawnym bytem systemowym.[file:2]

### Tasklista

- Zaimplementować `HistoryEngine` i `Timeline`.[file:2]
- Zaimplementować `HistorySplit`, `HistoryMerge`, `HistoryErase`.[file:2]
- Zaimplementować `ParadoxHistory`, `AbsoluteHistory`, `TimeLoop`.[file:2]
- Zdefiniować model prawdy lokalnej i globalnej.[file:2]
- Zaimplementować resolver konfliktów między historiami aktywnymi.[file:2]

### Ticki wykonawcze

1. Utworzyć model timeline’u.[file:2]
2. Dodać registry aktywnych historii.[file:2]
3. Zaimplementować split historii.[file:2]
4. Zaimplementować merge historii.[file:2]
5. Zaimplementować erase historii.[file:2]
6. Zaimplementować pętle i paradoksy czasu.[file:2]
7. Dodać integrację historii z NPC i regionami.[file:2]
8. Dodać testy spójności save/load przy wielu historiach.[file:2]

## Program 8 — Mutation Engine 2.0

### Cel

Mutation Engine 2.0 obejmuje mutacje fizyczne, emocjonalne, pamięciowe, tożsamościowe, historyczne i absolutne, a dokument wskazuje też, że mutacje mogą dotyczyć regionów, fenomenów, Collapse i Absolutu.[file:2]
Oznacza to, że silnik mutacji nie może być tylko systemem statusów, ale musi być pełnym pipeline’em transformacji ontologicznych.[file:2]

### Tasklista

- Zaimplementować `MutationEngine` i jego kategorie.[file:2]
- Zaimplementować propagację mutacji między modułami.[file:2]
- Rozdzielić mutacje zwykłe i absolutne.[file:2]
- Zdefiniować reguły dziedziczenia, eskalacji i wygaszania mutacji.[file:2]
- Zintegrować mutacje z fenomenami, historią, regionami, NPC i Collapse.[file:2]

### Ticki wykonawcze

1. Utworzyć model `MutationEngine`.[file:2]
2. Utworzyć klasy mutacji dla wszystkich kategorii.[file:2]
3. Dodać registry aktywnych mutacji.[file:2]
4. Dodać pipeline nakładania mutacji.[file:2]
5. Dodać pipeline cofania i wygaszania mutacji.[file:2]
6. Dodać obsługę mutacji absolutnych.[file:2]
7. Dodać testy propagacji mutacji przez kilka ticków świata.[file:2]

## Program 9 — Region AbsoluteVersion

### Cel

Region AbsoluteVersion jest według dokumentu regionem, który istniał przed światem, posiada cztery warstwy geometrii: Mist, Chaos, Zero, Absolute, własne mutacje absolutne i zachowania AbsoluteObserver, AbsoluteMirror, AbsoluteEraser, AbsoluteCreator oraz AbsoluteJudge.[file:2]
Jest to źródłowy moduł świata i powinien zostać wdrożony jako osobna warstwa nadrzędna nad zwykłymi regionami.[file:2]

### Tasklista

- Zaimplementować `RegionAbsoluteVersion`.[file:2]
- Zaimplementować warstwy geometrii absolutnej.[file:2]
- Zaimplementować mutacje absolutne: FormlessShift, MemoryBloom, AntiTimePulse, IdentitySplit, LyssaEcho.[file:2]
- Zaimplementować pięć zachowań absolutnych.[file:2]
- Zintegrować region absolutny z historią, NPC, Lyssą i Collapse.[file:2]

### Ticki wykonawcze

1. Utworzyć model `RegionAbsoluteVersion`.[file:2]
2. Dodać `AbsoluteGeometry`.[file:2]
3. Dodać `AbsoluteMutations`.[file:2]
4. Zaimplementować `AbsoluteObserver`.[file:2]
5. Zaimplementować `AbsoluteMirror`.[file:2]
6. Zaimplementować `AbsoluteEraser`.[file:2]
7. Zaimplementować `AbsoluteCreator`.[file:2]
8. Zaimplementować `AbsoluteJudge`.[file:2]
9. Dodać testy wpływu regionu absolutnego na zwykłe regiony.[file:2]

## Program 10 — Lyssa AbsoluteVersion

### Cel

Lyssa posiada cztery wersje: MistVersion, ChaosVersion, ZeroVersion i AbsoluteVersion, jest pierwszą Mutacją Absolutną, osią Collapse i katalizatorem scenariusza Mist → Chaos → Zero.[file:2]
Z tego powodu implementacja Lyssy musi być połączona z historią, mutacjami, Collapse i warstwą absolutną, a nie wyłącznie z systemem NPC.[file:2]

### Tasklista

- Zaimplementować model `Lyssa` z czterema wersjami.[file:2]
- Zaimplementować mutacje DreamLeak, FormShift i NameFade.[file:2]
- Zaimplementować wpływ emocji Lyssy na świat.[file:2]
- Zaimplementować sprzężenie Lyssy z osią scenariusza Mist → Chaos → Zero.[file:2]
- Zaimplementować integrację z Region AbsoluteVersion i Absolutem.[file:2]

### Ticki wykonawcze

1. Utworzyć model `LyssaVersions`.[file:2]
2. Dodać wszystkie cztery wersje Lyssy.[file:2]
3. Dodać unikalne mutacje Lyssy.[file:2]
4. Dodać wpływ emocji Lyssy na Collapse i regiony.[file:2]
5. Dodać testy przejść między wersjami Lyssy.[file:2]

## Program 11 — Other Side Layers 2.0

### Cel

Other Side obejmuje warstwy MistSide, BloodSide, ReflectionSide, FullnessSide, ChaosSide, ZeroSide i AbsoluteSide i reprezentuje prawdziwe formy fenomenów.[file:2]
W 2.0 nie może to być wyłącznie warstwa lore; musi mieć własną reprezentację systemową i wpływ na świat główny.[file:2]

### Tasklista

- Zaimplementować model `OtherSide`.[file:2]
- Dodać osobne warstwy dla wszystkich fenomenów.[file:2]
- Zdefiniować reguły przejść między światem głównym a Other Side.[file:2]
- Zintegrować Other Side z historią, mutacjami i fenomenami.[file:2]

### Ticki wykonawcze

1. Utworzyć model `OtherSide`.[file:2]
2. Dodać wszystkie warstwy Other Side.[file:2]
3. Dodać resolver projekcji Other Side na świat główny.[file:2]
4. Dodać testy spójności warstw przy wielu aktywnych fenomenach.[file:2]

## Program 12 — Expedition Generator 2.0

### Cel

Expedition Generator 2.0 generuje ekspedycje typów Standard, Collapse, Identity, Paradox, Absolute i Origin oraz obsługuje ryzyka AbsoluteErase, ParadoxLoop, IdentityCollapse i AntiTimePulse.[file:2]
W 2.0 ekspedycje powinny być wynikiem aktualnego stanu symulacji, a nie tylko tabelą skryptowanych misji.[file:2]

### Tasklista

- Zaimplementować model `Expedition`.[file:2]
- Dodać wszystkie typy ekspedycji i ryzyk.[file:2]
- Powiązać generowanie ekspedycji ze stanem Collapse, historii, mutacji i warstw absolutnych.[file:2]
- Zaimplementować `collapseImpact` jako wynik ekspedycji.[file:2]

### Ticki wykonawcze

1. Utworzyć model danych `Expedition`.[file:2]
2. Dodać typy ekspedycji.[file:2]
3. Dodać ryzyka ekspedycji.[file:2]
4. Dodać generator oparty na snapshotach świata.[file:2]
5. Dodać integrację z Collapse i historią.[file:2]
6. Dodać testy różnorodności ekspedycji przy różnych seedach świata.[file:2]

## Program 13 — warstwa Absolutu i system override

### Cel

Dokument stwierdza, że Absolut może nadpisać każdy moduł, a w priorytetach systemowych zajmuje miejsce pierwsze, ponad Collapse AI i wszystkimi pozostałymi komponentami.[file:2]
Implementacja 2.0 musi więc zakończyć się wdrożeniem mechanizmu nadrzędnego override, a nie tylko kolejnym modułem obok innych.[file:2]

### Tasklista

- Zaimplementować model nadrzędnego sterowania przez Absolut.[file:2]
- Dodać reguły override dla historii, regionów, NPC, mutacji i scenariuszy.[file:2]
- Dodać priorytety rozstrzygania konfliktów zgodne z dokumentem.[file:2]
- Zaimplementować audyt źródła decyzji przy każdym world diff.[file:2]

### Ticki wykonawcze

1. Utworzyć `AbsoluteOverrideResolver`.[file:2]
2. Zdefiniować priorytety konfliktów.[file:2]
3. Dodać hooki override do wszystkich głównych modułów.[file:2]
4. Dodać śledzenie źródła nadpisania w logach świata.[file:2]
5. Dodać testy konfliktów z udziałem Absolutu.[file:2]

## Program 14 — narzędzia developerskie i operacyjne

### Cel

Ze względu na złożoność relacji między modułami potrzebne są narzędzia obserwacji stanu świata, scenariuszy, historii, mutacji i warstw absolutnych; bez tego 2.0 będzie praktycznie niemożliwe do debugowania.[file:2]
Potrzeba ta wynika bezpośrednio z tego, że dokument opisuje świat, w którym każdy element wpływa na każdy inny, a historia, mutacje i Collapse są aktywnie zmienne.[file:2]

### Tasklista

- Zbudować timeline inspector.[file:2]
- Zbudować scenario inspector.[file:2]
- Zbudować mutation inspector.[file:2]
- Zbudować region/NPC state inspector.[file:2]
- Dodać replay runner i diff viewer.[file:2]
- Dodać telemetrię kosztu ticków.[file:2]

### Ticki wykonawcze

1. Dodać logowanie per faza ticka.[file:2]
2. Dodać viewer aktywnego scenariusza i dominujących warstw Collapse.[file:2]
3. Dodać viewer aktywnych timeline’ów.[file:2]
4. Dodać viewer aktywnych mutacji.[file:2]
5. Dodać viewer stanu Lyssy i regionu absolutnego.[file:2]
6. Dodać eksport world diff do pliku debugowego.[file:2]

## Plan milestone’ów

### Milestone M1 — Foundation 2.0

Zakres obejmuje Program 0, Program 1, Program 2 i Program 3: kontrakty, model domenowy, orkiestrator i Collapse AI.[file:2]
Rezultatem tego milestone’u ma być świat, który posiada formalny model 2.0 i potrafi przechodzić między scenariuszami w deterministycznej symulacji.[file:2]

### Milestone M2 — Living World 2.0

Zakres obejmuje Program 4, Program 5, Program 6, Program 7 i Program 8: fenomeny, regiony, NPC, historię i mutacje.[file:2]
Rezultatem ma być świat, w którym regiony i NPC są pełnoprawnymi bytami wielowarstwowymi, a historia i mutacje realnie zmieniają ontologię świata.[file:2]

### Milestone M3 — Absolute Layer 2.0

Zakres obejmuje Program 9, Program 10, Program 11, Program 12, Program 13 i Program 14: region absolutny, Lyssę, Other Side, ekspedycje pełne, system override i narzędzia operacyjne.[file:2]
Rezultatem ma być pełne uruchomienie architektury opisanej w dokumencie, łącznie z nadrzędną warstwą Absolutu.[file:2]

## Kolejność commitów / sprintów

### Sprint 1

- Kontrakty 2.0
- WorldSnapshot
- TickContext
- Commit resolver
- Testy kompatybilności

### Sprint 2

- Modele domenowe
- Hierarchie fenomenów
- Hierarchie scenariuszy
- Snapshot serialization

### Sprint 3

- Scheduler świata
- Event queue
- Replay mode
- World diff logging

### Sprint 4

- CollapseAI
- CollapseScenario2
- Graf przejść
- VectorShift
- CollapsePulse

### Sprint 5

- Phenomena Engine
- Wpływ fenomenów na region/NPC/historię
- Konflikty fenomenów

### Sprint 6

- RegionAdvancedAI
- Region loops
- Behavior roles
- Integracja z Collapse

### Sprint 7

- NPCAdvancedAI
- Pamięć wielowersyjna
- IdentitySplit
- MemoryEcho

### Sprint 8

- HistoryEngine
- Timeline split/merge/erase
- ParadoxHistory
- Save/load consistency

### Sprint 9

- MutationEngine
- Kategorie mutacji
- Propagacja mutacji
- Mutacje absolutne

### Sprint 10

- RegionAbsoluteVersion
- Zachowania absolutne
- Integracja z historią i regionami

### Sprint 11

- Lyssa
- Wersje Lyssy
- Emocjonalny wpływ na świat
- Oś Mist → Chaos → Zero

### Sprint 12

- OtherSide
- Warstwy fenomenów
- Projekcja na świat główny

### Sprint 13

- Expedition Generator 2.0
- Typy ekspedycji
- Ryzyka
- Collapse impact

### Sprint 14

- AbsoluteOverrideResolver
- Priorytety konfliktów
- Hooki override

### Sprint 15

- Tooling
- Inspectory
- Replay viewer
- Telemetria ticków

## Definition of Done

2.0 można uznać za ukończone dopiero wtedy, gdy świat działa jako jedna żywa symulacja synchronizująca NPC Loops, Region Loops, Collapse Loops, History Engine, Mutation Engine i Phenomena Engine.[file:2]
Dodatkowo Collapse musi przełączać scenariusze zgodnie z pełnym modelem warstw i przejść, NPC muszą mieć wiele wersji siebie, regiony muszą posiadać własne zachowania i historię, a Region AbsoluteVersion, Lyssa i Absolut muszą być w stanie nadpisywać niższe warstwy świata zgodnie z priorytetami systemowymi.[file:2]
Other Side musi istnieć jako realna warstwa symulacyjna, a nie tylko element opisu lore.[file:2]

## Najważniejsze ryzyka

Największe ryzyko wynika z założenia dokumentu, że każdy element wpływa na każdy inny, co może prowadzić do cyklicznych zależności i trudnych do odtworzenia błędów runtime.[file:2]
Drugie ryzyko to koszt wydajnościowy, ponieważ pełne pętle NPC, regionów, historii i mutacji mogą być zbyt ciężkie dla mobilnego runtime bez agregacji i ograniczeń symulacji poza aktywnym obszarem.[file:2]
Trzecie ryzyko dotyczy spójności historii i save/load, ponieważ ParadoxHistory, AbsoluteHistory i mutacje absolutne mogą destabilizować stan gry, jeśli nie będą wspierane przez deterministyczny commit i replay.[file:2]

## Zalecenia końcowe

Najbezpieczniejsza implementacja 2.0 powinna opierać się na zasadzie, że moduły obliczają własne intencje, ale nie zapisują bezpośrednio canonical state świata; zapis powinien przechodzić przez centralny resolver i commit pipeline.[file:2]
W praktyce to jedna decyzja architektoniczna najbardziej ogranicza chaos integracyjny wynikający z zależności NPC ↔ Region, Region ↔ Collapse, NPC ↔ Collapse i nadrzędności Absolutu nad wszystkimi modułami.[file:2]
Dzięki temu 2.0 pozostanie rozszerzalne, testowalne i możliwe do zdebugowania mimo skali systemów opisanych w GrimReich 2.0.[file:2]