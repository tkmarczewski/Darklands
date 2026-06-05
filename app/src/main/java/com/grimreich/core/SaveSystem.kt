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
            version = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label = label.ifEmpty { "Save ${slotId + 1}" },
            state = gameState
        )
        slots[slotId] = snapshot
        return snapshot
    }

    fun load(slotId: Int = 0): SaveSnapshot? = slots[slotId]

    fun isCompatible(snapshot: SaveSnapshot): Boolean {
        return snapshot.version >= 1 && snapshot.version <= SAVE_VERSION
    }

    fun getSlot(slotId: Int): SaveSlot = SaveSlot(
        slotId = slotId,
        snapshot = slots[slotId]
    )

    fun getAllSlots(count: Int = 3): List<SaveSlot> =
        (0 until count).map { getSlot(it) }

    fun deleteSlot(slotId: Int) {
        slots.remove(slotId)
    }

    // ==================== AUTOSAVE ====================

    fun autoSave(gameState: GameState): Boolean {
        val hash = computeStateHash(gameState)
        if (hash == lastAutoSaveHash) return false // brak zmian

        autoSaveSnapshot = SaveSnapshot(
            version = SAVE_VERSION,
            timestamp = System.currentTimeMillis(),
            label = "Autosave",
            state = gameState
        )
        lastAutoSaveHash = hash
        return true
    }

    fun loadAutoSave(): SaveSnapshot? = autoSaveSnapshot

    fun hasAutoSave(): Boolean = autoSaveSnapshot != null

    private fun computeStateHash(state: GameState): Int {
        return state.hashCode()
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
        return ValidationResult(
            isValid = snapshot.state != null,
            version = snapshot.version,
            isCompatible = compatible,
            message = when {
                !compatible -> "Niezgodna wersja zapisu (${snapshot.version} vs $SAVE_VERSION)"
                snapshot.state == null -> "Zapis jest pusty lub uszkodzony"
                else -> "Zapis poprawny (wersja ${snapshot.version})"
            }
        )
    }

    fun migrateIfNeeded(snapshot: SaveSnapshot): SaveSnapshot {
        if (snapshot.version == SAVE_VERSION) return snapshot
        // Migracja z wersji 1/2 do 3 — bazowa ścieżka
        return snapshot.copy(version = SAVE_VERSION)
    }
}
