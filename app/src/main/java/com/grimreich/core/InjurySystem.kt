package com.grimreich.core

/**
 * Manages the acquisition and impact of permanent wounds.
 */
object InjurySystem {

    fun applyInjury(hero: Hero, severity: Int) {
        when (severity) {
            1 -> hero.hp -= 5
            2 -> {
                hero.maxHp -= 2
                hero.hp = hero.hp.coerceAtMost(hero.maxHp)
            }
            3 -> {
                hero.strength -= 1
                hero.sanity -= 10
            }
        }
    }
}
