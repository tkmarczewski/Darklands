package com.grimreich.systems

import com.grimreich.core.GameRepository
import kotlin.random.Random

object StabilitySystem {
    
    fun updateStability() {
        val g = GameRepository.state
        val avgCorruption = if (g.party.isNotEmpty()) g.party.map { it.corruption }.average().toInt() else 0
        
        // Stability drops as average corruption rises
        g.world.globalStability = (100 - avgCorruption).coerceIn(0, 100)
        
        // If stability is low, increase Echo intensity (Era of Fracture)
        if (g.world.globalStability < 50) {
            g.world.echoIntensity = (1.0f - (g.world.globalStability / 50.0f)).coerceIn(0.0f, 1.0f)
        } else {
            g.world.echoIntensity = 0.0f
        }
        
        // At very low stability, Collapse starts
        if (g.world.globalStability < 20) {
            g.world.collapseProgress = (1.0f - (g.world.globalStability / 20.0f)).coerceIn(0.0f, 1.0f)
        }
    }
    
    fun getStabilityEffectModifier(): Float {
        val stability = GameRepository.state.world.globalStability
        return if (stability < 30) 1.5f else if (stability < 70) 1.2f else 1.0f
    }
    
    fun checkFractureEvent(): String? {
        val g = GameRepository.state
        if (g.world.echoIntensity > 0.3f && Random.nextFloat() < g.world.echoIntensity * 0.2f) {
            return "Pęknięcie rzeczywistości! Widzisz obrazy z innych czasów."
        }
        return null
    }
}
