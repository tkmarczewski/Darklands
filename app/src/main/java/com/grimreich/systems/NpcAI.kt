package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import kotlin.random.Random

object NpcAI {
    
    fun tickNpc(hero: Hero) {
        val intensity = GameRepository.state.world.echoIntensity
        if (intensity < 0.3f) return
        
        // Memory Leak
        if (Random.nextFloat() < intensity * 0.1f) {
            hero.sanity -= 5
            ChronicleSystem.record("${hero.name} doświadczył wycieku pamięci.")
        }
        
        // Identity Split
        if (intensity > 0.7f && Random.nextFloat() < 0.05f) {
            hero.corruption += 10
            ChronicleSystem.record("Tożsamość ${hero.name} zaczyna pękać.")
        }
    }
}
