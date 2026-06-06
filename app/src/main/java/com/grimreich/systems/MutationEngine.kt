package com.grimreich.systems

import com.grimreich.core.Hero
import kotlin.random.Random

enum class OntologicalMutationType {
    PHYSICAL, EMOTIONAL, MEMORY, IDENTITY, ABSOLUTE
}

object MutationEngine {
    
    fun applyMutation(hero: Hero, type: OntologicalMutationType): String {
        return when (type) {
            OntologicalMutationType.PHYSICAL -> {
                hero.strength += 2
                hero.maxHp += 5
                "Ciało ${hero.name} twardnieje pod wpływem mroku."
            }
            OntologicalMutationType.MEMORY -> {
                hero.intelligence += 3
                "Pamięć ${hero.name} rozszerza się o wizje innych żywotów."
            }
            OntologicalMutationType.ABSOLUTE -> {
                hero.corruption += 10
                hero.virtue -= 5
                "Dotyk Absolutu zmienia samą esencję ${hero.name}."
            }
            else -> "Wpływ fenomenów wywołuje dziwne dreszcze."
        }
    }
    
    fun checkRandomMutation(hero: Hero) {
        val g = com.grimreich.core.GameRepository.state
        if (hero.corruption > 50 && Random.nextFloat() < g.world.collapseProgress * 0.1f) {
            val type = OntologicalMutationType.entries.random()
            applyMutation(hero, type)
        }
    }
}
