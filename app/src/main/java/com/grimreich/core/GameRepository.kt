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
    private var state: GameState = GameState()

    fun currentState(): GameState = state

    fun replaceState(newState: GameState) {
        state = newState
    }

    fun updateState(transform: (GameState) -> GameState) {
        state = transform(state)
        persistCurrentState()
    }

    fun seed() {
        // Legacy seed disabled to avoid conflicts with GameBootstrapper
        log("Seed requested but ignored (using Bootstrapper flow)")
    }

    fun log(msg: String) {
        state.logEntries.add(msg)
        if (state.logEntries.size > 100) state.logEntries.removeAt(0)
        persistCurrentState()
    }

    fun sync() {
        // Reseed runtime content on sync/restore to ensure catalogues are populated
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
        state = restored.toDomain()
        sync() // Ensure catalogues are seeded
        return true
    }

    fun persistCurrentState() {
        val stateSnapshot = state.deepCopy()
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
        state = GameState()
    }
}
