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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
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

    @Volatile
    var isWiped: Boolean = false
        private set

    private val saveRequests = MutableSharedFlow<GameState>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val syncMutex = Mutex()
    private val saveMutex = Mutex()
    private var syncJob: Job? = null

    init {
        syncJob = repositoryScope.launch { sync() }
        
        // BUG-NEW-LEAK-02: Collector for conflated save requests.
        // Ensures only the latest state is saved and multiple requests don't pile up.
        repositoryScope.launch {
            saveRequests.collect { snapshot ->
                saveMutex.withLock {
                    if (isWiped) {
                        Log.d("GameRepository", "Auto-save cancelled (isWiped = true).")
                        return@withLock
                    }
                    try {
                        val dto = snapshot.toDto()
                        persistence.persist(dto)
                        Log.d("GameRepository", "Conflated auto-save completed.")
                    } catch (e: Exception) {
                        Log.e("GameRepository", "Auto-save failed", e)
                    }
                }
            }
        }
    }

    // FIX: reverted from suspend/Mutex back to a synchronous ReentrantLock.
    private val stateLock = ReentrantLock()

    val questEngine get() = questEngineProvider.get()
    val dialogueManager get() = dialogueManagerProvider.get()
    val questManifest get() = questManifestProvider.get()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameLogs = MutableStateFlow<List<String>>(emptyList())
    val gameLogs: StateFlow<List<String>> = _gameLogs.asStateFlow()

    fun currentState(): GameState = _gameState.value

    fun replaceState(newState: GameState) {
        _gameState.value = newState
        repositoryScope.launch { sync() }
    }

    private val activeTransactionState = ThreadLocal<GameState?>()

    fun updateState(shouldPersist: Boolean = true, transform: (GameState) -> Unit) {
        val startTime = System.currentTimeMillis()
        stateLock.withReentrantLock {
            val mutable = _gameState.value.deepCopy()
            val previousTx = activeTransactionState.get()
            activeTransactionState.set(mutable)
            try {
                transform(mutable)
                mutable.normalizeState()
                _gameState.value = mutable
                _gameLogs.value = mutable.logEntries.toList()

                if (shouldPersist) {
                    persistCurrentState()
                }
            } finally {
                activeTransactionState.set(previousTx)
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
        val currentTx = activeTransactionState.get()
        if (currentTx != null) {
            Log.i("GameRepository", "LOG (Nested Tx): $message")
            logDirect(currentTx, message)
            return
        }
        updateState(shouldPersist = false) { state ->
            logDirect(state, message)
        }
    }

    /**
     * Appends a message directly to the provided state's log.
     * Use this when already inside an updateState block.
     */
    fun logDirect(state: GameState, message: String) {
        state.logEntries.add(message)
        state.trimLogs()
        _gameLogs.value = state.logEntries.toList()
    }

    /**
     * Ensures that the repository is fully synchronized before proceeding.
     * Use this in ViewModels that require access to catalogues immediately after startup.
     */
    suspend fun awaitSync() {
        syncJob?.join()
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
            val assets = try { persistence.assets() } catch (e: Exception) { null }
            if (assets != null) {
                val jsonString = assets.open("grimreich/bestiary_pilot.json").bufferedReader().use { it.readText() }
                val loadedEnemies: List<Enemy> = json.decodeFromString(ListSerializer(Enemy.serializer()), jsonString)
                
                if (loadedEnemies.isEmpty()) throw IllegalStateException("Bestiary is empty")
                
                Bestiary.loadFromList(loadedEnemies)
            }
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
                
                isWiped = false
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
        if (isWiped) return
        val snapshot = _gameState.value.deepCopy()
        saveRequests.tryEmit(snapshot)
    }

    fun flushAndPersistSync() {
        if (isWiped) {
            Log.d("GameRepository", "Sync flush cancelled (isWiped = true).")
            return
        }
        try {
            val snapshot = _gameState.value.deepCopy()
            val dto = snapshot.toDto()
            kotlinx.coroutines.runBlocking(Dispatchers.IO) {
                persistence.persist(dto)
            }
            Log.d("GameRepository", "Synchronous flush save completed.")
        } catch (e: Exception) {
            Log.e("GameRepository", "Synchronous flush save failed", e)
        }
    }

    /**
     * Zamyka repozytorium i anuluje wszystkie oczekujące operacje po opróżnieniu zapisu.
     */
    fun close() {
        Log.d("GameRepository", "Closing repository, flushing save and cancelling jobs...")
        flushAndPersistSync()
        repositoryJob.cancel()
    }

    fun manualSave(slotId: Int, label: String = "") {
        if (isWiped) return
        val stateSnapshot = _gameState.value.deepCopy()
        repositoryScope.launch {
            saveMutex.withLock {
                if (isWiped) return@withLock
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
        isWiped = false
        val metaCopy = _gameState.value.persistentMeta.copy(
            unlockedLegacyBuffs = _gameState.value.persistentMeta.unlockedLegacyBuffs.toMutableSet(),
            unitedSelves = _gameState.value.persistentMeta.unitedSelves.toMutableList()
        )
        persistence.clearSessionOnly()
        val newState = GameState()
        newState.persistentMeta = metaCopy
        _gameState.value = newState
        _gameLogs.value = emptyList()
        repositoryScope.launch { sync() }
    }

    /**
     * Purges ALL save slots, session files, disk persistence, and persistent metadata ("Destrukcja").
     * Does NOT save a new session afterward.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    suspend fun deleteAllDataAndWipe() {
        stateLock.withReentrantLock {
            isWiped = true
            saveRequests.resetReplayCache()
        }
        
        // 1. Wipe files from disk FIRST before emitting state change to observers
        withContext(Dispatchers.IO + NonCancellable) {
            saveMutex.withLock {
                saveSystem.wipeAll(persistence)
            }
        }

        // 2. Update memory state AFTER disk files are completely purged
        stateLock.withReentrantLock {
            val wipedState = GameState()
            _gameState.value = wipedState
            _gameLogs.value = emptyList()
        }
    }
}

