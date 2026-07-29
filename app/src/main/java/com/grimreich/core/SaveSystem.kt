package com.grimreich.core

import com.grimreich.systems.StatePersistenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveSystem @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val saveSlots = mutableMapOf<Int, SaveSnapshot>()
    private val slotsLock = Mutex()

    suspend fun save(state: GameState, slotId: Int, label: String = "") {
        slotsLock.withLock {
            val snapshot = SaveSnapshot(
                version = SAVE_VERSION,
                timestamp = System.currentTimeMillis(),
                label = label,
                state = state.deepCopy()
            )
            saveSlots[slotId] = snapshot
        }
    }

    suspend fun load(slotId: Int): SaveSnapshot? {
        return slotsLock.withLock {
            saveSlots[slotId]?.let { it.copy(state = it.state.deepCopy()) }
        }
    }

    suspend fun restore(slotId: Int): GameState? {
        return slotsLock.withLock {
            saveSlots[slotId]?.state?.deepCopy()
        }
    }

    suspend fun getSlots(): Map<Int, SaveSnapshot> = slotsLock.withLock { saveSlots.toMap() }

    suspend fun deleteSlot(slotId: Int) {
        slotsLock.withLock {
            saveSlots.remove(slotId)
        }
    }

    fun computeStateHash(state: GameState): Int = SaveIntegrity.computeStateHash(state)

    fun saveToPersistence(persistence: StatePersistenceManager) {
        scope.launch {
            val copy = slotsLock.withLock { saveSlots.toMap() }
            persistence.persistSlots(copy)
        }
    }

    suspend fun restoreFromPersistence(persistence: StatePersistenceManager) {
        val restored = persistence.restoreSlots()
        slotsLock.withLock {
            saveSlots.clear()
            saveSlots.putAll(restored)
        }
    }
}
