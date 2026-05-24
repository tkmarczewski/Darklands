package com.darklandsmobile.systems

import com.darklandsmobile.core.GameState
import com.darklandsmobile.core.PartyRepository

// Stan bossa w wieloetapowej walce finalowej (sprint 17).
data class BossState(
    var phase: Int = 1,
    var hp: Int = 120,
    var morale: Int = 100,
    var armor: Int = 15,
    var statusEffects: MutableList<String> = mutableListOf()
)

// Walka z bossem - liczy ataki wzgledem aktywnego bohatera z party + jego ekwipunku w inventory.
object BossBattleSystem {
    fun startBoss(gameState: GameState): BossState = BossState()

    fun attackBoss(boss: BossState, gameState: GameState): String {
        val playerAtk = 10 + heroAttackBonus(gameState)
        val dmg = maxOf(1, playerAtk - boss.armor / 2)
        boss.hp -= dmg
        boss.morale -= 5

        if (boss.hp <= 72 && boss.phase == 1) {
            boss.phase = 2
            boss.armor += 5
            boss.statusEffects.add("enraged")
        }
        if (boss.hp <= 36 && boss.phase == 2) {
            boss.phase = 3
            boss.statusEffects.add("corrupted_aura")
        }

        return when {
            boss.hp <= 0 -> "Pokonales bossa! Faza ${boss.phase}. Zadano $dmg obrazen."
            boss.phase == 3 -> "Boss w fazie 3 (skazona aura)! Zadano $dmg obrazen. HP: ${boss.hp}"
            boss.phase == 2 -> "Boss wsciekly! Zadano $dmg obrazen. HP: ${boss.hp}"
            else -> "Zadano $dmg obrazen. Boss HP: ${boss.hp}"
        }
    }

    fun bossTurn(boss: BossState, gameState: GameState): String {
        val baseDmg = when (boss.phase) {
            1 -> 8
            2 -> 14
            3 -> 20
            else -> 8
        }
        val dmg = maxOf(1, baseDmg - heroDefenseBonus(gameState) / 2)
        val hero = PartyRepository.activeHero() ?: return "Brak aktywnego bohatera."
        hero.hp = (hero.hp - dmg).coerceAtLeast(0)
        return "Boss atakuje! Tracisz $dmg HP. Twoje HP: ${hero.hp}"
    }

    fun isDefeated(boss: BossState) = boss.hp <= 0
    fun isPlayerDefeated(gameState: GameState) =
        (PartyRepository.activeHero()?.hp ?: 0) <= 0

    // Atak/obrona liczone z efektow zalozonych przedmiotow aktywnego bohatera.
    private fun heroAttackBonus(gameState: GameState): Int {
        val hero = PartyRepository.activeHero() ?: return 0
        val equippedIds = hero.equipment.values.filterNotNull()
        return gameState.inventory
            .filter { it.id in equippedIds }
            .sumOf { it.effects["attack"] ?: 0 }
    }

    private fun heroDefenseBonus(gameState: GameState): Int {
        val hero = PartyRepository.activeHero() ?: return 0
        val equippedIds = hero.equipment.values.filterNotNull()
        return gameState.inventory
            .filter { it.id in equippedIds }
            .sumOf { it.effects["defense"] ?: 0 }
    }
}
