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
    private val persistenceLock = Mutex()

    suspend fun save(state: GameState, slotId: Int, label: String = "") {
        slotsLock.withLock {
            val copy = state.deepCopy()
            val hash = computeStateHash(copy).toString()
            val snapshot = SaveSnapshot(
                version = SAVE_VERSION,
                timestamp = System.currentTimeMillis(),
                label = label,
                state = copy,
                checksum = hash
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
            val snapshot = saveSlots[slotId] ?: return@withLock null
            val currentHash = computeStateHash(snapshot.state).toString()
            if (snapshot.checksum != null && snapshot.checksum != currentHash) {
                android.util.Log.e("SaveSystem", "Checksum mismatch detected for slot $slotId! Expected ${snapshot.checksum}, got $currentHash")
                return@withLock null
            }
            snapshot.state.normalizeState()
            snapshot.state.deepCopy()
        }
    }

    suspend fun getSlots(): Map<Int, SaveSnapshot> = slotsLock.withLock { saveSlots.toMap() }

    suspend fun deleteSlot(slotId: Int) {
        slotsLock.withLock {
            saveSlots.remove(slotId)
        }
    }

    fun computeStateHash(state: GameState): Int = SaveIntegrity.computeStateHash(state)

    suspend fun saveToPersistenceDirect(persistence: StatePersistenceManager): Boolean {
        return try {
            persistenceLock.withLock {
                val copy = slotsLock.withLock { saveSlots.toMap() }
                persistence.persistSlots(copy)
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("SaveSystem", "Failed to persist slots", e)
            false
        }
    }

    fun saveToPersistence(persistence: StatePersistenceManager) {
        scope.launch {
            saveToPersistenceDirect(persistence)
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
