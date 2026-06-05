package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.OtherSideExpedition
import com.grimreich.world.ItemCatalogue
import kotlin.random.Random

object OtherSideSystem {
    
    fun processExpedition(expedition: OtherSideExpedition, depth: Int = 1): String {
        val g = GameRepository.state
        val party = g.party
        if (party.isEmpty()) return "Brak drużyny do ekspedycji."
        
        val effectiveDifficulty = expedition.difficultyTier + depth
        val risk = effectiveDifficulty * 12
        val sb = StringBuilder()
        sb.appendLine("Ekspedycja: ${expedition.expeditionName} (Głębokość: $depth)")
        
        for (hero in party) {
            // Sanity test
            val sanityLoss = Random.nextInt(5, 10) * depth + (effectiveDifficulty * 2)
            hero.sanity = (hero.sanity - sanityLoss).coerceAtLeast(0)
            
            // Vision check
            if (Random.nextInt(100) < 40) {
                val visionEffect = Random.nextInt(1, 4)
                when (visionEffect) {
                    1 -> {
                        hero.corruption += 3
                        sb.appendLine("${hero.name}: Doznał wizji mroku (+3 Korupcja).")
                    }
                    2 -> {
                        hero.virtue += 2
                        sb.appendLine("${hero.name}: Doznał przebłysku nadziei (+2 Cnota).")
                    }
                    3 -> {
                        hero.morale = (hero.morale - 10).coerceAtLeast(0)
                        sb.appendLine("${hero.name}: Zobaczył własną śmierć (-10 Morale).")
                    }
                }
            }
            
            // Corruption check
            if (Random.nextInt(100) < risk) {
                val corrGain = Random.nextInt(3, 10)
                hero.corruption = (hero.corruption + corrGain).coerceAtMost(100)
                sb.appendLine("${hero.name}: Skażenie mrokiem (+$corrGain).")
            }
            
            sb.appendLine("${hero.name}: Strata poczytalności (-$sanityLoss).")
        }
        
        // Rewards
        val goldMult = 1.0f + (depth * 0.5f)
        val rewardGold = (expedition.difficultyTier * 200 * goldMult).toInt() + Random.nextInt(100)
        g.gold += rewardGold
        sb.appendLine("Zysk z Drugiej Strony: +$rewardGold złota.")
        
        // Relic find chance
        if (Random.nextFloat() < (0.1f * depth)) {
            val relic = ItemCatalogue.all().filter { it.type == "relic" }.randomOrNull()
            if (relic != null) {
                g.inventory.add(relic)
                sb.appendLine("ZNALEZIONO RELIKT: ${relic.name}!")
            }
        }
        
        return sb.toString()
    }
}
