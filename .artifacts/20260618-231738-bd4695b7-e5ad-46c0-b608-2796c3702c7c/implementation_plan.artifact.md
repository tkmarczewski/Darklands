# Resolve UI Freezes and Navigation Inconsistency

The app successfully starts but suffers from heavy main thread blocking and a fragmented navigation flow between legacy Activities and Compose.

## Proposed Changes

### Core Systems (Performance)

#### [GameBootstrapper.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/GameBootstrapper.kt)
- Convert `bootstrapFreshWorld` to a `suspend` function.
- Wrap internal seeding calls in `withContext(Dispatchers.IO)`.

#### [GameRepository.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/GameRepository.kt)
- Move `persistCurrentState` logic to a background thread to prevent frame skips during auto-saves.

### Navigation & UI Architecture

#### [GameNavHost.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/main/GameNavHost.kt)
- Add new routes for `MainMenu`, `PlayerIdentity`, and `CharacterCreator`.
- Implement Compose versions of these screens (migrating logic from legacy Activities).

#### [MainActivity.kt](file:///C:/repo2/app/src/main/java/com/grimreich/ui/MainActivity.kt)
- Set `GameScreenMode.MAIN_MENU` as the default starting state.
- Remove Activity-switching logic; all flow will now be within `GameNavHost`.

### Content Seeding

#### [QuestSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/QuestSystem.kt)
- Audit `seedIntegratedContent` to ensure Aelion's quest and subsequent nodes are correctly registered.

---

## Verification Plan

### Automated Tests
- `./gradlew app:assembleDebug`
- Unit tests for seeding logic consistency.

### Manual Verification
- Launch app from Splash.
- Verify smooth transition between screens without "Davey!" (long frame) warnings in Logcat.
- Complete character creation and ensure the game state persists correctly in `MainActivity`.
