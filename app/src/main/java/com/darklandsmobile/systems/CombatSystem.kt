package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.PartyRepository
import kotlin.math.max

object CombatSystem {
    fun startCombat(enemyName: String, enemyHp: Int, enemyAttack: Int, enemyDefense: Int) {
        val c = GameRepository.state.combat
        c.active      = true
        c.round       = 0
        c.enemyName   = enemyName
        c.enemyHp     = enemyHp
        c.enemyMaxHp  = enemyHp
        c.enemyAttack = enemyAttack
        c.enemyDefense = enemyDefense
        c.log.clear()
        c.log.add("Walka z $enemyName rozpoczeta!")
        GameRepository.log("Walka z $enemyName!")
    }

    fun playerAttack(): String {
        val c = GameRepository.state.combat
        val hero = PartyRepository.activeHero() ?: return "Brak bohatera"
        val dmg = max(1, hero.strength - c.enemyDefense + (1..6).random())
        c.enemyHp -= dmg
        val msg = "${hero.name} zadaje $dmg obrazen. HP wroga: ${max(0, c.enemyHp)}/${c.enemyMaxHp}"
        c.log.add(msg)
        if (c.enemyHp <= 0) {
            c.active = false
            c.log.add("${c.enemyName} pokonany!")
            GameRepository.log("Pokonano ${c.enemyName}")
        }
        return msg
    }

    fun enemyAttack(): String {
        val c = GameRepository.state.combat
        val hero = PartyRepository.activeHero() ?: return "Brak bohatera"
        val dmg = max(1, c.enemyAttack - 2 + (1..4).random())
        hero.hp -= dmg
        val msg = "${c.enemyName} zadaje $dmg obrazen. HP ${hero.name}: ${max(0, hero.hp)}/${hero.maxHp}"
        c.log.add(msg)
        if (hero.hp <= 0) {
            c.active = false
            c.log.add("${hero.name} pokonany...")
        }
        return msg
    }

    fun isCombatActive() = GameRepository.state.combat.active
}
