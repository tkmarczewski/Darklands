package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero

object ChurchSystem {
    
    fun pray(hero: Hero): String {
        hero.divineFavor = (hero.divineFavor + 10).coerceAtMost(150)
        hero.virtue += 1
        return "${hero.name} modli się żarliwie. (+10 Divine Favor, +1 Cnota)"
    }
    
    fun cleanseRelic(hero: Hero): String {
        if (hero.corruption <= 0) return "${hero.name} nie jest skażony mrokiem."
        
        val cost = hero.corruption * 5
        if (GameRepository.state.gold < cost) return "Brak złota na ceremonię oczyszczenia (potrzeba $cost)."
        
        GameRepository.state.gold -= cost
        val reduction = hero.corruption / 2 + 5
        hero.corruption = (hero.corruption - reduction).coerceAtLeast(0)
        hero.sanity = (hero.sanity + 10).coerceAtMost(100)
        
        return "${hero.name} przeszedł rytuał oczyszczenia. Korupcja spadła o $reduction. Poczytalność wzrosła."
    }
}
