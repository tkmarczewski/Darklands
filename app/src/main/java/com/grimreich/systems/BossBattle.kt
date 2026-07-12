package com.grimreich.systems

import com.grimreich.core.GameState
import com.grimreich.core.PartyRepository
import javax.inject.Inject
import javax.inject.Singleton

data class BossState(
    var phase: Int = 1,
    var hp: Int = 120,
    var morale: Int = 100,
    var armor: Int = 15,
    var statusEffects: MutableList<String> = mutableListOf()
)

@Deprecated("Replaced by CombatSystem with advanced encounter logic. Scheduled for deletion.")
@Singleton
class BossBattleSystem @Inject constructor(
    private val partyRepository: PartyRepository,
    private val gameRepository: com.grimreich.core.GameRepository
) {
    fun startBoss(gameState: GameState): BossState = BossState()

    fun attackBoss(boss: BossState, gameState: GameState): String {
        val playerAtk = 10 + heroAttackBonus(gameState)
        val dmg = maxOf(1, playerAtk - boss.armor / 2)
        boss.hp -= dmg
                boss.morale = (boss.morale - 5).coerceAtLeast(0)

        if (boss.hp <= 72 && boss.phase == 1) {
            boss.phase = 2
            boss.armor += 5
            if (!boss.statusEffects.contains("enraged")) {
                boss.statusEffects.add("enraged")
            }
        }
        if (boss.hp <= 36 && boss.phase == 2) {
            boss.phase = 3
            if (!boss.statusEffects.contains("corrupted_aura")) {
                boss.statusEffects.add("corrupted_aura")
            }
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
        val dmg = maxOf(1, baseDmg - heroDefenseBonus(gameState) / 2)
        val activeId = gameState.activeHeroId ?: return "Brak aktywnego bohatera."
        
        var finalHp = 0
        gameRepository.updateState { s ->
            val h = s.party.find { it.id == activeId }
            if (h != null) {
                h.hp = (h.hp - dmg).coerceAtLeast(0)
                finalHp = h.hp
            }
        }
        
        return "Boss atakuje! Tracisz $dmg HP. Twoje HP: $finalHp"
    }

    fun isDefeated(boss: BossState) = boss.hp <= 0

    fun isPlayerDefeated(gameState: GameState) =
        (partyRepository.activeHero()?.hp ?: 0) <= 0

    private fun heroAttackBonus(gameState: GameState): Int {
        val hero = partyRepository.activeHero() ?: return 0
        val equippedIds = hero.equipment.values.filterNotNull()
        return gameState.inventory
            .filter { it.instanceId in equippedIds }
            .sumOf { it.effects["attack"] ?: 0 }
    }

    private fun heroDefenseBonus(gameState: GameState): Int {
        val hero = partyRepository.activeHero() ?: return 0
        val equippedIds = hero.equipment.values.filterNotNull()
        return gameState.inventory
            .filter { it.instanceId in equippedIds }
            .sumOf { it.effects["defense"] ?: 0 }
    }
}
