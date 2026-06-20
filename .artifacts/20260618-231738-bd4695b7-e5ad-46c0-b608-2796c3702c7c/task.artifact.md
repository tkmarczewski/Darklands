# Task Management

- [x] Fix Startup Crash and Circular Dependencies
	- [x] Research new project state and logcat
	- [x] Identify circular dependency loop in Hilt graph
	- [x] Create implementation plan
	- [x] Update `QuestSystem.kt` to use `dagger.Lazy<GameRepository>`
	- [x] Update `GameRepository.kt` to use `dagger.Lazy<QuestSystem>`
	- [x] Refactor `EchoSystem.kt` to self-initialize using `@ApplicationContext`
	- [x] Migrate `build.gradle` to use `KSP` for Hilt (CRITICAL FIX)
	- [x] Clean up `AppModule.kt` and `GrimReichApp.kt`
	- [x] Verify build and startup
- [x] Fix Main Thread Blocking and Navigation Inconsistency
	- [x] Refactor `GameBootstrapper` to use coroutines for background seeding
	- [x] Optimize `GameRepository` persistence (async JSON saving)
	- [x] Consolidate legacy Activities into Compose `GameNavHost`
	- [x] Fix Aelion quest content seeding logic
	- [x] Verify UI responsiveness and state persistence
