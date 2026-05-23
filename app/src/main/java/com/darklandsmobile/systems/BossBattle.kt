package com.darklandsmobile.systems

import com.darklandsmobile.core.GameState

data class BossState(
    var phase: Int = 1,
    var hp: Int = 120,
    var morale: Int = 100,
    var armor: Int = 15,
    var statusEffects: MutableList<String> = mutableListOf()
)

object BossBattleSystem {
    fun startBoss(gameState: GameState): BossState = BossState()

    fun attackBoss(boss: BossState, gameState: GameState): String {
        val playerAtk = 10 + (gameState.equipment.attackBonus())
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
            boss.hp <= 0 -> "Pokonałeś bossa! Faza ${boss.phase}. Zadano $dmg obrażeń."
            boss.phase == 3 -> "Boss w fazie 3 (skażona aura)! Zadano $dmg obrażeń. HP: ${boss.hp}"
            boss.phase == 2 -> "Boss wściekły! Zadano $dmg obrażeń. HP: ${boss.hp}"
            else -> "Zadano $dmg obrażeń. Boss HP: ${boss.hp}"
        }
    }

    fun bossTurn(boss: BossState, gameState: GameState): String {
        val baseDmg = when (boss.phase) {
            1 -> 8
            2 -> 14
            3 -> 20
            else -> 8
        }
        val dmg = maxOf(1, baseDmg - gameState.equipment.defenseBonus() / 2)
        gameState.battle.hp -= dmg
        gameState.battle.morale -= 8
        return "Boss atakuje! Tracisz $dmg HP. Twoje HP: ${gameState.battle.hp}"
    }

    fun isDefeated(boss: BossState) = boss.hp <= 0
    fun isPlayerDefeated(gameState: GameState) = gameState.battle.hp <= 0
}
