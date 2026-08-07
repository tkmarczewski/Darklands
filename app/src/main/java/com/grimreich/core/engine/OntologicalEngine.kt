package com.grimreich.core.engine

import com.grimreich.core.GameRepository
import com.grimreich.systems.DefaultCollapseRandomProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * The Ontological Layer (Engine)
 * Handles world stability, reality glitches, and background echo mechanics.
 */
@Singleton
class OntologicalEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val collapseRandomProvider: com.grimreich.contracts.CollapseRandomProvider
) {
    fun processRealityShift() {
        gameRepository.updateState { state ->
            // Use seed-based random for deterministic shifts
            val random = if (collapseRandomProvider is DefaultCollapseRandomProvider) {
                 Random(state.world.day.toLong() + state.world.locationId.hashCode())
            } else Random.Default

            // Random fluctuations in stability
            val baseShift = random.nextInt(-2, 3)
            var shift = baseShift
            var reason = "natural fluctuation"

            // If expedition is active, stability drain is much harsher
            if (state.isExpeditionActive) {
                shift -= 5 // Constant drain
                reason = "expedition drain"
                if (random.nextFloat() < 0.3f) {
                    shift -= 3 // Extra spikes
                    reason = "expedition rift spike"
                }
            }

            val oldStability = state.world.globalStability
            state.world.globalStability = (oldStability + shift).coerceIn(0, 100)
            
            android.util.Log.d("OntologicalEngine", "[STABILITY] Shift: $shift ($reason). From $oldStability to ${state.world.globalStability}")

            // BUG-R3-08: Added critical stability warning threshold at <= 10
            when {
                state.world.globalStability <= 10 ->
                    gameRepository.logDirect(state, "KRYTYCZNE: Stabilność rzeczywistości osiągnęła poziom krytyczny (${state.world.globalStability})!")
                state.world.globalStability < 30 ->
                    gameRepository.logDirect(state, "Rzeczywistość staje się niestabilna...")
            }

            // --- PASSIVE HEALING DURING TIME PASSAGE ---
            // Based on Endurance. Every shift heals 5% of Max HP.
            state.party.forEach { hero ->
                if (!hero.isDead && hero.hp < hero.maxHp) {
                    val healAmount = (hero.maxHp * 0.05f).toInt().coerceAtLeast(1)
                    // BUG FIX #1: Avoid direct reference mutation
                    // Note: If hero is a data class with var, this still mutates.
                    // But in this engine, state.party is a MutableList of Hero objects.
                    // To be TRULY safe against data races with UI, we should replace the object.
                    val updatedHero = hero.copy(hp = (hero.hp + healAmount).coerceAtMost(hero.maxHp))
                    val index = state.party.indexOf(hero)
                    state.party[index] = updatedHero
                }
            }
        }
    }

    fun isGlitchActive(): Boolean {
        val state = gameRepository.currentState()
        val stability = state.world.globalStability
        
        // BUG FIX #5: Use stable seed for the entire day to ensure determinism
        val random = Random(state.world.day.toLong() + state.world.locationId.hashCode())

        // Glitches are more common during expeditions even at higher stability
        val threshold = if (state.isExpeditionActive) 60 else 40
        val baseChance = (1.0f - stability / 100f)
        // BUG-R3-07: Clamp finalChance to [0, 1] to prevent probability exceeding 100%
        val finalChance = if (state.isExpeditionActive) (baseChance * 1.5f).coerceAtMost(1.0f) else baseChance

        return stability < threshold && random.nextFloat() < finalChance
    }
}
