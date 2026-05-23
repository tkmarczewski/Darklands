# Darklands Mobile vs Darklands (1992) - Porównanie

Data analizy: 2026-05-23

## Podsumowanie wykonawcze

Darklands Mobile to mobilna implementacja klasycznego RPG z 1992 roku. Obecna wersja zawiera solidny fundament mechanik, ale brakuje wielu elementów zawartości z oryginału.

### Statystyki kompletności:
- **Miasta**: 1/90+ (1%)
- **Święci**: 5/113 (4%)
- **Kariery**: 8/37 (22%)
- **Systemy mechaniczne**: ~70% kompletne

---

## 1. Mapa świata i miasta

### Co jest zaimplementowane ✅
- `WorldMap.kt` z systemem nodeów
- 1 miasto: **Magdeburg** (z 8 dzielnicami)
- 4 dodatkowe lokacje: 2 trakty + 2 lasy
- System dzielnicy: `CityDistrict` z 8 typami (Market, Church, Inn, Blacksmith, Alchemist, Guildhall, Castle, Slums)

### Czego brakuje ❌

Oryginalny Darklands ma **90+ miast** w Cesarstwie Rzymskim Narodu Niemieckiego, w tym:

#### Ważne miasta (priorytet wysoki):
- Köln (Kolonia)
- Nürnberg (Norymberga)
- Frankfurt
- Praha (Praga)
- Lübeck (Lubeka)
- Hamburg
- Wien (Wiedeń)
- Breslau (Wrocław)
- Augsburg
- Dresden (Drezno)

#### Średnie miasta (priorytet średni):
- Leipzig
- Erfurt
- Mainz
- Basel
- Strassburg
- Münster
- Bremen
- Danzig (Gdańsk)
- Trier
- Würzburg

### Co należy dodać:
1. Minimum 10-15 głównych miast
2. Każde miasto powinno mieć unikalne cechy:
   - Specjalne dzielnice (uniwersytety, porty)
   - Lokalne eventy
   - Modyfikatory reputacji regionalnej
3. Większa sieć dróg i lokacji dzikich

---

## 2. System religii i świętych

### Co jest zaimplementowane ✅
- `Religion.kt` z systemem modlitwy
- `SaintCatalogue` z 5 świętymi:
  - Archanioł Michał (War)
  - Archanioł Rafał (Healing)
  - Santo Tomasz (Wisdom)
  - Najdroższa Maria Panna (Mercy)
  - Jerzy Męczennik (Justice)
- System DivineFavor i Virtue
- VirtueSystem z mechaniką grzechu

### Czego brakuje ❌

Oryginalny Darklands ma **113 świętych** z pełnymi profilami! Każdy święty ma:
- Statystyki do poprawy (Str, End, Int, Per, Agi, Chr)
- Umiejętności do poprawy
- Specjalne moce (np. levitacja, teleportacja, oczyszczanie zła)
- Wymagania (min. DivineFavor, Virtue)

#### Kluczowi święci do dodania (priorytet wysoki):
- **Św. Rafał** - leczy 100% End/Str, +Heal (50-99)
- **Św. Piotr** - +Str (12-19), +Chr, +weapon skills
- **Św. Paweł** - +Int (12-23), +R&W (15-29)
- **Św. Jerzy** - +Str (10-14), +weapon skill (25-49), +Ride (25-49)
- **Św. Mikołaj** - +End, +Chr, +WFll, +Local Rep
- **Św. Barbara** - +Artf (15-29), +WMsD (20-39)
- **Św. Franciszek** - leczy End 30%/Str 10%, +Chr, +Heal
- **Św. Cecylia** - +Local Rep (20-60) jeśli postawa instrument
- **Św. Krzysztof** - +WdWs (20-39), +Ride (25-74)
- **Św. Adrian** - +End (7-15), +weapon skills (15-29)

#### Specjalne moce do zaimplementowania:
- **Teleportacja** (Gertrude, Vitus) - do przedmieścia miasta
- **Przekraczanie wody** (Finnian, Julian, Raymond) - przez rzeki/jeziora
- **Levitacja** (Christina) - nad przeszkodami
- **Oczyszczanie zła** (Boniface, Emydius) - w lochach/katedrach
- **Osłabianie demonów** (Anthony, Cyprian, Dymphna) - przed walką
- **Leczenie zarazy** (Roch, Sebastian)
- **Unikanie zasadzek** (Drogo, Genevieve, Godfrey)

### Co należy dodać:
1. Minimum 30-40 dodatkowych świętych
2. System specjalnych mocy świętych
3. Zależności geograficzne (bonus do reputacji w określonych regionach)

---

## 3. System karier

### Co jest zaimplementowane ✅
- `CareerChain.kt` z 8 karierami:
  - Paź (Page)
  - Giermek (Squire)
  - Rycerz (Knight)
  - Najemnik (Mercenary)
  - Uczony (Scholar)
  - Mnich (Monk)
  - Złodziej (Thief)
  - Alchemik (Alchemist)
- System wymagań (atrybuty, wiek, poprzednie kariery)
- Bonusy statystyk dla każdej kariery

### Czego brakuje ❌

Oryginalny Darklands ma **37 karier** w 6 kategoriach:

#### Military Service (6 karier):
- ✅ Recruit (częściowo: Najemnik)
- Soldier
- Veteran
- Captain
- ✅ Knight (Rycerz)
- Hunter

#### Civil Service (6 karier):
- Peasant
- Laborer
- Schulz
- Courtier
- Noble Heir
- Manorial Lord

#### Religious Occupations (7 karier):
- Hermit
- Novice
- ✅ Monk (Mnich)
- Friar
- Priest
- Abbot
- Bishop

#### Academics (7 karier):
- Student
- Oblate
- Clerk
- Physician
- ✅ Alchemist (Alchemik)
- Professor
- Master Alchemist

#### Trades (7 karier):
- Peddler
- Local Trader
- Travelling Merchant
- Merchant-Proprietor
- Apprentice Craftsman
- Journeyman Craftsman
- Master Craftsman

#### Underworld (4 kariery):
- Bandit
- ✅ Thief (Złodziej)
- Vagabond
- Swindler

### Co należy dodać:
1. Kompletne łańcuchy karier dla każdej kategorii
2. Unikalne bonusy i wymagania dla każdej kariery
3. System tła społecznego (Nobility, Wealthy Urban, Town Trades, Country Crafts, Urban Commoners, Rural Commoners)

---

## 4. Systemy mechaniczne

### Co jest zaimplementowane ✅

#### Combat & Combat State:
- `Combat.kt` - MoraleSystem, WoundType, CombatRound
- Morale z 5 statusami (Heroic, Steady, Shaken, Panicked, Routed)
- System ran (Light, Serious, Critical)
- Modyfikatory ataku/obrony według morale
- Post-combat recovery

#### Alchemy:
- `Alchemy.kt` - 22 mikstury
- 15 składników alchemicznych
- 3 kategorie: offensive, buff, healing
- System jakości i quality points

#### Equipment:
- `Equipment.kt` z ItemQuality enum
- WeaponQualitySystem + ArmorQualitySystem
- Encumbrance (4 poziomy ciężaru)

#### Characters:
- `Hero.kt` - 7 atrybutów, age, virtue, divineFavor
- `HeroSkills.kt` - **18 umiejętności** (kompletne!)
- `AgingSystem.kt` - efekty starzenia, degradacja
- `CharacterFactory.kt` - templates (Knight, Scholar, Merchant, Rogue)

#### Quests:
- `QuestGraph.kt` - graf zadań z node'ami
- `QuestChains.kt` - 2 łańcuchy (Raubritter, Endgame/Kult Baphometa)
- `QuestState.kt` - śledzenie postępu

#### Faction & Reputation:
- `FactionReputation.kt` - 4 frakcje (Knights, Merchants, Church, Commoners)
- `ReputationState.kt` - per-city reputation
- Price modifiers

#### Events:
- `EventCatalog.kt` - CityEventCatalog (8 eventów)
- `DungeonEventCatalog.kt` - 9 eventów (połapki, nieumarli, kultysci, skarby, więźnie, bossowie)
- `WildernessEventCatalog.kt` - wilderness events
- `EventSystem.kt` - Event/EventNode/EventOption

#### Bestiary:
- `BestiaryAndEncounters.kt` - 16 typów przeciwników
- Loot tables, AI behaviors
- 9 predefiniowanych encounterów

#### Save System:
- `SaveSystem.kt` - autosave, slots, validation, migration v3
- `SaveSnapshot.kt`

#### Travel:
- `Travel.kt` - TimeOfDay enum, DayNightSystem
- Seasons, fatigue cost, encounter system

### Co działa dobrze i nie wymaga zmian:
- ✅ System walki (morale, rany) - bardzo dobry
- ✅ Alchemia - kompletna
- ✅ Umiejętności - pełne 18
- ✅ System starzenia - działa
- ✅ Equipment quality system - OK

### Co należy poprawić / rozbudować:

#### 1. System ekonomii (brakuje!):
- Ceny przedmiotów w sklepach
- System handlu
- Wpływ reputacji na ceny
- Lokalne ceny (różnice między miastami)

#### 2. System plotek i NPC (brakuje!):
- RumorSystem - plotki w gospodach
- NPC z imionami i rolami
- Quest hooks przez plotki

#### 3. System czasu dobowego (częściowo):
- ✅ `TimeOfDay` enum istnieje
- ❌ Brak integracji z eventami miejskimi (nocne włamanie, patrol straży)
- ❌ Brak ograniczeń nocnych (zamknięte sklepy, kościoły)

#### 4. Więcej questów:
- Tylko 2 łańcuchy - potrzeba minimum 5-8
- Dodaj: czarownice, krasnoludy w kopalniach, plaga w miastach, turnieje rycerskie

#### 5. Random world generation:
- Oryginał ma "revolutionary random world generator"
- Warto dodać losowe lokacje: zamki, hamlets, klasztory

---

## 5. Priorytety implementacji

### KRYTYCZNY priorytet (następny sprint):
1. **Dodaj 10 miast** (Köln, Nürnberg, Frankfurt, Praha, Lübeck, Hamburg, Wien, Breslau, Augsburg, Dresden)
2. **Dodaj 30 świętych** (najważniejsi z listy powyżej)
3. **System ekonomii** (EconomySystem.kt - ceny, handel)

### WYSOKI priorytet (2-3 tygodnie):
4. **Rozbuduj kariery** - dodaj 20 brakujących
5. **System plotek** (RumorSystem.kt)
6. **Dodaj 3 nowe quest chainy** (czarownice, krasnoludy, plaga)
7. **Integracja czasu dobowego** z eventami miejskimi

### ŚREDNI priorytet (1-2 miesiące):
8. **Dodaj kolejne 30 świętych** (do 65 total)
9. **Dodaj kolejne 20 miast** (do 30 total)
10. **System random world generation** (losowe lokacje)
11. **Specjalne moce świętych** (teleportacja, levitacja, etc.)

### NISKI priorytet (backlog):
12. **Dodaj resztę świętych** (113 total)
13. **Dodaj resztę miast** (90+ total)
14. **Holy relics system** (relikwie w złych klasztorach)
15. **Tournament system** (turnieje rycerskie)

---

## 6. Wnioski

### Mocne strony projektu:
- **Solidny fundament mechaniczny** - systemy core działają dobrze
- **Dobra architektura** - podział na core/systems/ui jest przejrzysty
- **Kompletne systemy** - Combat, Alchemy, Skills są na poziomie oryginału

### Główne braki:
- **Skala zawartości** - 1 miasto vs 90+, 5 świętych vs 113
- **Systemy drugiego rzędu** - brak ekonomii, plotek, pełnej integracji czasu
- **Questy** - tylko 2 łańcuchy

### Rekomendacja:
Projekt jest **silnie zaawansowany mechanicznie**, ale potrzebuje **masowego dodania zawartości** (miasta, święci, kariery, questy). Sugeruję podejście iteracyjne:
1. Sprint 1: 10 miast + 30 świętych + ekonomia
2. Sprint 2: 20 karier + plotki + 3 questy
3. Sprint 3: kolejne 20 miast + 35 świętych
4. Sprints 4-6: dopędzanie do pełnej skali oryginału

---

*Dokument wygenerowany automatycznie na podstawie analizy kodu i materiałów źródłowych Darklands (1992).*
