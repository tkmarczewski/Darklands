package com.grimreich.systems

import com.grimreich.core.Hero

object ExperienceSystem {
    
    fun addXp(hero: Hero, amount: Int): String {
        hero.xp += amount
        val threshold = hero.level * 100
        return if (hero.xp >= threshold) {
            hero.level += 1
            hero.xp -= threshold
            hero.attributePoints += 2
            "${hero.name} awansuje na poziom ${hero.level}! Zyskał 2 punkty atrybutów."
        } else {
            "${hero.name} zyskał $amount XP."
        }
    }
}
