package com.darklandsmobile.core

import com.darklandsmobile.grimreich.v1.GrimWorldEngine
import com.darklandsmobile.grimreich.v1.GrimWorldEngineFactory

data class GameState(
    val grimEngine: GrimWorldEngine = GrimWorldEngineFactory.create(),
    var grimCurrentRegion: String = "Wybrzeże Północne",
    var grimPendingExpeditionName: String? = null
) {
    fun deepCopy(): GameState = GameState(
        grimEngine = grimEngine,
        grimCurrentRegion = grimCurrentRegion,
        grimPendingExpeditionName = grimPendingExpeditionName
    )
}
