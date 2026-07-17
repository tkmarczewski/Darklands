package com.grimreich.core

import android.util.Log
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
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
    private val saveSystem: SaveSystem,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(repositoryJob + Dispatchers.IO)

    init {
        repositoryScope.launch { sync() }
    }
    private val saveMutex = Mutex()
    private val syncMutex = Mutex()

    // FIX: reverted from suspend/Mutex back to a synchronous ReentrantLock.
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
            Log.w("GameRepository", "PERF: updateState took ${duration}ms (DeepCopy overhead?)")
        }
    }

    fun updateState(tag: String, transform: (GameState) -> Unit) =
        updateState(shouldPersist = true, transform = transform)

    fun log(message: String) {
        updateState(shouldPersist = false) { state ->
            state.logEntries.add(message)
            state.trimLogs()
            _gameLogs.value = state.logEntries.toList()
        }
    }

    suspend fun sync() = syncMutex.withLock {
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        dialogueManager.seedBasicDialogues()
        questEngine.clearRegistry()
        questManifest.seed()

        echoSystemProvider.get().loadEchoesAsync()

        com.grimreich.core.TradingEngine.initialize(economySystemProvider.get())

        try {
            val jsonString = persistence.assets().open("grimreich/bestiary_pilot.json").bufferedReader().use { it.readText() }
            val loadedEnemies: List<Enemy> = json.decodeFromString(ListSerializer(Enemy.serializer()), jsonString)
            
            if (loadedEnemies.isEmpty()) throw IllegalStateException("Bestiary is empty")
            
            Bestiary.loadFromList(loadedEnemies)
        } catch (e: Exception) {
            Log.e("GameRepository", "CRITICAL: Failed to load bestiary", e)
            log("[Bestiary] Failed to load external data: ${e.message}")
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
                
                saveSystem.restoreFromPersistence(persistence)
                
                val domain = restored.toDomain()
                _gameState.value = domain
                _gameLogs.value = domain.logEntries.toList()
                
                sync() 
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Resilience: Restore failed, resetting session.", e)
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
                } catch (e: Exception) {
                    Log.e("GameRepository", "Auto-save failed", e)
                }
            }
        }
    }

    fun manualSave(slotId: Int, label: String = "") {
        val stateSnapshot = _gameState.value.deepCopy()
        repositoryScope.launch {
            saveMutex.withLock {
                try {
                    saveSystem.save(stateSnapshot, slotId, label)
                    saveSystem.saveToPersistence(persistence)
                    log("Gra zapisana w slocie $slotId.")
                } catch (e: Exception) {
                    Log.e("GameRepository", "Manual save failed", e)
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

