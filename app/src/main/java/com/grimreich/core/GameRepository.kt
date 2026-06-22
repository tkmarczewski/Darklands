package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestSystem
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
    private val questSystemProvider: Lazy<QuestSystem>,
    private val dialogueManagerProvider: Lazy<DialogueManager>,
    private val persistence: StatePersistenceManager,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue,
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val questSystem get() = questSystemProvider.get()
    private val dialogueManager get() = dialogueManagerProvider.get()
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun currentState(): GameState = _gameState.value

    fun replaceState(newState: GameState) {
        _gameState.value = newState
        sync()
    }

    fun updateState(transform: (GameState) -> Unit) {
        val current = _gameState.value
        transform(current)
        _gameState.value = current.deepCopy() // Force flow update with a deep copy
        persistCurrentState()
    }

    fun seed() {
        log("Seed requested via Bootstrapper flow")
    }

    fun log(msg: String) {
        updateState { 
            it.logEntries.add(msg)
            if (it.logEntries.size > GameConstants.MAX_LOG_ENTRIES) it.logEntries.removeAt(0)
        }
    }

    fun sync() {
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        questSystem.seedIntegratedContent()
        dialogueManager.seedBasicDialogues()
    }

    fun restoreIfAvailable(): Boolean {
        val restored = persistence.restore() ?: return false
        if (restored.version < 2) {
            persistence.clear()
            return false
        }
        _gameState.value = restored.toDomain()
        sync()
        return true
    }

    fun persistCurrentState() {
        val stateSnapshot = _gameState.value.deepCopy()
        repositoryScope.launch {
            try {
                val dto = stateSnapshot.toDto()
                persistence.persist(dto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hasSession(): Boolean = persistence.exists()

    fun clearSessionAndReset() {
        persistence.clear()
        _gameState.value = GameState()
    }
}
