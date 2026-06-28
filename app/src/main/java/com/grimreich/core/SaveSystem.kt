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
            state     = gameState
        )
        slots[slotId] = snapshot
        return snapshot
    }

    fun load(slotId: Int = 0): SaveSnapshot? = slots[slotId]

    fun isCompatible(snapshot: SaveSnapshot): Boolean {
        return snapshot.version >= 1 && snapshot.version <= SAVE_VERSION
    }

    fun getSlot(slotId: Int): SaveSlot = SaveSlot(
        slotId   = slotId,
        snapshot = slots[slotId]
    )

    fun getAllSlots(count: Int = 3): List<SaveSlot> = (0 until count).map { getSlot(it) }

    fun deleteSlot(slotId: Int) { slots.remove(slotId) }

    fun importSlots(persisted: Map<Int, SaveSnapshot>) {
        slots.clear()
        slots.putAll(persisted)
    }

    fun exportSlots(): Map<Int, SaveSnapshot> = slots.toMap()

    // ==================== AUTOSAVE ====================
    fun autoSave(gameState: GameState): Boolean {
        val hash = computeStateHash(gameState)
        if (hash == lastAutoSaveHash) return false
        autoSaveSnapshot = SaveSnapshot(
            version   = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label     = "Autosave",
            state     = gameState
        )
        lastAutoSaveHash = hash
        return true
    }

    fun loadAutoSave(): SaveSnapshot? = autoSaveSnapshot
    fun hasAutoSave(): Boolean = autoSaveSnapshot != null

    // FIX BUG-07: Deterministic hash based on key state fields instead of fragile hashCode()
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

    // FIX BUG-08: Actually validate key state fields instead of always returning true
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
        // Migracja z wersji 1/2 do 3 — bazowa ścieżka
        return snapshot.copy(version = SAVE_VERSION)
    }
}
