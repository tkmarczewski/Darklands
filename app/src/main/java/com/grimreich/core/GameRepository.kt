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
        state = GameState()
        state.gold = 100

        cityCatalogue.clear()
        cityCatalogue.seedCanonical()
        
        itemCatalogue.seed()

        state.grimCurrentRegion = "wybrzeze_polnocne"
        state.world.location = "wybrzeze_polnocne"

        questSystem.clear()
        questSystem.seedIntegratedContent()
        dialogueManager.seedBasicDialogues()

        state.logEntries.add("Początek nowej ery w Grimreich.")
        persistCurrentState()
    }

    fun log(msg: String) {
        state.logEntries.add(msg)
        if (state.logEntries.size > 100) state.logEntries.removeAt(0)
        persistCurrentState()
    }

    fun sync() {}

    fun restoreIfAvailable(): Boolean {
        val restored = persistence.restore() ?: return false
        state = restored.toDomain()
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
