# Fix Startup Crash and DI Architecture Cleanup

I have resolved the critical startup crash and stabilized the Hilt dependency injection graph.

## Key Fixes

### 1. Hilt Compilation & KSP Migration
- **Problem**: The project was using `annotationProcessor` for Hilt with Kotlin 2.x, which caused Hilt annotations to be ignored. This led to `UninitializedPropertyAccessException` at runtime.
- **Solution**: Migrated Hilt to use **KSP (Kotlin Symbol Processing)** and upgraded Hilt to version **2.59.2**. This ensures full compatibility with the latest Kotlin versions and correct code generation for `@AndroidEntryPoint` and `@HiltAndroidApp`.

### 2. Breaking Circular Dependencies
- **Problem**: A hard dependency loop existed between `GameRepository` and `QuestSystem`.
- **Solution**: Used `dagger.Lazy` in constructors for `QuestSystem` and `GameRepository` to break the cyclic dependency, allowing Hilt to initialize these singletons correctly.

### 3. EchoSystem Self-Initialization
- **Problem**: `GrimReichApp` was manually initializing `EchoSystem` in `onCreate`, which triggered the crash when injection failed.
- **Solution**: Refactored `EchoSystem` to use `@ApplicationContext` and self-initialize in its `init` block. Removed all manual `init` calls from `GrimReichApp` and `SplashActivity`.

### 4. Code Cleanup & Warning Removal
- **AppModule Cleanup**: Removed almost all redundant `@Provides` methods, relying on `@Inject constructor` on individual classes. This simplifies the DI graph and reduces maintenance overhead.
- **Deprecated Patterns**: Replaced deprecated `lifecycleScope.launchWhenCreated` with `repeatOnLifecycle` in `SplashActivity`.
- **Naming Conventions**: Renamed `@Provides` methods (before removal) to follow Kotlin CamelCase conventions.

## Verification Summary
- **Build Status**: `./gradlew app:assembleDebug` completes successfully with ZERO errors.
- **Runtime Verification**: The app launches, displays the Splash screen for 2 seconds, and successfully transitions to the Main Menu without crashing.
- **Logcat**: Verified that no `UninitializedPropertyAccessException` or Hilt-related errors occur during startup.
