package com.grimreich.core

import com.grimreich.grimreich.v1.Faction

sealed class GameEvent {
    data class QuestCompleted(val questId: String) : GameEvent()
    data class FactionChanged(val faction: String, val delta: Int) : GameEvent()
    data class SettlementAttacked(val settlementId: String) : GameEvent()
    data class ItemCrafted(val itemId: String) : GameEvent()
    object DayPassed : GameEvent()
}

typealias GameEventListener = (GameEvent) -> Unit

object GameEventBus {

    private val listeners = mutableListOf<GameEventListener>()

    fun subscribe(listener: GameEventListener) {
        listeners += listener
    }

    fun unsubscribe(listener: GameEventListener) {
        listeners -= listener
    }

    fun emit(event: GameEvent) {
        listeners.toList().forEach { it(event) }
    }
}
