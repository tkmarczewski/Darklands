package com.grimreich.core

/**
 * Implements advanced tactical layers: Mutation Synergies and Action Economy.
 */
object AdvancedTactics {

    fun applySynergies(hero: Hero) {
        val activeMutationTypes = hero.abilities.map { it.id.uppercase() }.toSet()
        
        // Example Synergy: BLOOD + SHADOW = BLOOD MIST
        if (activeMutationTypes.contains("BLOOD") && activeMutationTypes.contains("SHADOW")) {
            hero.morale += 15
            hero.agility += 2
        }
    }

    fun handleReaction(state: CombatState, trigger: ReactionTrigger, entityId: String): CombatState {
        // Logic for processing Parry/Dodge/Counter reactions
        return state
    }
}

enum class ReactionTrigger {
    ON_MELEE_HIT,
    ON_RANGED_HIT,
    ON_DAMAGE_TAKEN,
    ON_SPELL_CAST
}
