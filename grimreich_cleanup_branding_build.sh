#!/usr/bin/env bash
set -euo pipefail
clear || true

if [ ! -f "settings.gradle" ] || [ ! -d "app" ]; then
  echo "Uruchom z korzenia repo Androida."
  exit 1
fi

echo "== GrimReich cleanup + branding + build =="

rm -rf app/src/main/java/com/darklandsmobile
rm -rf app/src/test/java/com/darklandsmobile
rm -rf app/src/androidTest/java/com/darklandsmobile
rm -f app/src/main/res/layout/activity_baphomet.xml
rm -f Roadmap.md COMPARISON.md FEATURE_GAPS_AND_50SPRINTS.md
rm -f apply_grimreich_patch.sh apply_grimreich_migration_v3.sh apply_grimreich_fix_v4.sh apply_grimreich_allinone_v5.sh
find . -maxdepth 1 -type d \( -name 'grimreich_backup_*' -o -name '*backup*' \) -exec rm -rf {} + 2>/dev/null || true
find . -type d \( -path '*/build' -o -path '*/.gradle' \) -prune -exec rm -rf {} + 2>/dev/null || true

fix_file() {
  local f="$1"
  [ -f "$f" ] || return 0
  python3 - "$f" <<'PY2'
from pathlib import Path
import sys
p = Path(sys.argv[1])
text = p.read_text(encoding='utf-8')
repls = [
    ('Theme.Grimreich', 'Theme.GrimReich'),
    ('Grimreich 1.0', 'GrimReich 1.0'),
    ('Bootstrap Grimreich', 'Bootstrap GrimReich'),
    ('Grimreich to mobilna gra RPG', 'GrimReich to mobilna gra RPG'),
    ('krainę Grimreich', 'krainę GrimReich'),
    ('pokój Grimreich.', 'pokój GrimReich.'),
    ('lecz Grimreich pozostał poraniony.', 'lecz GrimReich pozostał poraniony.'),
    ('Grimreich pochłonął mrok.', 'GrimReich pochłonął mrok.'),
    ('Finał Grimreich', 'Finał GrimReich'),
    ('# Grimreich', '# GrimReich'),
    ('BUILDING.md — Grimreich', 'BUILDING.md — GrimReich'),
    ('android:theme="@style/Theme.Grimreich"', 'android:theme="@style/Theme.GrimReich"'),
    ('<style name="Theme.Grimreich"', '<style name="Theme.GrimReich"'),
    ('Generate GrimReich Icons', 'Generate GrimReich Icons'),
    ('assets: GrimReich icons generated via script', 'assets: GrimReich icons generated via script'),
]
for a,b in repls:
    text = text.replace(a,b)
p.write_text(text, encoding='utf-8')
PY2
}

files=(
  "app/src/main/AndroidManifest.xml"
  "app/src/main/res/values/themes.xml"
  "app/src/main/res/values-night/themes.xml"
  "app/src/main/res/values/strings.xml"
  "app/src/main/java/com/grimreich/ui/MainActivity.kt"
  "app/src/main/java/com/grimreich/systems/EndingSystem.kt"
  "README.md"
  "BUILDING.md"
  "CONTRIBUTING.md"
  ".github/workflows/generate-icons.yml"
)
for f in "${files[@]}"; do
  fix_file "$f"
done

echo
echo "== Legacy/branding grep =="
(grep -RInE 'Darklands|darklandsmobile|Baphomet|magdeburg|Magdeburg|Theme\.Grimreich|Grimreich' .   --exclude-dir=.git   --exclude-dir=.gradle   --exclude-dir=build   --exclude=apply-manually.txt   --exclude=cleanup_and_build.sh   --exclude=fix_grimreich_branding.sh | head -n 240) || true

echo
echo "== Gradle build =="
./gradlew clean assembleDebug
