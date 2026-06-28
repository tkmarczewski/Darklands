package com.grimreich.core

const val SAVE_VERSION = 3

// ==================== SAVE SYSTEM ====================
data class SaveSlot(
    val slotId: Int,
    val snapshot: SaveSnapshot?,
    val isEmpty: Boolean = snapshot == null
)

object SaveSystem {
    private val slots = mutableMapOf<Int, SaveSnapshot>()
    private var autoSaveSnapshot: SaveSnapshot? = null
    private var lastAutoSaveHash: Int = 0

    fun save(gameState: GameState, slotId: Int = 0, label: String = ""): SaveSnapshot {
        val snapshot = SaveSnapshot(
            version   = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label     = label.ifEmpty { "Save ${slotId + 1}" },
            state     = gameState.deepCopy()
        )
        slots[slotId] = snapshot
        return snapshot
    }

    fun load(slotId: Int = 0): SaveSnapshot? = slots[slotId]?.copy(state = slots[slotId]!!.state.deepCopy())

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

    fun saveToPersistence(persistence: com.grimreich.systems.StatePersistenceManager) {
        persistence.persistSlots(slots)
    }

    fun restoreFromPersistence(persistence: com.grimreich.systems.StatePersistenceManager) {
        importSlots(persistence.restoreSlots())
    }

    // ==================== AUTOSAVE ====================
    fun autoSave(gameState: GameState): Boolean {
        val hash = computeStateHash(gameState)
        if (hash == lastAutoSaveHash) return false
        autoSaveSnapshot = SaveSnapshot(
            version   = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label     = "Autosave",
            state     = gameState.deepCopy()
        )
        lastAutoSaveHash = hash
        return true
    }

    fun loadAutoSave(): SaveSnapshot? = autoSaveSnapshot?.copy(state = autoSaveSnapshot!!.state.deepCopy())
    fun hasAutoSave(): Boolean = autoSaveSnapshot != null

    private fun computeStateHash(state: GameState): Int {
        return java.util.Objects.hash(
            state.gold,
            state.world.day,
            state.party.size,
            state.quest.activeQuestIds.size,
            state.inventory.size
        )
    }

    // ==================== VALIDATION ====================
    data class ValidationResult(
        val isValid: Boolean,
        val version: Int,
        val isCompatible: Boolean,
        val message: String
    )

    fun validate(snapshot: SaveSnapshot): ValidationResult {
        val compatible = isCompatible(snapshot)
        val isValid = snapshot.version >= 1 &&
            snapshot.state.gold >= 0 &&
            snapshot.state.world.day >= 1 &&
            snapshot.state.party.isNotEmpty()
        return ValidationResult(
            isValid      = isValid,
            version      = snapshot.version,
            isCompatible = compatible,
            message = when {
                !compatible -> "Niezgodna wersja zapisu (${snapshot.version} vs $SAVE_VERSION)"
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
