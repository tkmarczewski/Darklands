package com.darklandsmobile.core

import com.darklandsmobile.grimreich.v1.*

data class GrimGameState(
    val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),
    var currentRegion: String = "Schwarzwald",
    var pendingExpeditionName: String? = null
) {
    fun triggerWorldCollapse(collapse: WorldCollapse) {
        grimEngine.updateWorldCollapse(collapse)
    }
}

object GrimGameRepository {
    var state: GrimGameState = GrimGameState()
}
