# BUILDING.md — Grimreich

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

Po pierwszym otwarciu w Android Studio (Iguana lub nowsze) IDE wygeneruje brakujący `gradlew` / `gradle/wrapper/*` automatycznie. Jeśli budujesz z CLI bez Studia, wygeneruj wrapper jednorazowo:

```bash
gradle wrapper --gradle-version 8.4
```

Zmienna środowiskowa `ANDROID_HOME` musi wskazywać na lokalizację Android SDK, np.:

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
```

## Budowanie APK

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Testy jednostkowe

```bash
./gradlew test
```

## Instalacja na emulatorze / urządzeniu

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Pakiet aplikacji

`com.grimreich` — wersja `1.0.0`.
