package com.grimreich.core

import com.grimreich.grimreich.v1.*

@Deprecated("Use GameRepository and GameState instead. Scheduled for deletion.")
data class GrimGameState(
    val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),
    var currentRegion: String = "Schwarzwald",
    var pendingExpeditionName: String? = null
) {
    fun triggerWorldCollapse(collapse: WorldCollapse) {
        grimEngine.updateWorldCollapse(collapse)
    }
}

@Deprecated("Use GameRepository and GameState instead. Scheduled for deletion.")
object GrimGameRepository {
    var state: GrimGameState = GrimGameState()
}
