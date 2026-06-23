# Project Refactoring and Polish Plan

Based on the audit, I propose a plan to address technical debt and enhance core gameplay mechanics.

## Proposed Changes

### 1. Technical Debt (Detekt & Standards)
Clean up the codebase to comply with project standards.

#### [Multiple UI Files]
- Replace wildcard imports with explicit ones.
- Replace magic numbers with constants from `GameConstants.kt`.

#### [Combat.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/Combat.kt)
- Refactor `resolveRound` to reduce length and complexity.

### 2. Gameplay Enhancements
Deepen existing systems.

#### [MutationSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/mutations/MutationSystem.kt)
- Implement mutation tier progression (evolution logic).

#### [SaintCatalogue.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/SaintCatalogue.kt) & UI
- Integrate Saint blessings with actual game state modifiers (e.g., stability recovery).

### 3. Expanded Unit Tests
Ensure the new logic is robust.

#### [MutationSystemTest.kt](file:///C:/repo2/app/src/test/java/com/grimreich/core/mutations/MutationSystemTest.kt) [NEW]
- Test mutation chance based on stability.
- Test attribute modifier application.

## Verification Plan

### Automated Tests
- `./gradlew test` (Verify existing and new tests).
- `./gradlew detekt` (Verify reduction in lint warnings).

### Manual Verification
- Launch the app and verify the Temple interaction updates the player's attributes or world stability.
