package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdvancedTactics @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applySynergies(hero: Hero) {
        if (hero.skills.getOrDefault("ALCH", 0) >= 15 && hero.intelligence > 12) {
            gameRepository.log("${hero.name} odkrył synergię alchemiczną!")
        }
    }
}

enum class ReactionTrigger { ON_MELEE_HIT, ON_RANGED_HIT, ON_DAMAGE_TAKEN, ON_SPELL_CAST }
