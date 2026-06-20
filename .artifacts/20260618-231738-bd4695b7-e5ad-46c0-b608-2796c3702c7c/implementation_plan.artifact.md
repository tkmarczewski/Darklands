# Resolve Startup Crash, Circular Dependencies, and EchoSystem Initialization

The application crashes at startup because of two intertwined issues:
1. **Circular Dependency Loop**: `GameRepository` <-> `QuestSystem` <-> `GameRepository` (and others) prevents the Hilt graph from resolving.
2. **EchoSystem Crash**: `GrimReichApp` attempts to use `echoSystem` before Hilt has injected it, resulting in `UninitializedPropertyAccessException`.

## Proposed Changes

### DI Configuration

#### [AppModule.kt](file:///C:/repo2/app/src/main/java/com/grimreich/di/AppModule.kt)
- Update `provideQuestSystem` to accept `Provider<GameRepository>`.
- Update `provideGameRepository` to accept `Provider<QuestSystem>` and `Provider<DialogueManager>`.
- This breaks the cyclic dependency at the factory level.

### Core Systems

#### [QuestSystem.kt](file:///C:/repo2/app/src/main/java/com/grimreich/systems/QuestSystem.kt)
- Update constructor to use `Provider<GameRepository>`.
- Use `private val gameRepository get() = gameRepositoryProvider.get()` for lazy access.

#### [GameRepository.kt](file:///C:/repo2/app/src/main/java/com/grimreich/core/GameRepository.kt)
- Update constructor to use `Provider<QuestSystem>` and `Provider<DialogueManager>`.
- Access these systems via lazy getters to ensure they are only retrieved when the graph is ready.

### Application Lifecycle (Critical Fix)

#### [GrimReichApp.kt](file:///C:/repo2/app/src/main/java/com/grimreich/GrimReichApp.kt)
- Change `echoSystem` to `Provider<EchoSystem>`.
- In `onCreate`, use `echoSystemProvider.get().init(this)`.
- **Why?** This implements "Variant C" (Lazy/Deferred creation). By using a `Provider`, we don't force Hilt to resolve the entire (potentially broken) graph immediately when the `Application` object is created.

---

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug`.
- If it passes, the DI graph is at least theoretically resolvable.

### Manual Verification
- Deploy to device/emulator.
- **Success Criteria**: App shows Splash screen, then transitions to Main Menu.
- **Logcat Check**: No `UninitializedPropertyAccessException` on start.
