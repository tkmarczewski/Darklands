package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.OtherSideExpedition
import kotlin.random.Random

object OtherSideSystem {
    
    fun processExpedition(expedition: OtherSideExpedition): String {
        val g = GameRepository.state
        val party = g.party
        if (party.isEmpty()) return "Brak drużyny do ekspedycji."
        
        val risk = expedition.difficultyTier * 10
        val sb = StringBuilder()
        sb.appendLine("Ekspedycja: ${expedition.expeditionName}")
        
        for (hero in party) {
            val sanityLoss = Random.nextInt(5, 15) + (expedition.difficultyTier * 2)
            hero.sanity = (hero.sanity - sanityLoss).coerceAtLeast(0)
            
            if (Random.nextInt(100) < risk) {
                val corrGain = Random.nextInt(2, 8)
                hero.corruption = (hero.corruption + corrGain).coerceAtMost(100)
                sb.appendLine("${hero.name}: Strata poczytalności (-$sanityLoss). Wzrost korupcji (+$corrGain).")
            } else {
                sb.appendLine("${hero.name}: Strata poczytalności (-$sanityLoss).")
            }
        }
        
        val rewardGold = expedition.difficultyTier * 200 + Random.nextInt(100)
        g.gold += rewardGold
        sb.appendLine("Zysk z Drugiej Strony: +$rewardGold złota.")
        
        return sb.toString()
    }
}
