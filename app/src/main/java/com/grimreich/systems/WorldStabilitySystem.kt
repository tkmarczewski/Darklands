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
        
        // Project Anchor: In the "Iron Fortress" (Krew), every action costs 1 HP
        if (state.world.locationId == "twierdza_zelazna" && delta != 0) {
            state.party.forEach { hero ->
                hero.hp = (hero.hp - 1).coerceAtLeast(1) // Anchor tax: cannot kill, but weakens
            }
            state.logEntries.add("Krew: Kotwica porusza się, a Naczynia krwawią.")
        }

        state.world.globalStability += delta
        state.normalizeState()
        val after = state.world.globalStability
        
        if (before != after) {
            state.logEntries.add("TRIBUNAL_LOG: Stabilność: $after. Powód: $reason")
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
        
        // BUG FIX: Prevent log spam by only logging significant changes (>= 1% or crossing threshold)
        val beforePct = (before * 100).toInt()
        val afterPct = (after * 100).toInt()
        
        if (beforePct != afterPct) {
            state.logEntries.add("Upadek: $afterPct%. Powód: $reason")
        }
    }

    /**
     * Centrally advances the world day and triggers related events.
     */
    fun advanceDayDirect(state: GameState, reason: String) {
        state.world.day += 1
        state.logEntries.add("Dzień ${state.world.day}: $reason")
        advanceCollapseDirect(state, CollapseEvent.DayEnded)
    }
}
