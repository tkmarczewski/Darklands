package com.darklandsmobile.systems

import com.darklandsmobile.core.CombatRound
import com.darklandsmobile.core.CombatantState
import com.darklandsmobile.core.EquippedItems
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.MoraleSystem
import com.darklandsmobile.core.PartyRepository
import com.darklandsmobile.core.WoundType

object CombatSystem {

    // Build a CombatantState from the active hero
    private fun heroToCombatant(): CombatantState? {
        val hero = PartyRepository.activeHero() ?: return null
        val armorValue = if (hero.equipment["armor"] != null) 3 else 0
        return CombatantState(
            name        = hero.name,
            hp          = hero.hp,
            maxHp       = hero.maxHp,
            endurance   = hero.endurance,
            morale      = 70,
            armor       = armorValue,
            attackBase  = hero.strength
        )
    }

    fun startCombat(enemyName: String, enemyHp: Int, enemyAttack: Int, enemyDefense: Int) {
        val c = GameRepository.state.combat
        c.active        = true
        c.round         = 0
        c.enemyName     = enemyName
        c.enemyHp       = enemyHp
        c.enemyMaxHp    = enemyHp
        c.enemyAttack   = enemyAttack
        c.enemyDefense  = enemyDefense
        c.log.clear()
        c.log.add("Walka z $enemyName rozpoczeta!")
        GameRepository.log("Walka z $enemyName!")
    }

    fun playerAttack(): String {
        val c    = GameRepository.state.combat
        val hero = PartyRepository.activeHero() ?: return "Brak bohatera"
        if (!c.active) return "Brak aktywnej walki"

        val heroState = heroToCombatant() ?: return "Brak bohatera"
        val enemyState = CombatantState(
            name       = c.enemyName,
            hp         = c.enemyHp,
            maxHp      = c.enemyMaxHp,
            endurance  = c.enemyHp / 2,
            morale     = 60,
            armor      = c.enemyDefense,
            attackBase = c.enemyAttack
        )

        val result = CombatRound.resolveRound(
            attacker        = heroState,
            defender        = enemyState,
            attackerEquipped = EquippedItems()
        )
        c.round++

        // Apply results back to legacy CombatState
        c.enemyHp = enemyState.hp
        hero.hp   = heroState.hp
        c.log.addAll(result.log)

        // Sync morale log
        val heroMorale   = MoraleSystem.computeStatus(result.attackerMorale)
        val enemyMorale  = MoraleSystem.computeStatus(result.defenderMorale)
        val woundMsg     = if (result.defenderWound != WoundType.NONE)
            " [Rana ${c.enemyName}: ${result.defenderWound}]" else ""
        val heroWoundMsg = if (result.attackerWound != WoundType.NONE)
            " [Rana ${hero.name}: ${result.attackerWound}]" else ""

        val defeated = CombatRound.isDefeated(enemyState)
        if (defeated) {
            c.active = false
            c.log.add("${c.enemyName} pokonany!")
            GameRepository.log("Pokonano ${c.enemyName}")
            val recovery = CombatRound.postCombatRecovery(heroState)
            hero.hp = heroState.hp
            c.log.add(recovery)
        }
        if (CombatRound.isDefeated(heroState)) {
            c.active = false
            c.log.add("${hero.name} pokonany...")
        }

        val summary = result.log.joinToString(" | ")
        GameRepository.log(summary)
        return "Runda ${c.round}: $summary" +
               " | Morale: $heroMorale vs $enemyMorale" +
               woundMsg + heroWoundMsg
    }

    fun isCombatActive() = GameRepository.state.combat.active

    fun getCombatLog(): List<String> = GameRepository.state.combat.log
}
