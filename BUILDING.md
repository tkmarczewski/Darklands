# BUILDING.md — Darklands Mobile

Instrukcja budowania, testowania i uruchamiania projektu.

## Wymagania środowiska

| Narzędzie       | Wersja                | Uwagi                                                       |
| --------------- | --------------------- | ----------------------------------------------------------- |
| JDK             | 17 (LTS)              | Projekt celuje w `JavaVersion.VERSION_17`.                  |
| Android SDK     | API 34 (compileSdk)   | + Build-Tools 34.x, Platform-Tools (adb).                   |
| Android Gradle Plugin | 8.3.0           | Pinned w `build.gradle`.                                    |
| Kotlin          | 1.9.22                | Pinned w `build.gradle`.                                    |
| Gradle          | 8.4+                  | Najszybciej przez Android Studio (wbudowany wrapper).       |
| minSdk / targetSdk | 24 / 34            | Aplikacja działa od Androida 7.0.                           |

Po pierwszym otwarciu w Android Studio (Iguana lub nowsze) IDE wygeneruje brakujący `gradlew` / `gradle/wrapper/*` automatycznie. Jeśli budujesz z CLI bez Studia, wygeneruj wrapper jednorazowo komendą:

```bash
gradle wrapper --gradle-version 8.4
```

Zmienna środowiskowa `ANDROID_HOME` (lub `ANDROID_SDK_ROOT`) musi wskazywać na lokalizację Android SDK, np.:

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
```

## Budowanie APK

```bash
# Debug APK (do testów na urządzeniu / emulatorze)
./gradlew :app:assembleDebug

# Release APK (niepodpisany - do publikacji wymaga signingConfig)
./gradlew :app:assembleRelease
```

Wynikowe APK znajdziesz w:

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
```

Pełny clean build:

```bash
./gradlew clean :app:assembleDebug
```

## Uruchamianie testów

Projekt zawiera JVM unit testy (czysty Kotlin, bez Robolectric / instrumented).

```bash
# Wszystkie unit testy
./gradlew test

# Tylko warianty debug
./gradlew :app:testDebugUnitTest

# Pojedyncza klasa
./gradlew :app:testDebugUnitTest --tests "com.darklandsmobile.systems.InventorySystemTest"

# Pojedynczy test
./gradlew :app:testDebugUnitTest --tests "com.darklandsmobile.systems.InventorySystemTest.equip puts item id into hero equipment slot"
```

Raport HTML po przebiegu:

```
app/build/reports/tests/testDebugUnitTest/index.html
```

### Uruchamianie testów bez Gradle (środowiska CI bez Android SDK)

Testy są czystym kodem JVM — można je odpalić samym `kotlinc` + `java`, bez Android SDK. Wymaga to Kotlina 1.9+ i JDK 17.

```bash
# 1. Skompiluj źródła "core" + "systems" + "world" + testy do build/test-classes/
ALL_SRC=$(find app/src/main/java/com/darklandsmobile/core \
              app/src/main/java/com/darklandsmobile/systems \
              app/src/main/java/com/darklandsmobile/world \
              app/src/test/java -name "*.kt")
kotlinc -jvm-target 17 -no-reflect \
  -cp /path/to/junit-4.13.2.jar:/path/to/hamcrest-core.jar \
  $ALL_SRC \
  -d build/test-classes

# 2. Odpal JUnit4
TESTS=$(cd build/test-classes && find . -name "*Test.class" -not -path '*/$*' \
        | sed 's|^\./||; s|\.class$||; s|/|.|g')
java -cp "build/test-classes:/path/to/junit-4.13.2.jar:/path/to/hamcrest-core.jar:/path/to/kotlin-stdlib.jar" \
     org.junit.runner.JUnitCore $TESTS
```

Klasy z pakietu `com.darklandsmobile.ui.*` zależą od Android SDK i nie kompilują się tą ścieżką — to oczekiwane. Logika domeny (`core`, `systems`, `world`) jest niezależna od Androida i to ona jest pokryta testami.

## Uruchomienie aplikacji

### Na podłączonym urządzeniu lub emulatorze przez adb

```bash
# Sprawdz urzadzenia
adb devices

# Zbuduj + zainstaluj debug
./gradlew :app:installDebug

# Lub recznie po assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uruchom MainActivity
adb shell am start -n com.darklandsmobile/.ui.MainActivity
```

### Z Android Studio

1. Otwórz katalog projektu (`File → Open`).
2. Poczekaj na zakończenie synchronizacji Gradle.
3. Wybierz konfigurację `app`, urządzenie docelowe (emulator AVD lub fizyczne).
4. Kliknij **Run** (`Shift+F10`).

### Tworzenie emulatora z CLI

```bash
sdkmanager "system-images;android-34;google_apis;x86_64"
avdmanager create avd -n Pixel_API34 -k "system-images;android-34;google_apis;x86_64" -d pixel
emulator -avd Pixel_API34 &
adb wait-for-device
```

## Najczęstsze problemy

- **`SDK location not found`** — ustaw `ANDROID_HOME` albo dodaj `sdk.dir=/sciezka/do/Android/Sdk` w `local.properties` (plik nie commitowany).
- **`Cannot resolve symbol R`** — zsynchronizuj Gradle (`./gradlew clean` lub `File → Sync Project` w Studio).
- **Niezgodność JDK** — wymuś JDK 17: `./gradlew -Dorg.gradle.java.home=/path/to/jdk-17 build`.
- **Wrapper nie istnieje (`./gradlew: No such file`)** — patrz wyżej `gradle wrapper --gradle-version 8.4`.
- **`Execution failed for task ':app:lintReport'`** — projekt nie korzysta z lint w CI; jeśli przeszkadza: `./gradlew assembleDebug -x lint`.
- **Testy uruchomione, ale brak rezultatów** — sprawdź `app/build/reports/tests/`, czasem `--info` lub `--stacktrace` na komendzie Gradle pokazuje przyczynę.

## CI (GitHub Actions)

Workflow `.github/workflows/android-ci.yml` odpala się automatycznie:

- na każdy `push` do gałęzi `master`,
- na każdy `pull_request` skierowany do `master`.

Co robi workflow (job `build-and-test` na `ubuntu-latest`):

1. Checkout repo (`actions/checkout@v4`).
2. JDK 17 Temurin (`actions/setup-java@v4`).
3. Android SDK (`android-actions/setup-android@v3`).
4. Gradle 8.4 (`gradle/actions/setup-gradle@v3`) + wygenerowanie wrappera, jeśli go nie ma w repo.
5. Unit testy: `./gradlew :app:testDebugUnitTest`.
6. Build debug APK: `./gradlew :app:assembleDebug`.

Artefakty po przebiegu (zakładka **Actions → workflow run → Artifacts**):

- `unit-test-report` — raport HTML z `app/build/reports/tests/testDebugUnitTest` (uploadowany zawsze, również przy czerwonym buildzie).
- `app-debug-apk` — APK z `app/build/outputs/apk/debug/*.apk` (uploadowany przy udanym buildzie).

## Struktura testów

```
app/src/test/java/com/darklandsmobile/
├── TestSupport.kt                 -- helper resetujacy GameRepository
├── core/
│   ├── AlchemyTest.kt
│   ├── CareerChainTest.kt
│   ├── EquipmentTest.kt
│   ├── FactionReputationTest.kt
│   ├── MoraleAndCombatTest.kt
│   ├── SaveSystemTest.kt
│   ├── SeasonAndTimeTest.kt
│   ├── SkillSystemTest.kt
│   └── WorldMapAndBootstrapTest.kt
└── systems/
    ├── BossBattleSystemTest.kt
    ├── CityEventSystemTest.kt
    ├── EconomySystemTest.kt
    ├── EndingSystemTest.kt
    ├── InventorySystemTest.kt
    ├── QuestSystemTest.kt
    ├── ReligionSystemTest.kt
    ├── ReputationSystemTest.kt
    └── TravelSystemTest.kt
```
