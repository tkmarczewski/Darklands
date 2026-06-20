# Fix Startup Crash, Performance ANRs, and Unified Navigation

I have completed a comprehensive overhaul of the application's core architecture to resolve crashes, severe lag, and navigation inconsistencies.

## Key Accomplishments

### 1. Stability & DI Fixes
- **Hilt KSP Migration**: Upgraded Hilt to 2.59.2 and switched to **KSP** (Kotlin Symbol Processing). This was the root cause of the `UninitializedPropertyAccessException` as the previous setup was incompatible with Kotlin 2.x.
- **Breaking Circular Dependencies**: Resolved the `GameRepository` <-> `QuestSystem` loop using `dagger.Lazy`.

### 2. Performance Optimization
- **Background Operations**: Refactored `GameBootstrapper` and `GameRepository` to move world seeding and state serialization (JSON saving) to **IO threads**. This eliminated the 1-6 second UI freezes (ANRs) reported in logcat.
- **Async Persistence**: `persistCurrentState()` now uses a background scope with a thread-safe `GameState` snapshot to ensure the UI remains smooth during auto-saves.

### 3. Navigation Consolidation
- **Single-Activity Architecture**: Migrated `MainMenu`, `PlayerIdentity`, and `CharacterCreator` from legacy Android Activities to **Jetpack Compose** screens integrated within `MainActivity`.
- **Unified Flow**: `SplashActivity` now routes directly to `MainActivity`, where `GameNavHost` manages the entire user journey. This significantly improves state management and reduces startup overhead.

### 4. Quest & Content Refinement
- **Dynamic Quest Dialogues**: Fixed a logic error in `CityViewModel` where all start quests defaulted to Aelion's dialogue. It now dynamically maps quests to the correct NPC nodes (Aelion, Merchant, Zealot, Mystic).
- **Expanded Seed**: Added new introductory quests (`q_start_02`, `q_start_03`) to enrich the early-game experience on the North Coast.

## Verification Summary
- **Zero Errors**: `./gradlew app:assembleDebug` passes with no errors.
- **Responsive UI**: Verified manually that the app launches, transitions through the menu/creator, and reaches the `HubScreen` without frame skips or freezes.
- **Correct Logic**: Confirmed that Aelion's quest is correctly seeded and triggers the intended interaction in the city.
