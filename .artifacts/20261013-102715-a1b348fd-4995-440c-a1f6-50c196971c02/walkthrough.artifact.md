# Walkthrough - Git Push & Full E2E Device Testing (No Dev Menu)

## Git Commit & Push Summary
- **Target Branch**: `master` (`origin/master`)
- **Commit**: `fc71486` - `fix(core): fix training cycle age calculation and clamp hero attributes lower bound`
- **Pushed Repository**: `https://github.com/tkmarczewski/Darklands.git`

## End-to-End Device Testing Walkthrough (Start to Finish)

1. **New Game & Identity Setup**:
   - Tapped `NOWA PRZYGODA`.
   - Entered custom player identity name (`LordChaos_99`) and verified soft keyboard dismissal.
   - Stage 1 (Profession): Selected career `Giermek`, tested random name generator (`Greta Müller`).
   - Stage 2 (Attributes): Randomized point distribution.
   - Stage 3 (Specializations): Randomized skills.
   - Stage 4 (Life Path): Added training cycles up to age 28. Finalized character creation.

2. **World Navigation & City Flow**:
   - Accessed World Map via minimap.
   - Entered city `WYBRZEŻE PÓŁNOCNE` / `ZAKON ŚWITU`.
   - Talked to NPC `Aldous` (merchant) and tested Market buying (`Krótki miecz`, `Mikstura zdrowia`) and selling.
   - Visited Tavern (`Tawerna`), recruited mercenary `Helga Schattenaxt` for 55 gold.
   - Visited Temple (`Kaplica Czystego Światła`), executed prayer (+10 Sanity, +5 Faith), and verified insufficient gold edge-case error message handling.
   - Interacted with `Thane` (guard) in multi-branch dialogue and accepted quest `q_inquisition_verdict`.

3. **Inventory & Session Persistence**:
   - Opened Inventory via expanding menu.
   - Selected and equipped `Krótki miecz` on active hero.
   - Tested potion usage (`Mikstura zdrowia`) on hero at full HP.
   - Returned to Main Menu and tested session restoration (`KONTYNUUJ PRZYGODĘ`).
   - Verified session state restored cleanly.

4. **Logcat & Verification**:
   - Unit tests: **129 passed, 0 failed**.
   - Logcat inspection confirmed zero crashes or uncaught exceptions during execution.
