package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestManifest
import com.grimreich.systems.StatePersistenceManager
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val questEngineProvider: Lazy<QuestEngine>,
    private val dialogueManagerProvider: Lazy<DialogueManager>,
    private val questManifestProvider: Lazy<QuestManifest>,
    private val persistence: StatePersistenceManager,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue,
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val questEngine get() = questEngineProvider.get()
    private val dialogueManager get() = dialogueManagerProvider.get()
    private val questManifest get() = questManifestProvider.get()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        sync()
    }

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
            if (shouldPersist) {
                persistCurrentState()
            }
        }
    }

    fun log(message: String) {
        updateState(shouldPersist = false) { state ->
            state.logEntries.add(message)
            while (state.logEntries.size > GameConstants.MAX_LOG_ENTRIES) {
                state.logEntries.removeAt(0)
            }
        }
    }

    fun sync() {
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        dialogueManager.seedBasicDialogues()
        questManifest.seed()
    }

    fun restoreIfAvailable(): Boolean {
        val restored = persistence.restore()
        if (restored != null) {
            if (restored.version < 3) {
                // FIX (BUG-2a): Use clearSessionOnly() instead of clear().
                // The old code called persistence.clear() here which deleted BOTH
                // current_session.json AND save_slots.json. This meant that whenever
                // a player had an outdated session (version < 3) the entire slots file
                // was nuked, losing all manual saves.
                // clearSessionOnly() deletes only the stale session file, preserving slots.
                persistence.clearSessionOnly()
                return false
            }
            // FIX (BUG-2b): SaveSystem.restoreFromPersistence was previously called
            // BEFORE the null-check on `restored`. When restored == null the code then
            // called persistence.clear(), wiping slots even though they had just been
            // loaded into memory (SaveSystem.importSlots). The in-memory slots survived,
            // but the file was gone so they would be lost on next process death.
            // Now slots are only restored when a valid session is present.
            SaveSystem.restoreFromPersistence(persistence)
            _gameState.value = restored.toDomain()
            sync()
            return true
        }
        // No session file present - do NOT clear anything.
        // The player may have valid manual save slots even without an active session.
        return false
    }

    fun persistCurrentState() {
        val stateSnapshot = _gameState.value.deepCopy().also {
            it.grimEchoIntensity = it.grimEngine.echoIntensity
            it.grimMutationPhase = it.grimEngine.mutationPhase
        }
        repositoryScope.launch {
            try {
                val dto = stateSnapshot.toDto()
                persistence.persist(dto)
                SaveSystem.saveToPersistence(persistence)
            } catch (e: Exception) {
                log("[Persistence] Save failed: ${e.message ?: "unknown error"}")
            }
        }
    }

    fun snapshotForTests(): GameState = _gameState.value.deepCopy()

    fun replaceStateForTests(state: GameState) {
        _gameState.value = state.deepCopy().also { it.normalizeState() }
    }

    fun hasSession(): Boolean = persistence.exists()

    fun clearSessionAndReset() {
        persistence.clear()
        _gameState.value = GameState()
    }
}
