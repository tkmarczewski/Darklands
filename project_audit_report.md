# Project Audit Report

## 1. Build Status
- **Result:** Success
- **Build Command:** `:app:assembleDebug`
- **Output:** `app-debug.apk`

## 2. Unit Testing
- **Result:** Passed (21/21)
- **Command:** `:app:testDebugUnitTest`
- **Coverage:** ~9.3% (Jacoco)
- **Key Observation:** Unit tests cover core logic (calculators, engine stats) but miss UI/Activity interactions.

## 3. Critical Bugs Found
### Bug 1: NPE in HubActivity
- **Location:** `HubActivity.kt:55`
- **Description:** `tvDevMenuTrigger.setOnClickListener` throws NPE because the view is missing or inaccessible in certain layout configurations.

### Bug 2: Missing Theme Attribute (Crash)
- **Location:** `styles.xml` / `UiUtils.showNarrativePopup`
- **Description:** `Theme.GrimReich` is missing the `colorSurface` attribute required by MaterialComponents. Any popup (Tavern, Map regions) causes an immediate crash with `IllegalArgumentException`.

## 4. UI/UX Audit
- **Navigation:** Main Menu and Character Creator are functional but transition to Hub is brittle.
- **Dev Menu:** Essential for testing due to creator validation issues.
- **Functionality:** 
    - Dialogue: Functional (Heinrich).
    - Map: Loads, but interaction triggers the Theme crash.
    - City: Tavern and Shop are currently inaccessible due to the Theme crash.

## 5. Recommendations
1. **Fix Theme:** Add `<item name="colorSurface">?attr/colorBackground</item>` (or equivalent) to `Theme.GrimReich`.
2. **Safe View Access:** Use null-safe calls or View Binding in `HubActivity` to prevent NPEs.
3. **Increase Test Coverage:** Add UI tests (Espresso) and more unit tests for NPC/Quest logic.
