package com.darklandsmobile.core

object GameBootstrap {
    fun init(state: GameState): GameState {
        seedGrimWorld(state.grimEngine)
        return state
    }
    fun initialize(): GameState = init(GameState())
    private fun seedGrimWorld(engine: com.darklandsmobile.grimreich.v1.GrimWorldEngine) { }
}
