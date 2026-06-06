package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.SaintCatalogue

object ReligionSystem {

    fun pray(hero: Hero): String {
        val s = GameRepository.state.prayer
        hero.piety += 5
        s.faith = (s.faith + 10).coerceAtMost(150)
        
        return "${hero.name} odmawia mroczną modlitwę. Wiara wzrasta."
    }

    fun getBlessing(): String {
        val s = GameRepository.state.prayer
        val vision = "Wizja " + listOf("Cienia", "Krwi", "Mgły", "Lustra").random()
        s.blessings.add(vision)
        return "Prorocy zesłali wizję: $vision"
    }

    fun allSaints() = SaintCatalogue.all()
    
    fun getFaith() = GameRepository.state.prayer.faith
}
