package com.grimreich.core

import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestManifest
import com.grimreich.systems.StatePersistenceManager
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.Lazy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock as withReentrantLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val questEngineProvider: Lazy<QuestEngine>,
    private val dialogueManagerProvider: Lazy<DialogueManager>,
    private val questManifestProvider: Lazy<QuestManifest>,
    private val economySystemProvider: Lazy<com.grimreich.core.EconomyCalculator>,
    private val echoSystemProvider: Lazy<EchoSystem>,
    val persistence: StatePersistenceManager,
    val cityCatalogue: CityCatalogue,
    val itemCatalogue: ItemCatalogue,
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val saveMutex = Mutex()

    // FIX: reverted from suspend/Mutex back to a synchronous ReentrantLock.
    // updateState() is called synchronously from ~44 production call-sites
    // (NpcAI, AgingSystem, TravelSystem, TavernViewModel, etc.). Making it
    // suspend would break all of them. ReentrantLock still gives thread-safety
    // under concurrent coroutine access (Dispatchers.Default), because callers
    // simply block briefly on the lock instead of the function suspending.
    private val stateLock = ReentrantLock()

    private val questEngine get() = questEngineProvider.get()
    private val dialogueManager get() = dialogueManagerProvider.get()
    private val questManifest get() = questManifestProvider.get()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameLogs = MutableStateFlow<List<String>>(emptyList())
    val gameLogs: StateFlow<List<String>> = _gameLogs.asStateFlow()

    fun currentState(): GameState = _gameState.value

    fun replaceState(newState: GameState) {
        _gameState.value = newState
        repositoryScope.launch { sync() }
    }

    // FIX: back to a plain (non-suspend) function, synchronized via ReentrantLock
    // instead of `synchronized(this)` / Mutex.withLock. Safe to call from any
    // thread, including coroutines launched on Dispatchers.Default (ConcurrencyTest).
    fun updateState(shouldPersist: Boolean = true, transform: (GameState) -> Unit) {
        val startTime = System.currentTimeMillis()
        stateLock.withReentrantLock {
            val mutable = _gameState.value.deepCopy()
            transform(mutable)
            mutable.normalizeState()
            _gameState.value = mutable

            if (mutable.logEntries.isNotEmpty()) {
                _gameLogs.value = mutable.logEntries.toList()
            }

            if (shouldPersist) {
                persistCurrentState()
            }
        }
        val duration = System.currentTimeMillis() - startTime
        if (duration > 50) {
            android.util.Log.w("GameRepository", "PERF: updateState took ${duration}ms (DeepCopy overhead?)")
        }
    }

    // Overload for callers that pass a String tag (e.g. ConcurrencyTest, GameLoopController).
    fun updateState(tag: String, transform: (GameState) -> Unit) =
        updateState(shouldPersist = true, transform = transform)

    fun log(message: String) {
        stateLock.withReentrantLock {
            val current = _gameLogs.value.toMutableList()
            current.add(message)
            if (current.size > GameConstants.MAX_LOG_ENTRIES) {
                current.removeAt(0)
            }
            _gameLogs.value = current

            // FIX: update state log entries to match Flow
            _gameState.value.logEntries.clear()
            _gameState.value.logEntries.addAll(current)
            _gameState.value.trimLogs()
        }
    }

    suspend fun sync() {
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        dialogueManager.seedBasicDialogues()
        questEngine.clearRegistry()
        questManifest.seed()

        // FIX: Sequential loading to avoid race conditions and ensure data readiness
        echoSystemProvider.get().loadEchoesAsync()

        com.grimreich.core.TradingEngine.initialize(economySystemProvider.get())

        try {
            val jsonString = persistence.assets().open("grimreich/bestiary_pilot.json").bufferedReader().use { it.readText() }
            val type = object : com.google.gson.reflect.TypeToken<List<Enemy>>() {}.type
            val loadedEnemies: List<Enemy> = com.google.gson.Gson().fromJson(jsonString, type)
            
            // FIX: Critical validation - don't start if bestiary is empty
            if (loadedEnemies.isEmpty()) throw IllegalStateException("Bestiary is empty")
            
            Bestiary.loadFromList(loadedEnemies)
        } catch (e: Exception) {
            android.util.Log.e("GameRepository", "CRITICAL: Failed to load bestiary", e)
            log("[Bestiary] Failed to load external data: ${e.message}")
            // Optional: rethrow if we want to force fail-fast during bootstrap
        }
    }

    suspend fun restoreIfAvailable(): Boolean {
        return try {
            val restored = persistence.restore()
            if (restored != null) {
                if (restored.version < 3) {
                    persistence.clearSessionOnly()
                    return false
                }
                
                // Load manual slots into memory
                SaveSystem.restoreFromPersistence(persistence)
                
                val domain = restored.toDomain()
                _gameState.value = domain
                _gameLogs.value = domain.logEntries.toList()
                
                sync() // Wait for assets
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("GameRepository", "Resilience: Restore failed, resetting session.", e)
            clearSessionAndReset()
            false
        }
    }

    fun persistCurrentState() {
        val stateSnapshot = _gameState.value.deepCopy()
        repositoryScope.launch {
            saveMutex.withLock {
                try {
                    val dto = stateSnapshot.toDto()
                    persistence.persist(dto)
                    // FIX: Removed SaveSystem.saveToPersistence() from auto-save.
                    // Manual slots should only be saved on explicit user action.
                } catch (e: Exception) {
                    android.util.Log.e("GameRepository", "Auto-save failed", e)
                }
            }
        }
    }

    /**
     * Explicitly saves current state to a manual save slot.
     */
    fun manualSave(slotId: Int, label: String = "") {
        val stateSnapshot = _gameState.value.deepCopy()
        repositoryScope.launch {
            saveMutex.withLock {
                try {
                    SaveSystem.save(stateSnapshot, slotId, label)
                    SaveSystem.saveToPersistence(persistence)
                    log("Gra zapisana w slocie $slotId.")
                } catch (e: Exception) {
                    android.util.Log.e("GameRepository", "Manual save failed", e)
                }
            }
        }
    }

    fun snapshotForTests(): GameState = _gameState.value.deepCopy()

    fun replaceStateForTests(state: GameState) {
        _gameState.value = state.deepCopy().also { it.normalizeState() }
    }

    fun hasSession(): Boolean = persistence.exists() && persistence.hasPersistedSession()

    fun clearSessionAndReset() {
        persistence.clearSessionOnly()
        _gameState.value = GameState()
        _gameLogs.value = emptyList()
        repositoryScope.launch { sync() }
    }
}
