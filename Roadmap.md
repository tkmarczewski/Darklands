# Darklands Mobile – Plan Pracy (Sprinty)

Dokument: podział pracy na etapy i sprinty, z checklistami możliwie małymi, żeby dało się je łatwo wrzucać jako pojedyncze issue.

---

## ETAP 0 – Porządki, struktura, meta

### Sprint 0.1 – Struktura katalogów i modułów

- [ ] Przejrzeć aktualną strukturę pakietów (`app/src/main/java/...`)
- [ ] Zidentyfikować pakiety odpowiedzialne za:
  - [ ] logikę core (walka, alchemia, skille, ekwipunek, starzenie, religia, reputacja)
  - [ ] świat (mapa, travel, eventy)
  - [ ] content (miasta, święci, kariery, bestiariusz, questy)
- [ ] Utworzyć (lub potwierdzić istnienie) pakietów:
  - [ ] `core` – mechanika gry
  - [ ] `world` – mapa, lokacje, travel
  - [ ] `content` – katalogi danych (miasta, święci, kariery, bestiariusz, questy)
- [ ] Przenieść klasy do odpowiednich pakietów, jeśli wymaga tego porządek

### Sprint 0.2 – Format danych i TODO

- [ ] Wybrać format danych dla katalogów (JSON/YAML / klasy + enumy)
- [ ] Dla miast:
  - [ ] Zdefiniować strukturę danych (nazwa, region, typ, populacja/wielkość, modyfikatory)
- [ ] Dla świętych:
  - [ ] Zdefiniować strukturę danych (imię, opis, bonusy, wymagania, specjalne moce)
- [ ] Dla karier:
  - [ ] Zdefiniować strukturę danych (nazwa, grupa, wymagania, efekty)
- [ ] Dla bestiariusza:
  - [ ] Zdefiniować strukturę danych (typ wroga, statystyki, ekwipunek, zachowanie)
- [ ] Wprowadzić konwencję komentarzy TODO:
  - [ ] `// TODO[city]`
  - [ ] `// TODO[saint]`
  - [ ] `// TODO[career]`
  - [ ] `// TODO[bestiary]`
  - [ ] `// TODO[quest]`
- [ ] Dodać krótką notkę o konwencji TODO w `CONTRIBUTING` / README

---

## ETAP I – Miasta, Święci, Ekonomia

### Sprint 1.1 – Pierwsza paczka miast (Mapa świata)

Cel: z 1 miasta → 5–6 miast, prosta siatka połączeń.

- [ ] Otworzyć `WorldMap` i sprawdzić aktualną listę lokacji
- [ ] Dodać miasto: Köln
  - [ ] Wpisać do `WorldMap` (położenie / sąsiadujące węzły)
  - [ ] Wpisać do `CityCatalogue` z podstawowymi danymi
- [ ] Dodać miasto: Nürnberg
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane miasta
- [ ] Dodać miasto: Frankfurt
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane miasta
- [ ] Dodać miasto: Praha
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane miasta
- [ ] Dodać miasto: Lübeck
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane miasta
- [ ] Dodać typy terenu na mapie (min. droga / las / góry)
- [ ] Zweryfikować, że travel między nowymi miastami działa (prosty smoke test)

### Sprint 1.2 – Druga paczka miast + lokalne eventy

Cel: 10–12 miast, każdy ma choć 1 lokalny event.

- [ ] Dodać miasto: Hamburg
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane
- [ ] Dodać miasto: Wien
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane
- [ ] Dodać miasto: Breslau
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane
- [ ] Dodać miasto: Augsburg
  - [ ] `WorldMap` – połączenia
  - [ ] `CityCatalogue` – dane
- [ ] Dla każdego miasta (w tym Magdeburg):
  - [ ] Dodać min. 1 lokalny event ogólny (np. gospodarczo‑społeczny)
  - [ ] Dodać min. 1 event zależny od reputacji (np. reakcja cechu, kościoła)

### Sprint 1.3 – Święci: paczka #1 (do ~20)

Cel: z 5 świętych → do ok. 20.

- [ ] Otworzyć `SaintCatalogue` i potwierdzić aktualną listę
- [ ] Dodać świętego: Św. Jerzy
  - [ ] Bonusy (np. walka z potworami, odwaga)
  - [ ] Wymagania (łaska, cnota)
- [ ] Dodać świętego: Św. Mikołaj
  - [ ] Bonusy (np. handel, podróż)
  - [ ] Wymagania
- [ ] Dodać świętego: Św. Barbara
  - [ ] Bonusy (np. ochrona przed wybuchami/miksturami)
  - [ ] Wymagania
- [ ] Dodać świętego: Św. Rafał
- [ ] Dodać świętego: Św. Piotr
- [ ] Dodać świętego: Św. Paweł
- [ ] Dodać świętego: Św. Krzysztof
- [ ] Dodać świętego: Św. Franciszek
- [ ] Dodać świętą: Św. Cecylia
- [ ] Dodać świętego: Św. Adrian
- [ ] Przy każdym nowym świętym:
  - [ ] Zdefiniować bonusy do atrybutów/skills
  - [ ] Zdefiniować wymagania (łaska, cnota, ewentualnie region)
  - [ ] Zapisać krótką notkę w komentarzu (inspiracja z oryginału lub własna)

### Sprint 1.4 – Święci: specjalne moce + paczka #2

Cel: zacząć „większe” moce + dojść do ~35 świętych.

- [ ] Zaimplementować specjalną moc 1:
  - [ ] Teleport do bramy najbliższego miasta
  - [ ] Koszt w łasce
  - [ ] Cooldown (np. raz na X dni)
- [ ] Zaimplementować specjalną moc 2:
  - [ ] Tymczasowe pozwolenie na przejście przez rzekę / wodę
  - [ ] Koszt i cooldown
- [ ] Zaimplementować specjalną moc 3:
  - [ ] Osłabienie demonów w kolejnej walce
  - [ ] Koszt i cooldown
- [ ] Dodać kolejne ~10 świętych (aż do ok. 35)
- [ ] Dla wybranych świętych powiązać moce z regionem/miastem (silniejsze lokalnie)

### Sprint 1.5 – Ekonomia: core system

- [ ] Utworzyć klasę/system `EconomySystem`
- [ ] Zdefiniować bazowe ceny:
  - [ ] broń
  - [ ] zbroje
  - [ ] mikstury
  - [ ] jedzenie
  - [ ] noclegi/usługi
- [ ] Dodać globalny mnożnik cen zależny od poziomu trudności
- [ ] Dodać `cityPriceModifier` per miasto w `CityCatalogue`
- [ ] Zintegrować ceny z istniejącym ekwipunkiem (zakup/sprzedaż)

### Sprint 1.6 – Ekonomia: reputacja + UI

- [ ] Podłączyć reputację frakcji do cen:
  - [ ] reputacja z Kupcami → zniżki w sklepach
  - [ ] reputacja z Kościołem → tańsze usługi religijne
- [ ] Zaimplementować UI sklepu:
  - [ ] lista dostępnych przedmiotów
  - [ ] ceny kupna i sprzedaży
  - [ ] aktualne złoto drużyny
- [ ] Dodać tooltip/mini‑opis wyjaśniający wpływ reputacji na cenę
- [ ] Zrobić prosty test: różne reputacje → różne ceny w tym samym mieście

---

## ETAP II – Kariery, Plotki, Questy, Czas

### Sprint 2.1 – SocialBackground i podstawy karier

- [ ] Dodać enum `SocialBackground`:
  - [ ] Nobility
  - [ ] WealthyUrban
  - [ ] TownTrades
  - [ ] CountryCrafts
  - [ ] UrbanCommoners
  - [ ] RuralCommoners
- [ ] Dodać pole background do modelu bohatera
- [ ] Ustawić background podczas tworzenia postaci (losowo / wybór gracza)
- [ ] Przejrzeć istniejące kariery i przypisać im wymagania backgroundu (jeśli potrzebne)

### Sprint 2.2 – Kariery: Military & Civil (paczka #1)

- [ ] Spisać listę brakujących karier wojskowych i cywilnych z COMPARISON
- [ ] Dodać karierę Military 1 (np. Recruit)
- [ ] Dodać karierę Military 2 (np. Soldier)
- [ ] Dodać karierę Military 3 (np. Veteran)
- [ ] Dodać karierę Civil 1 (np. Scribe)
- [ ] Dodać karierę Civil 2 (np. Clerk)
- [ ] Dla każdej nowej kariery:
  - [ ] Zdefiniować wymagania (wiek, atrybuty, background, poprzednie kariery)
  - [ ] Zdefiniować zmiany atrybutów/skills
  - [ ] Dodać wpływ na reputację/virtue jeśli sensowne

### Sprint 2.3 – Kariery: Religious, Academics, Trades, Underworld (paczka #2)

- [ ] Spisać brakujące kariery religijne, akademickie, rzemieślnicze, underworld
- [ ] Dodać co najmniej:
  - [ ] 3 kariery Religious (np. Novice, Monk, Priest)
  - [ ] 3 kariery Academics (np. Student, Scholar, Physician)
  - [ ] 3 kariery Trades (np. Apprentice, Journeyman, Master)
  - [ ] 3 kariery Underworld (np. Thief, Smuggler, Gang Member)
- [ ] Zdefiniować dla nich wymagania i efekty (jak wyżej)
- [ ] Upewnić się, że łańcuchy kariery odzwierciedlają sensowny „życiorys”

### Sprint 2.4 – Plotki (RumorSystem)

- [ ] Utworzyć `Rumor` (data class: tekst, wiarygodność, questId, region)
- [ ] Utworzyć `RumorSystem` zarządzający dostępem do plotek
- [ ] Dodać źródła plotek:
  - [ ] karczma
  - [ ] ulice/slumsy
  - [ ] Kościół/uczeni
- [ ] Powiązać plotki z istniejącymi quest chainami (Raubritter, Endgame)
- [ ] Dodać prosty UI odbierania plotki (dialog/okno z tekstem i ewentualnym hintem)

### Sprint 2.5 – NPC (NamedNpc)

- [ ] Utworzyć typ `NamedNpc` (imię, rola, miasto, powiązany event/quest)
- [ ] Dodać NPC: karczmarz w kilku miastach
- [ ] Dodać NPC: kapłan w katedrze / kościele
- [ ] Dodać NPC: dowódca straży
- [ ] Zmodyfikować eventy miejskie, by korzystały z konkretnych NPC zamiast anonimowych opisów
- [ ] Powiązać dialogi z `RumorSystem` (NPC → plotka)

### Sprint 2.6 – Nowe quest chainy (Witch Hunt, Dwarves, Plague)

- [ ] Zaprojektować strukturę quest chain „Witch Hunt”
  - [ ] Event startowy (plotka)
  - [ ] 1–2 eventy śledcze w mieście
  - [ ] Event sabatu w lesie
  - [ ] Finał (walka / wybór moralny)
- [ ] Zaprojektować strukturę „Dwarves in Mines”
- [ ] Zaprojektować strukturę „Plague”
- [ ] Zaimplementować minimalne wersje tych chainów (bez pełnego rozbudowania dialogów)
- [ ] Podłączyć je do mapy (lokacje) i plotek (RumorSystem)

### Sprint 2.7 – Czas dobowy

- [ ] Rozszerzyć system czasu (jeśli potrzeba) o wyraźne fazy: dzień / wieczór / noc
- [ ] Wprowadzić zamykanie sklepów/kościołów nocą
- [ ] Zmienić szanse encounterów i typy wrogów według pory dnia
- [ ] Dodać eventy dostępne tylko nocą (np. włamania, sabaty)
- [ ] Ulepszyć UI:
  - [ ] widoczny wskaźnik pory dnia
  - [ ] komunikaty o godzinach otwarcia
- [ ] Powiązać wybrane moce świętych z porą dnia (np. nocne podróże)

---

## ETAP III – Skala, Losowość, Smaczki

### Sprint 3.1 – Kolejne miasta

- [ ] Dodać kolejną paczkę 5 miast (wg Twojej listy w COMPARISON)
- [ ] Wpisać je do `WorldMap` i `CityCatalogue`
- [ ] Dodać po 1–2 unikalne eventy per nowe miasto

### Sprint 3.2 – Święci: pełniejsza lista

- [ ] Dodać kolejną paczkę 15–20 świętych (do ~65)
- [ ] Dla każdego zdefiniować bonusy, wymagania, ewentualne specjalne moce
- [ ] Zadbać o różnorodność efektów (nie tylko buffy do walki)

### Sprint 3.3 – Random World Generation

- [ ] Zdefiniować template’y lokacji:
  - [ ] zamek raubrittera
  - [ ] klasztor
  - [ ] ruiny
  - [ ] hamlet / wioska
  - [ ] loch
- [ ] Napisać generator rozmieszczający te lokacje na mapie (zależnie od terenu)
- [ ] Dodać losowe przypisywanie „właścicieli” (raubritter, kult, dobry klasztor, ruiny)

### Sprint 3.4 – Relikwie, turnieje, specjalne lokacje

- [ ] Wprowadzić system „holy relics” (artefakty związane z Kościołem)
- [ ] Dodać kilka relikwii z prostymi efektami (bonusy do modlitw, reputacji)
- [ ] Zaprojektować system turniejów rycerskich:
  - [ ] wybór miasta gospodarza
  - [ ] drabinka pojedynków
  - [ ] nagrody (złoto, reputacja)
- [ ] Rozbudować finałowy łańcuch (Fortress Monastery / kult Baphometa)

---

## ETAP IV – Bestiariusz, AI, Polishing

### Sprint 4.1 – Bestiariusz: lista i pierwsze rozszerzenie

- [ ] Na podstawie wiki/ENM spisać listę ikon typów przeciwników (ludzie + potwory + demony + bossowie)
- [ ] Oznaczyć, które typy są już w grze, a które brakuje
- [ ] Dodać pierwszą paczkę brakujących wrogów do `Bestiary`:
  - [ ] statystyki
  - [ ] ekwipunek
  - [ ] typowe encountery

### Sprint 4.2 – AI: łucznicy i alchemicy

- [ ] Dodać do AI:
  - [ ] szukanie linii strzału przez łuczników (LOS)
  - [ ] logiczną zmianę pozycji, aby mieć LOS
- [ ] Dodać zachowania alchemików:
  - [ ] używanie mikstur jako ataków dystansowych
  - [ ] przejście na melee po wyczerpaniu mikstur
- [ ] Przetestować kilka przykładowych walk z nową AI

### Sprint 4.3 – AI: bossowie i demony

- [ ] Zdefiniować prosty wzorzec faz dla bossów (np. demon, smok)
- [ ] Dodać przyzwania minionów w trakcie walki
- [ ] Dodać specjalne ataki (np. AOE, strach, debuffy)
- [ ] Zbalansować walki testowe (nie za łatwo/nie za trudno)

### Sprint 4.4 – Skill‑checki i UX

- [ ] Przejść katalog eventów i oznaczyć miejsca, gdzie można użyć:
  - [ ] streetwise
  - [ ] read/write
  - [ ] alchemy
  - [ ] healing
  - [ ] innych umiejętności społecznych/technicznych
- [ ] Dodać konkretne skill‑checki w tych miejscach
- [ ] Ujednolicić komunikaty testów umiejętności (sukces/porażka)
- [ ] Dodać opcjonalny debug‑log dla testów skillowych (dla balansu)

---
