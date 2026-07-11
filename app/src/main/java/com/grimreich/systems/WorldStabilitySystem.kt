package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton

sealed interface CollapseEvent {
    data object DayEnded : CollapseEvent
    data class TravelCompleted(val fatigueDelta: Int) : CollapseEvent
    data class QuestFailed(val questId: String) : CollapseEvent
    data object RealityRitualUsed : CollapseEvent
}

@Singleton
class WorldStabilitySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    /**
     * Zmienia stabilność świata. 
     */
    fun changeStability(delta: Int, reason: String) {
        gameRepository.updateState { state ->
            changeStabilityDirect(state, delta, reason)
        }
    }

    fun changeStabilityDirect(state: GameState, delta: Int, reason: String) {
        val before = state.world.globalStability
        state.world.globalStability += delta
        state.normalizeState()
        val after = state.world.globalStability
        
        if (before != after) {
            state.logEntries.add("Stabilność: $after (${if (delta > 0) "+" else ""}$delta). Powód: $reason")
        }
    }

    /**
     * Zmienia intensywność echa (0.0 - 1.0).
     */
    fun changeEcho(delta: Float, reason: String) {
        gameRepository.updateState { state ->
            changeEchoDirect(state, delta, reason)
        }
    }

    fun changeEchoDirect(state: GameState, delta: Float, reason: String) {
        val before = state.world.echoIntensity
        state.world.echoIntensity += delta
        state.normalizeState()
        val after = state.world.echoIntensity
        
        if (before != after) {
            state.logEntries.add("Echa: ${"%.2f".format(after)}. Powód: $reason")
        }
    }

    /**
     * Przesuwa postęp upadku rzeczywistości na podstawie zdarzenia.
     */
    fun advanceCollapse(event: CollapseEvent) {
        gameRepository.updateState { state ->
            advanceCollapseDirect(state, event)
        }
    }

    fun advanceCollapseDirect(state: GameState, event: CollapseEvent) {
        val (delta, reason) = when (event) {
            CollapseEvent.DayEnded -> 0.05f to "Koniec dnia"
            is CollapseEvent.TravelCompleted -> (event.fatigueDelta / 1000f) to "Wyczerpująca podróż"
            is CollapseEvent.QuestFailed -> 0.03f to "Porażka w zadaniu: ${event.questId}"
            CollapseEvent.RealityRitualUsed -> 0.10f to "Rytuał naruszenia rzeczywistości"
        }

        val before = state.world.collapseProgress
        state.world.collapseProgress += delta
        state.normalizeState()
        val after = state.world.collapseProgress
        
        if (before != after) {
            state.logEntries.add("Upadek: ${"%.1f".format(after * 100)}%. Powód: $reason")
        }
    }
}
