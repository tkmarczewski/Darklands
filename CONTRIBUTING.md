# CONTRIBUTING — Darklands Mobile

Instrukcja dla deweloperów pracujących nad projektem Darklands Mobile.

---

## 1. Konwencje Kodowania

### 1.1 Język i Kodowanie
- **Kod:** Kotlin 1.9.22+
- **Komentarze i dokumentacja:** Polish (PL) lub English — wybieraj spójnie w pliku
- **Encoding:** UTF-8
- **Line Endings:** LF (Unix)

### 1.2 Pakiety i Struktura
```
app/src/main/java/com/darklandsmobile/
├── core/              # Logika gry (walka, alchemia, skillie, postaci, religia, reputacja)
├── world/             # Mapa, lokacje, travel, terrain
├── content/           # Katalogi danych (miasta, święci, kariery, bestiariusz, questy)
├── systems/           # Systemy aplikacyjne (ekonomia, eventy, religion, inventory)
├── ui/                # Android Activities i bindings
└── MainActivity.kt    # Entry point aplikacji

app/src/test/java/com/darklandsmobile/
├── core/              # Testy unit dla core logiki
├── systems/           # Testy unit dla systemów
├── TestSupport.kt     # Pomocniki (reset GameRepository, itp.)
└── (world/)           # Testy dla world package'a
```

### 1.3 Nazewnictwo Klas
- **Data classes:** `SaintCatalogue`, `CityCatalogue`, `Hero`, `Item`, itp.
- **Systems (objecty):** `CombatSystem`, `ReligionSystem`, `EconomySystem`, itp.
- **Activities (UI):** `MainActivity`, `CityActivity`, `PrayerActivity`, itp.
- **Tests:** `SaintCatalogueTest`, `CombatSystemTest`, itp. (sufiks `Test`)

### 1.4 Zmienne i Stałe
```kotlin
// Zmienne lokalne: camelCase
val currentHero = party.first()
var healthPoints = 100

// Stałe: UPPER_SNAKE_CASE
const val MAX_PARTY_SIZE = 4
const val STARTING_GOLD = 50
```

---

## 2. TODO Konwencje

Używaj następujących tagów TODO do śledzenia pracy:

```kotlin
// TODO[city] — brakujący kod lub data dla miasta (Sprint 4–10)
// Przykład: TODO[city] Add Cologne to CityCatalogue with local events

// TODO[saint] — brakujący kod dla świętych (Sprint 11–16)
// Przykład: TODO[saint] Implement power: teleport to nearest city gate

// TODO[career] — brakujący kod dla karier (Sprint 17–21)
// Przykład: TODO[career] Add Religious career chain: Novice→Monk→Priest

// TODO[bestiary] — brakujący kod dla wrogów (Sprint 32–34)
// Przykład: TODO[bestiary] Add Werewolf type with nighttime bonuses

// TODO[quest] — brakujący kod dla questów (Sprint 26–31)
// Przykład: TODO[quest] Implement Witch Hunt quest chain structure

// TODO[rumor] — brakujący kod dla plotek (Sprint 22–23)
// Przykład: TODO[rumor] Connect rumors to RumorSystem

// TODO[economy] — brakujący kod dla ekonomii (Sprint 39–42)
// Przykład: TODO[economy] Implement price modifier for reputation

// TODO[ui] — brakujący UI lub responsywność (Sprint 47–50)
// Przykład: TODO[ui] Add loading screen for character creation

// TODO[ai] — brakujący kod dla AI (Sprint 35–38)
// Przykład: TODO[ai] Implement archer line-of-sight pathfinding
```

### 2.1 Szukanie TODO'ów
```bash
# Wszystkie TODO'y
grep -r "TODO\[" app/src/

# Tylko dla miast
grep -r "TODO\[city\]" app/src/

# Tylko dla questów
grep -r "TODO\[quest\]" app/src/
```

---

## 3. Formaty Danych

### 3.1 Miasta — `CityCatalogue`

```kotlin
data class CityData(
    val id: String,                    // Unique: "cologne", "prague", itp.
    val name: String,                  // "Köln", "Praha", itp.
    val region: String,                // "rhineland", "bohemia", itp.
    val type: CityType,                // METROPOLIS, CITY, TOWN, VILLAGE
    val population: Int,               // ~5000–500000
    val priceModifier: Float = 1.0f,   // 0.8–1.5 (wpływ na ceny)
    val saints: List<String> = emptyList(),  // ID świętych lokalnych (np. ["st_wenceslas"])
    val factions: Map<String, Int> = emptyMap(), // faction -> base reputation (Knights, Merchants, Church, People)
    val events: List<String> = emptyList()       // event ID'y dostępne w mieście
)

enum class CityType { METROPOLIS, CITY, TOWN, VILLAGE }
```

**Przykład:**
```kotlin
CityData(
    id = "cologne",
    name = "Köln",
    region = "rhineland",
    type = CityType.METROPOLIS,
    population = 40000,
    priceModifier = 1.1f,
    saints = listOf("st_kolumban"),
    factions = mapOf("knights" to 0, "merchants" to 10, "church" to 5, "people" to 0),
    events = listOf("event_guild_conflict", "event_pilgrims")
)
```

### 3.2 Święci — `SaintCatalogue`

```kotlin
data class SaintData(
    val id: String,                    // Unique: "st_george", "st_nicholas", itp.
    val name: String,                  // "Św. Jerzy", "Св. Николай"
    val domain: String,                // "Walka z potworami", "Podróż", itp.
    val patronage: String,             // "Żołnierze, dragonslayers" — opisowe
    val bonuses: Map<String, Int> = emptyMap(), // "strength" → 2, "courage" → 1, itp.
    val requirements: SaintRequirements? = null, // Warunki modlitwy
    val power: SaintPower? = null,    // Specjalna moc (nullable — nie ma wszystkie)
    val localBonusInCities: List<String> = emptyList() // Miasta gdzie św. jest silniejszy
)

data class SaintRequirements(
    val minFaith: Int = 0,
    val minVirtue: Int = 0,
    val maxSins: Int = 100
)

data class SaintPower(
    val name: String,                  // "Teleport to city gate"
    val description: String,
    val faithCost: Int,
    val cooldownDays: Int = 0,
    val effect: String                 // Kod efektu lub opis
)
```

**Przykład:**
```kotlin
SaintData(
    id = "st_george",
    name = "Św. Jerzy",
    domain = "Walka z potworami",
    patronage = "Żołnierze, smoki",
    bonuses = mapOf("strength" to 2, "combat" to 1),
    requirements = SaintRequirements(minFaith = 20),
    power = SaintPower(
        name = "Dragon Slayer",
        description = "Demony w następnej walce -25% dmg",
        faithCost = 20,
        cooldownDays = 7,
        effect = "demon_weakness"
    ),
    localBonusInCities = listOf("prague", "krakow")
)
```

### 3.3 Kariery — `CareerChain`

```kotlin
data class CareerData(
    val id: String,                    // "recruit", "soldier", "veteran", itp.
    val name: String,                  // "Rekrut", "Żołnierz", itp.
    val group: CareerGroup,            // MILITARY, CIVIL, RELIGIOUS, ACADEMICS, TRADES, UNDERWORLD
    val description: String,
    val requirements: CareerRequirements? = null,
    val effects: CareerEffects,        // Bonusy do atrybutów, skillów
    val nextCareers: List<String> = emptyList() // Możliwe przejścia (career ID'y)
)

enum class CareerGroup { MILITARY, CIVIL, RELIGIOUS, ACADEMICS, TRADES, UNDERWORLD }

data class CareerRequirements(
    val minAge: Int = 15,
    val maxAge: Int? = null,
    val minStrength: Int = 0,
    val minAgility: Int = 0,
    val minIntellect: Int = 0,
    val backgrounds: List<SocialBackground> = emptyList(),
    val previousCareers: List<String> = emptyList()
)

data class CareerEffects(
    val attributeBonuses: Map<String, Int> = emptyMap(), // "strength" → 1, itp.
    val skillBonuses: Map<String, Int> = emptyMap(),    // "swordplay" → 3, itp.
    val reputationEffect: Map<String, Int> = emptyMap() // faction → change
)
```

**Przykład:**
```kotlin
CareerData(
    id = "soldier",
    name = "Żołnierz",
    group = CareerGroup.MILITARY,
    description = "Zawodowy żołnierz, doświadczony w walce.",
    requirements = CareerRequirements(
        minAge = 18,
        minStrength = 4,
        previousCareers = listOf("recruit")
    ),
    effects = CareerEffects(
        attributeBonuses = mapOf("strength" to 1, "constitution" to 1),
        skillBonuses = mapOf("melee" to 2, "dodge" to 1),
        reputationEffect = mapOf("knights" to 5)
    ),
    nextCareers = listOf("veteran", "sergeant")
)
```

### 3.4 Bestiary — `BestiaryAndEncounters`

```kotlin
data class EnemyType(
    val id: String,                    // "bandit", "werewolf", "demon", itp.
    val name: String,                  // "Bandyta", "Wilkołak", itp.
    val type: EnemyCategory,           // HUMANOID, MONSTER, DEMON, BOSS
    val baseStats: EnemyStats,
    val equipment: List<String> = emptyList(),    // Item ID'y
    val behaviors: List<String> = emptyList(),    // "ranged", "caster", itp.
    val specialTraits: List<String> = emptyList() // "nocturnal_bonus", itp.
)

enum class EnemyCategory { HUMANOID, MONSTER, DEMON, BOSS }

data class EnemyStats(
    val hp: Int,
    val strength: Int,
    val agility: Int,
    val intellect: Int,
    val constitution: Int,
    val armor: Int
)
```

**Przykład:**
```kotlin
EnemyType(
    id = "werewolf",
    name = "Wilkołak",
    type = EnemyCategory.MONSTER,
    baseStats = EnemyStats(hp = 60, strength = 6, agility = 5, intellect = 2, constitution = 5, armor = 2),
    equipment = listOf("claw", "hide"),
    behaviors = listOf("melee", "pack_tactics"),
    specialTraits = listOf("nocturnal_bonus", "regeneration_at_night")
)
```

### 3.5 Questy — `QuestGraph`

```kotlin
data class QuestChain(
    val id: String,                    // "witch_hunt", "dwarves_mines", itp.
    val name: String,
    val startingRegion: String,        // region gdzie się zaczyna
    val events: List<QuestEvent>,      // Sekwencja eventów
    val rewards: QuestRewards,         // Złoto, reputacja, artefakty
    val endings: List<QuestEnding>     // Możliwe zakończenia
)

data class QuestEvent(
    val id: String,
    val description: String,
    val skillChecks: Map<String, Int> = emptyMap(), // skill -> difficulty
    val nextEventIds: List<String> = emptyList(),   // Możliwe następne eventy (branching)
    val consequences: Map<String, Int> = emptyMap() // faction reputation changes, itp.
)

data class QuestRewards(
    val gold: Int = 0,
    val reputationChanges: Map<String, Int> = emptyMap(),
    val items: List<String> = emptyList(),
    val virtue: Int = 0
)

data class QuestEnding(
    val id: String,
    val description: String,
    val requirementEvents: List<String>, // Które eventy muszą być, aby ten ending
    val reputationImpact: Map<String, Int> = emptyMap()
)
```

---

## 4. Sprint Checklist Template

Każdy sprint powinien zawierać:

```markdown
# Sprint X.Y — Tytuł

## Opis
Krótki opis celu sprintu.

## Checklist (do wykonania)
- [ ] Zadanie 1
- [ ] Zadanie 2
- [ ] Zadanie 3

## Unit Tests
- [ ] Test 1: Weryfikuj X
- [ ] Test 2: Weryfikuj Y

## Test Build
- [ ] `./gradlew clean assembleDebug` — brak błędów
- [ ] APK działa na emulatorze bez crash'ów
- [ ] Funkcjonalność X jest testowalna (UI lub konsolowe)

## Notatki
- Komentarz dla przyszłości
```

---

## 5. Pull Request Workflow

1. **Utwórz branch:**
   ```bash
   git checkout -b feature/sprint-X-description
   ```

2. **Commit z TODO tagami:**
   ```bash
   git commit -m "Sprint X.Y: Add cities to CityCatalogue
   
   - Added: Cologne, Prague, Frankfurt
   - TODO[city] Connect local reputation to economy
   - UT: CityDataTest passed
   - Test Build: assembleDebug OK"
   ```

3. **Unit tests — przed pushem:**
   ```bash
   ./gradlew :app:testDebugUnitTest
   # Wszystkie testy muszą passować!
   ```

4. **Test build:**
   ```bash
   ./gradlew clean :app:assembleDebug
   # Brak błędów!
   ```

5. **Push i PR:**
   ```bash
   git push origin feature/sprint-X-description
   # Otwórz PR na GitHub → poczekaj na CI (GitHub Actions)
   ```

---

## 6. Useful Commands

```bash
# Wszystkie unit testy
./gradlew :app:testDebugUnitTest

# Konkretny test
./gradlew :app:testDebugUnitTest --tests "com.darklandsmobile.core.SaintCatalogueTest"

# Clean build (APK)
./gradlew clean :app:assembleDebug

# Zainstaluj na emulatorze
./gradlew :app:installDebug

# Uruchom aplikację
adb shell am start -n com.darklandsmobile/.ui.MainActivity

# Logi
adb logcat | grep "darklandsmobile"

# Czy jest TODO?
grep -r "TODO\[" app/src/main/

# Licz lin code'u
find app/src/main -name "*.kt" | xargs wc -l
```

---

## 7. Code Style Guidelines

### 7.1 Kotlin Style
- Max 120 characters per line
- Use expression bodies for simple functions
- Prefer `object` dla singletonów (systemów)
- Prefer `data class` dla danych (katalogi, state)

**Good:**
```kotlin
object CombatSystem {
    fun calculateDamage(attacker: Hero, defender: Hero): Int =
        attacker.strength + attacker.equippedWeapon.damage - defender.armor
}

data class Hero(
    val name: String,
    val strength: Int,
    val hp: Int
)
```

**Bad:**
```kotlin
// Nie-singleton system
class CombatSystem {
    fun calculateDamage(...) { ... }
}

// Zbyt skomplikowana logika w one-liner
val damage = (attacker.strength + attacker.equippedWeapon.damage - defender.armor).let { if (it < 1) 1 else it }
```

### 7.2 Testowanie
- Każdy nowy kod → min. 1 test
- Test name = co testować + expected behavior
- Arrange → Act → Assert (AAA pattern)

**Good:**
```kotlin
@Test
fun `pray to saint increases faith`() {
    // Arrange
    val saint = SaintCatalogue.get("st_george")!!
    val initialFaith = GameRepository.state.prayer.faith
    
    // Act
    ReligionSystem.pray(saint.id, ShrineType.CHAPEL)
    
    // Assert
    assert(GameRepository.state.prayer.faith > initialFaith)
}
```

---

## 8. Release Checklist (Sprint 50)

- [ ] Wszystkie unit testy passują
- [ ] Brak TODO[*] w kodzie (lub zaplanowane na następne sprinty)
- [ ] README.md jest aktualne
- [ ] CHANGELOG.md zawiera nowości
- [ ] Brak crash'ów w smoke test'ach
- [ ] GitHub Actions CI succeeds
- [ ] APK działa na Android 7.0+ (minSdk 24)

---

## 9. Kontakt i Pomoc

- **Issues:** GitHub Issues → opisz bug / feature request
- **Discussions:** GitHub Discussions → pytania, pomysły
- **Docs:** Patrz [README.md](README.md), [BUILDING.md](BUILDING.md), [FEATURE_GAPS_AND_50SPRINTS.md](FEATURE_GAPS_AND_50SPRINTS.md)

---

**Ostatnia aktualizacja:** 2026-05-24  
**Wersja:** ETAP 0 COMPLETE
