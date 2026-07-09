package com.grimreich.core

import com.grimreich.grimreich.v1.*
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val questEngineProvider: Lazy<QuestEngine>,
    private val dialogueManagerProvider: Lazy<DialogueManager>,
    private val questManifestProvider: Lazy<QuestManifest>,
    val persistence: StatePersistenceManager,
    val cityCatalogue: CityCatalogue,
    val itemCatalogue: ItemCatalogue,
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val saveMutex = Mutex() // FIX (BUG-14): Prevent simultaneous writes

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
        sync()
    }

    fun updateState(shouldPersist: Boolean = true, transform: (GameState) -> Unit) {
        synchronized(this) {
            val mutable = _gameState.value.deepCopy()
            transform(mutable)
            mutable.normalizeState()
            _gameState.value = mutable
            
            // Sync internal logs flow if logs were changed inside transform
            if (mutable.logEntries.isNotEmpty()) {
                _gameLogs.value = mutable.logEntries.toList()
            }

            if (shouldPersist) {
                persistCurrentState()
            }
        }
    }

    /**
     * Unified logging. FIX (BUG-4, BUG-9): Synchronized and consistent.
     */
    fun log(message: String) {
        synchronized(this) {
            // Update Flow for UI
            val current = _gameLogs.value.toMutableList()
            current.add(message)
            if (current.size > GameConstants.MAX_LOG_ENTRIES) {
                current.removeAt(0)
            }
            _gameLogs.value = current
            
            // Update State for persistence
            _gameState.value.logEntries.clear()
            _gameState.value.logEntries.addAll(current)
            _gameState.value.trimLogs()
        }
    }

    fun sync() {
        android.util.Log.d("GameRepository", "SYNC START: Seeding catalogues...")
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        dialogueManager.seedBasicDialogues()
        questManifest.seed()
        
        try {
            val jsonString = persistence.assets().open("grimreich/bestiary_pilot.json").bufferedReader().use { it.readText() }
            val type = object : com.google.gson.reflect.TypeToken<List<Enemy>>() {}.type
            val loadedEnemies: List<Enemy> = com.google.gson.Gson().fromJson(jsonString, type)
            Bestiary.loadFromList(loadedEnemies)
            android.util.Log.d("GameRepository", "SYNC: Bestiary loaded successfully.")
        } catch (e: Exception) {
            log("[Bestiary] Failed to load external data: ${e.message}")
        }
        android.util.Log.d("GameRepository", "SYNC END.")
    }

    suspend fun restoreIfAvailable(): Boolean {
        return try {
            val restored = persistence.restore()
            if (restored != null) {
                if (restored.version < 3) {
                    persistence.clearSessionOnly()
                    return false
                }
                SaveSystem.restoreFromPersistence(persistence)
                val domain = restored.toDomain()
                _gameState.value = domain
                _gameLogs.value = domain.logEntries.toList()
                sync()
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
        val stateSnapshot = _gameState.value.deepCopy().also {
            it.world.echoIntensity = it.grimEngine.echoIntensity
            it.grimMutationPhase = it.grimEngine.mutationPhase
        }
        repositoryScope.launch {
            // FIX (BUG-14): Sequential saving via Mutex
            saveMutex.withLock {
                try {
                    val dto = stateSnapshot.toDto()
                    persistence.persist(dto)
                    SaveSystem.saveToPersistence(persistence)
                } catch (e: Exception) {
                    // Avoid recursive log call if log fails persistence
                    android.util.Log.e("GameRepository", "Save failed", e)
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
        android.util.Log.e("GameRepository", "CRITICAL: clearSessionAndReset() called!")
        persistence.clear()
        _gameState.value = GameState()
        _gameLogs.value = emptyList()
        sync() // FIX: Re-seed registries for the new session
    }
}
