#!/usr/bin/env bash
set -euo pipefail

echo "== Grimreich patch apply =="
ROOT="$(pwd)"
PATCH_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

if [ ! -f "settings.gradle" ] || [ ! -d "app" ]; then
  echo "Uruchom ten skrypt z korzenia repo Androida."
  exit 1
fi

mkdir -p app/src/main/java/com/grimreich/ui
mkdir -p app/src/main/java/com/grimreich/systems
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/values
mkdir -p app/src/main

cp "$PATCH_DIR/settings.gradle" settings.gradle
cp "$PATCH_DIR/app/build.gradle" app/build.gradle
cp "$PATCH_DIR/app/src/main/AndroidManifest.xml" app/src/main/AndroidManifest.xml
cp "$PATCH_DIR/app/src/main/res/values/strings.xml" app/src/main/res/values/strings.xml
cp "$PATCH_DIR/app/src/main/res/layout/activity_finale.xml" app/src/main/res/layout/activity_finale.xml
cp "$PATCH_DIR/app/src/main/res/layout/activity_hub.xml" app/src/main/res/layout/activity_hub.xml
cp "$PATCH_DIR/app/src/main/res/layout/activity_main.xml" app/src/main/res/layout/activity_main.xml
cp "$PATCH_DIR/README.md" README.md
cp "$PATCH_DIR/BUILDING.md" BUILDING.md

find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.gradle" -o -name "*.md" \) ! -path "./.git/*" | while read -r f; do
  sed -i \
    -e 's/com\.darklandsmobile/com.grimreich/g' \
    -e 's/DarklandsMobile/Grimreich/g' \
    -e 's/=== DARKLANDS MVP ===/=== GRIMREICH 1.0 ===/g' \
    -e 's/"magdeburg"/"grimhold"/g' \
    -e 's/kult Baphometa/kult Grimreich/g' \
    -e 's/Kult Baphometa/Kult Grimreich/g' \
    -e 's/Endgame\/Kult Baphometa/Endgame\/Kult Grimreich/g' \
    -e 's/Baphomet\/koniec gry/Finał Grimreich/g' \
    -e 's/BaphometActivity/FinaleActivity/g' \
    -e 's/baphometText/finaleText/g' \
    -e 's/openBaphomet/openFinale/g' \
    -e 's/activity_baphomet/activity_finale/g' \
    -e 's/Final \/ Baphomet/Finał/g' \
    -e 's/# Darklands$/# Grimreich/g' \
    -e 's/Darklands Mobile/Grimreich/g' \
    -e 's/\bDarklands\b/Grimreich/g' \
    "$f" || true
done

cp "$PATCH_DIR/app/src/main/java/com/grimreich/ui/FinaleActivity.kt" app/src/main/java/com/grimreich/ui/FinaleActivity.kt
cp "$PATCH_DIR/app/src/main/java/com/grimreich/ui/HubActivity.kt" app/src/main/java/com/grimreich/ui/HubActivity.kt
cp "$PATCH_DIR/app/src/main/java/com/grimreich/ui/MainActivity.kt" app/src/main/java/com/grimreich/ui/MainActivity.kt
cp "$PATCH_DIR/app/src/main/java/com/grimreich/systems/EndingSystem.kt" app/src/main/java/com/grimreich/systems/EndingSystem.kt

if [ -d app/src/main/java/com/darklandsmobile ] && [ ! -d app/src/main/java/com/grimreich/core ]; then
  mkdir -p app/src/main/java/com/grimreich
  cp -R app/src/main/java/com/darklandsmobile/* app/src/main/java/com/grimreich/ || true
fi
if [ -d app/src/test/java/com/darklandsmobile ] && [ ! -d app/src/test/java/com/grimreich ]; then
  mkdir -p app/src/test/java/com/grimreich
  cp -R app/src/test/java/com/darklandsmobile/* app/src/test/java/com/grimreich/ || true
fi

rm -f app/src/main/java/com/darklandsmobile/ui/BaphometActivity.kt
rm -f app/src/main/java/com/grimreich/ui/BaphometActivity.kt
rm -f app/src/main/res/layout/activity_baphomet.xml

chmod +x gradlew 2>/dev/null || true

echo
printf 'Pozostałe dopasowania legacy (max 50):\n'
(grep -RInE 'Darklands|darklandsmobile|Baphomet|magdeburg' . --exclude-dir=.git | head -n 50) || true

echo
cat <<MSG
Patch nałożony.
Następne kroki:
1) ./gradlew assembleDebug
2) jeśli build przejdzie, usuń stare katalogi com/darklandsmobile jeśli jeszcze zostały
3) opcjonalnie: git status && git add . && git commit -m "Rebrand to Grimreich"
MSG
