package com.grimreich.systems

import com.grimreich.core.PlayerState
import com.grimreich.core.SaveState

/**
 * In-memory save system placeholder.
 * Can later be backed by DataStore, Room or file serialization.
 */
object SaveLoadSystem {
    private var saveSlot: SaveState? = null

    fun save(playerState: PlayerState, lastResolutionSummary: String? = null): SaveState {
        val snapshot = SaveState(
            playerState = playerState,
            lastResolutionSummary = lastResolutionSummary,
            version = 1
        )
        saveSlot = snapshot
        return snapshot
    }

    fun load(): SaveState? = saveSlot

    fun hasSave(): Boolean = saveSlot != null

    fun clear() {
        saveSlot = null
    }
}
