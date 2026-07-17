# Implementation Plan - Unique Dialogues and Death Observer Optimization

Repair and verify the Main Hero's (BG) death logic, expand unique quest dialogues, and optimize the death observer for stability.

## Proposed Changes

### Dialogue System Enhancement

Add unique start and check nodes for quests that currently rely on generic role-based fallbacks.

#### [dialogues_extended.json](file:///C:/repo2/app/src/main/assets/grimreich/dialogues_extended.json)
- Add unique dialogue nodes for quests:
    - `q_blood_icon_start`
    - `q_lost_apostle_start`
    - `q_altar_silence_start`
    - `q_house_shadows_start`
    - `q_golden_ruins_betrayal_start`
    - `q_shadowless_wolves_start`
- Ensure triggers use `activate_quest` with the specific quest ID.

#### [CityViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/city/CityViewModel.kt)
- Update `selectQuestAndOpenDialogue` to prefer quest-specific nodes (`${quest.id}_start`, `${quest.id}_check`) over role-based fallbacks.

---

### Combat & Death Logic Optimization

#### [GameRootViewModel.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/main/GameRootViewModel.kt)
- Optimize `DEATH OBSERVER` to wait for a short duration or ensure combat is inactive before switching to the Ritual screen.
- Ensure `hero_main` death always sets `inspectedHeroId` to "hero_main" for the Ritual screen content.

#### [CombatSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/CombatSystem.kt)
- Update `handleHeroDeath` to immediately deactivate combat if `hero_main` dies, preventing further turns or enemy attacks from clashing with the Ritual screen transition.

## Verification Plan

### Automated Tests
- Run `gradle_build("app:assembleDebug")` to ensure no syntax errors.

### Manual Verification
1. **Death Logic**:
    - Start a game.
    - Enter combat.
    - Force `hero_main` death (via DEV menu or combat).
    - Verify immediate transition to Ritual screen with "Felix Anderson" (if Ralwing).
    - Verify combat is marked as inactive in logs.
2. **Quest Dialogues**:
    - Go to Quest Board in a city.
    - Pick `q_blood_icon`.
    - Verify unique dialogue starts instead of generic peasant text.
    - Verify quest activates correctly.
