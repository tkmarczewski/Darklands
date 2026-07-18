package com.grimreich.core

import com.grimreich.systems.StatePersistenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveSystem @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val saveSlots = mutableMapOf<Int, SaveSnapshot>()

    fun save(state: GameState, slotId: Int, label: String = "") {
        val snapshot = SaveSnapshot(
            version = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label = label,
            state = state.deepCopy()
        )
        saveSlots[slotId] = snapshot
    }

    fun load(slotId: Int): SaveSnapshot? {
        return saveSlots[slotId]?.let { it.copy(state = it.state.deepCopy()) }
    }

    fun restore(slotId: Int): GameState? {
        return saveSlots[slotId]?.state?.deepCopy()
    }

    fun getSlots(): Map<Int, SaveSnapshot> = saveSlots.toMap()

    fun deleteSlot(slotId: Int) {
        saveSlots.remove(slotId)
    }

    fun computeStateHash(state: GameState): Int = SaveIntegrity.computeStateHash(state)

    fun saveToPersistence(persistence: StatePersistenceManager) {
        scope.launch {
            persistence.persistSlots(saveSlots)
        }
    }

    fun restoreFromPersistence(persistence: StatePersistenceManager) {
        scope.launch {
            val restored = persistence.restoreSlots()
            saveSlots.clear()
            saveSlots.putAll(restored)
        }
    }
}
