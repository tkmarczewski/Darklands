package com.grimreich.systems

import com.grimreich.core.GameConstants
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Season
import com.grimreich.core.WeatherType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class StabilitySystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun updateStability(delta: Int) {
        gameRepository.updateState { state ->
            updateStabilityDirect(state, delta)
        }
    }

    fun updateStabilityDirect(state: GameState, delta: Int) {
        val current = state.world.globalStability
        val next = (current + delta).coerceIn(0, 100)
        state.world.globalStability = next
        
        if (delta < 0) state.logEntries.add("Stabilność rzeczywistości słabnie...")
        
        // CULMINATION: Zero Stability Logic
        if (next == 0 && current > 0) {
            triggerCollapseDirect(state)
        }
    }

    fun applyAtmosphericEffectsDirect(state: GameState) {
        val world = state.world
        val stability = world.globalStability

        // Project Cipher: Weather Glitches
        if (stability < GameConstants.STABILITY_ATMOSPHERE_GLITCH && Random.nextFloat() < 0.2f) {
            world.weather = WeatherType.entries.random() // Erratic weather
            state.logEntries.add("!!! BŁĄD ATMOSFERY: Pogoda traci spójność !!!")
        }

        // Seasonal Modifiers
        state.party.forEach { hero ->
            when (world.season) {
                Season.winter -> {
                    // Winter is harsh on stamina
                    hero.endurance = (hero.endurance - 1).coerceAtLeast(5)
                }
                Season.summer -> {
                    // Summer improves morale but increases fatigue if stability is low
                    if (stability < 50) {
                        world.fatigue = (world.fatigue + 1).coerceAtMost(100)
                    }
                }
                else -> {}
            }
            // FIX: Normalize after stat changes to update HP etc.
            hero.normalize()
        }
    }

    private fun triggerCollapseDirect(state: GameState) {
        state.logEntries.add("!!! PARADYGMAT ULEGŁ CAŁKOWITEMU ROZPADOWI !!!")
        state.world.echoIntensity = 1.0f
        state.world.collapseProgress = 1.0f
        
        // BUG FIX: One-time heavy penalty instead of infinite drain to prevent soft-lock
        state.party.forEach { hero ->
            hero.sanity = (hero.sanity - 50).coerceAtLeast(0)
            hero.hp = (hero.hp - 20).coerceAtLeast(1) // Leave at least 1 HP to avoid instant wipe
            hero.normalize()
        }

        state.world.weather = WeatherType.storm // Permanent storm in collapse
    }

    fun getStabilityModifier(): Float {
        val stability = gameRepository.currentState().world.globalStability
        return when {
            stability >= 90 -> 1.0f
            stability >= 50 -> 0.8f
            stability >= 20 -> 0.5f
            else -> 0.2f
        }
    }
}
