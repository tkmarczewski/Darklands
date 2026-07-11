package com.grimreich.core

import com.google.gson.Gson

const val SAVE_VERSION = 3

// ==================== SAVE SYSTEM ====================
data class SaveSlot(
    val slotId: Int,
    val snapshot: SaveSnapshot?,
    val isEmpty: Boolean = snapshot == null
)

object SaveSystem {
    private val slots = java.util.concurrent.ConcurrentHashMap<Int, SaveSnapshot>()
    private var autoSaveSnapshot: SaveSnapshot? = null
    private var lastAutoSaveHash: Int = 0
    private val gson = com.google.gson.Gson()

    suspend fun save(gameState: GameState, slotId: Int = 0, label: String = ""): SaveSnapshot {
        val stateCopy = gameState.deepCopy()
        val stateJson = gson.toJson(stateCopy)
        
        // BUG-02: Ensure cancellation safety
        val checksum = try {
            SaveIntegrity.generateChecksum(stateJson)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            ""
        }

        val snapshot = SaveSnapshot(
            version   = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label     = label.ifEmpty { "Save ${slotId + 1}" },
            state     = stateCopy,
            checksum  = checksum
        )
        slots[slotId] = snapshot
        return snapshot
    }

    fun load(slotId: Int = 0): SaveSnapshot? = slots[slotId]?.let { it.copy(state = it.state.deepCopy()) }

    fun isCompatible(snapshot: SaveSnapshot): Boolean {
        return snapshot.version >= 1 && snapshot.version <= SAVE_VERSION
    }

    fun getSlot(slotId: Int): SaveSlot = SaveSlot(
        slotId   = slotId,
        snapshot = slots[slotId]
    )

    fun getAllSlots(count: Int = 3): List<SaveSlot> = (0 until count.coerceAtLeast(0)).map { getSlot(it) }

    fun deleteSlot(slotId: Int) { slots.remove(slotId) }

    fun importSlots(persisted: Map<Int, SaveSnapshot>) {
        slots.clear()
        slots.putAll(persisted.filterKeys { it >= 0 })
    }

    fun exportSlots(): Map<Int, SaveSnapshot> = slots.toMap()

    fun clearAll() {
        slots.clear()
        autoSaveSnapshot = null
        lastAutoSaveHash = 0
    }

    suspend fun saveToPersistence(persistence: com.grimreich.systems.StatePersistenceManager) {
        persistence.persistSlots(slots)
    }

    suspend fun restoreFromPersistence(persistence: com.grimreich.systems.StatePersistenceManager) {
        importSlots(persistence.restoreSlots())
    }

    // ==================== AUTOSAVE ====================
    suspend fun autoSave(gameState: GameState): Boolean {
        val hash = computeStateHash(gameState)
        if (hash == lastAutoSaveHash) return false
        
        val stateCopy = gameState.deepCopy()
        val stateJson = gson.toJson(stateCopy)
        
        // BUG-02: Ensure cancellation safety
        val checksum = try {
            SaveIntegrity.generateChecksum(stateJson)
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            ""
        }

        autoSaveSnapshot = SaveSnapshot(
            version   = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label     = "Autosave",
            state     = stateCopy,
            checksum  = checksum
        )
        lastAutoSaveHash = hash
        return true
    }

    fun loadAutoSave(): SaveSnapshot? = autoSaveSnapshot?.let { it.copy(state = it.state.deepCopy()) }
    fun hasAutoSave(): Boolean = autoSaveSnapshot != null

    private fun computeStateHash(state: GameState): Int {
        return java.util.Objects.hash(
            state.gold,
            state.world.day,
            state.party.size,
            state.quest.activeQuestIds.size,
            state.inventory.size,
            state.reputation.globalFactions.size,
            state.metaAwarenessLevel
        )
    }

    // ==================== VALIDATION ====================
    data class ValidationResult(
        val isValid: Boolean,
        val version: Int,
        val isCompatible: Boolean,
        val message: String
    )

    suspend fun validate(snapshot: SaveSnapshot): ValidationResult {
        val compatible = isCompatible(snapshot)
        val stateJson = gson.toJson(snapshot.state)
        val checksumValid = snapshot.checksum?.let { SaveIntegrity.verify(stateJson, it) } ?: false

        val isValid = snapshot.version >= 1 &&
            snapshot.state.gold >= 0 &&
            snapshot.state.world.day >= 1 &&
            checksumValid

        return ValidationResult(
            isValid      = isValid,
            version      = snapshot.version,
            isCompatible = compatible,
            message = when {
                !compatible -> "Niezgodna wersja zapisu (${snapshot.version} vs $SAVE_VERSION)"
                !checksumValid -> "Naruszona integralność zapisu (Checksum mismatch)"
                !isValid    -> "Uszkodzony zapis — nieprawidłowy stan gry"
                else        -> "Zapis poprawny (wersja ${snapshot.version})"
            }
        )
    }

    fun migrateIfNeeded(snapshot: SaveSnapshot): SaveSnapshot {
        if (snapshot.version == SAVE_VERSION) return snapshot
        return snapshot.copy(version = SAVE_VERSION)
    }
}
