# Technical Debt Cleanup, Combat 2.0, and OtherSide Integration Plan

This plan addresses technical debt by centralizing UI constants, enhances the combat system with hero attributes, and integrates the OtherSide expedition mechanics with world stability.

## Proposed Changes

### 1. Technical Debt (UI Constants & Imports)
Centralize magic numbers and clean up wildcard imports.

#### [GameConstants.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/GameConstants.kt)
- Add `UI` nested object with standard spacing (`PADDING_SMALL`, `PADDING_MEDIUM`, `PADDING_LARGE`), icon sizes, and button heights.

#### [Multiple UI Files]
- Replace all inline `.dp` values with `GameConstants.UI` values.
- Replace `import .*` with explicit imports in all screens.

#### [NEW] [WorldPhaseWidget.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/shared/WorldPhaseWidget.kt)
- Create a dedicated UI component to display the current Era (e.g., "Era of Fracture" or "Era of Convergence") based on world stability.
- Add thematic descriptions and visual indicators (colors/glow) that shift with stability levels.

#### [HubScreen.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/main/HubScreen.kt)
- Integrate the `WorldPhaseWidget` into the top corner (e.g., Top-Right) of the Hub.
- Ensure it updates in real-time as the world state changes.

---

### 2. Combat 2.0 (Attribute Integration)
Make Hero attributes meaningful in battle.

#### [Combat.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/Combat.kt)
- **Perception**: Add critical hit chance logic based on `attacker.perception`.
- **Charisma**: Add morale regeneration or passive party buffs based on `attacker.charisma`.
- **Piety**: Scale the effectiveness of special skills (MIST, BLOOD, REFLECTION) with `attacker.piety`.

---

### 3. OtherSide Integration
Connect expeditions with the world's ontological state.

#### [OtherSideSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/OtherSideSystem.kt)
- Implement stability drain during active expeditions.
- Trigger "Reality Glitches" more frequently while on the Other Side.

#### [OntologicalEngine.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/engine/OntologicalEngine.kt)
- Add a multiplier for stability shifts when an expedition is active.

## Verification Plan

### Automated Tests
- `./gradlew test`
- Update `OntologicalEngineTest.kt` to verify stability drain during expeditions.
- Add `CombatAttributeTest.kt` to verify Perception and Charisma impact.

### Manual Verification
- Start an expedition and observe the global stability log in the Hub.
- Check if special skills in combat show different values based on hero Piety.
