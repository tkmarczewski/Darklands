package com.grimreich.systems

import com.grimreich.core.GameRepository

object StabilitySystem {
    
    fun updateStability() {
        val g = GameRepository.state
        val avgCorruption = if (g.party.isNotEmpty()) g.party.map { it.corruption }.average().toInt() else 0
        
        // Stability drops as average corruption rises
        g.world.globalStability = (100 - avgCorruption).coerceIn(0, 100)
    }
    
    fun getStabilityEffectModifier(): Float {
        val stability = GameRepository.state.world.globalStability
        return if (stability < 30) 1.5f else if (stability < 70) 1.2f else 1.0f
    }
}
