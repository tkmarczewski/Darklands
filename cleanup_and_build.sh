#!/usr/bin/env bash
set -euo pipefail
clear || true

if [ ! -f "settings.gradle" ] || [ ! -d "app" ]; then
  echo "Uruchom z korzenia repo Androida."
  exit 1
fi

echo "== Grimreich cleanup + build =="

rm -rf app/src/main/java/com/darklandsmobile
rm -rf app/src/test/java/com/darklandsmobile
rm -rf app/src/androidTest/java/com/darklandsmobile
rm -f app/src/main/res/layout/activity_baphomet.xml
rm -f Roadmap.md COMPARISON.md FEATURE_GAPS_AND_50SPRINTS.md
rm -f apply_grimreich_patch.sh apply_grimreich_migration_v3.sh apply_grimreich_fix_v4.sh apply_grimreich_allinone_v5.sh

find . -maxdepth 1 -type d \( -name 'grimreich_backup_*' -o -name '*backup*' \) -exec rm -rf {} + 2>/dev/null || true
find . -type d \( -path '*/build' -o -path '*/.gradle' \) -prune -exec rm -rf {} + 2>/dev/null || true

echo
echo "== Legacy grep after cleanup =="
(grep -RInE 'Darklands|darklandsmobile|Baphomet|magdeburg|Magdeburg|GrimReich' .   --exclude-dir=.git   --exclude-dir=.gradle   --exclude-dir=build   --exclude=apply-manually.txt   --exclude=cleanup_and_build.sh | head -n 200) || true

echo
echo "== Gradle build =="
./gradlew clean assembleDebug
