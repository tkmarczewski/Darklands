package com.darklandsmobile.core

import kotlin.random.Random

// ==================== MORALE SYSTEM ====================

enum class MoraleStatus {
    HEROIC, STEADY, SHAKEN, PANICKED, ROUTED;

    fun attackModifier(): Float = when (this) {
        HEROIC -> 1.2f
        STEADY -> 1.0f
        SHAKEN -> 0.8f
        PANICKED -> 0.5f
        ROUTED -> 0.0f
    }

    fun defenseModifier(): Float = when (this) {
        HEROIC -> 1.1f
        STEADY -> 1.0f
        SHAKEN -> 0.85f
        PANICKED -> 0.6f
        ROUTED -> 0.0f
    }
}

object MoraleSystem {
    fun computeStatus(morale: Int): MoraleStatus = when {
        morale >= 80 -> MoraleStatus.HEROIC
        morale >= 50 -> MoraleStatus.STEADY
        morale >= 30 -> MoraleStatus.SHAKEN
        morale >= 10 -> MoraleStatus.PANICKED
        else -> MoraleStatus.ROUTED
    }

    fun moraleAfterHit(morale: Int, dmgTaken: Int): Int =
        (morale - (dmgTaken / 2)).coerceAtLeast(0)

    fun moraleAfterKill(morale: Int): Int =
        (morale + 15).coerceAtMost(100)

    fun moraleAfterFlee(morale: Int): Int =
        (morale - 20).coerceAtLeast(0)
}

// ==================== COMBAT ROUND ====================

enum class WoundType {
    NONE, LIGHT, SERIOUS, CRITICAL
}

data class CombatantState(
    val name: String,
    var hp: Int,
    var maxHp: Int,
    var endurance: Int,
    var morale: Int = 80,
    var armor: Int = 0,
    var attackBase: Int = 5,
    var wounds: MutableList<WoundType> = mutableListOf()
)

data class RoundResult(
    val attackerDamage: Int,
    val defenderDamage: Int,
    val attackerMorale: Int,
    val defenderMorale: Int,
    val attackerWound: WoundType,
    val defenderWound: WoundType,
    val log: List<String>
)

object CombatRound {

    fun resolveRound(
        attacker: CombatantState,
        defender: CombatantState,
        attackerEquipped: EquippedItems = EquippedItems()
    ): RoundResult {
        val log = mutableListOf<String>()
        val attackerStatus = MoraleSystem.computeStatus(attacker.morale)
        val defenderStatus = MoraleSystem.computeStatus(defender.morale)

        // Atak
        val rawAtk = attacker.attackBase + attackerEquipped.totalAttack()
        val atkMod = attackerStatus.attackModifier()
        val defMod = defenderStatus.defenseModifier()
        val defArmor = defender.armor + attackerEquipped.totalDefense()

        val attackRoll = (rawAtk * atkMod * Random.nextFloat().let { 0.7f + it * 0.6f }).toInt()
        val defendRoll = (defArmor * defMod * Random.nextFloat().let { 0.5f + it * 0.5f }).toInt()

        val dmgToDefender = maxOf(1, attackRoll - defendRoll)
        defender.hp -= dmgToDefender
        defender.endurance = (defender.endurance - dmgToDefender / 2).coerceAtLeast(0)
        val newDefenderMorale = MoraleSystem.moraleAfterHit(defender.morale, dmgToDefender)
        defender.morale = newDefenderMorale
        log.add("${attacker.name} atakuje ${defender.name}: $dmgToDefender obrażeń.")

        // Kontratak
        val counterAtk = (defender.attackBase * defenderStatus.attackModifier() *
            Random.nextFloat().let { 0.6f + it * 0.8f }).toInt()
        val attackerDef = (attacker.armor * attackerStatus.defenseModifier() *
            Random.nextFloat().let { 0.5f + it * 0.5f }).toInt()
        val dmgToAttacker = maxOf(0, counterAtk - attackerDef)
        attacker.hp -= dmgToAttacker
        attacker.endurance = (attacker.endurance - dmgToAttacker / 2).coerceAtLeast(0)
        val newAttackerMorale = MoraleSystem.moraleAfterHit(attacker.morale, dmgToAttacker)
        attacker.morale = newAttackerMorale
        if (dmgToAttacker > 0) log.add("${defender.name} kontratakuje: $dmgToAttacker obrażeń.")

        // Rany
        val defenderWound = computeWound(defender)
        val attackerWound = computeWound(attacker)
        if (defenderWound != WoundType.NONE) {
            defender.wounds.add(defenderWound)
            log.add("${defender.name} otrzymuje ranę: $defenderWound")
        }
        if (attackerWound != WoundType.NONE) {
            attacker.wounds.add(attackerWound)
            log.add("${attacker.name} otrzymuje ranę: $attackerWound")
        }

        return RoundResult(
            attackerDamage = dmgToDefender,
            defenderDamage = dmgToAttacker,
            attackerMorale = newAttackerMorale,
            defenderMorale = newDefenderMorale,
            attackerWound = attackerWound,
            defenderWound = defenderWound,
            log = log
        )
    }

    private fun computeWound(state: CombatantState): WoundType {
        val hpPercent = if (state.maxHp > 0) state.hp.toFloat() / state.maxHp else 0f
        return when {
            hpPercent <= 0f -> WoundType.CRITICAL
            hpPercent <= 0.2f && state.endurance < 5 -> WoundType.SERIOUS
            hpPercent <= 0.4f && state.endurance < 10 -> WoundType.LIGHT
            else -> WoundType.NONE
        }
    }

    fun isDefeated(state: CombatantState): Boolean =
        state.hp <= 0 || MoraleSystem.computeStatus(state.morale) == MoraleStatus.ROUTED

    fun postCombatRecovery(hero: CombatantState): String {
        val healHp = (hero.maxHp * 0.1f).toInt().coerceAtLeast(1)
        hero.hp = (hero.hp + healHp).coerceAtMost(hero.maxHp)
        hero.endurance = (hero.endurance + 5).coerceAtMost(20)
        hero.morale = MoraleSystem.moraleAfterKill(hero.morale)
        if (hero.wounds.isNotEmpty()) hero.wounds.removeLast()
        return "Leczenie: +$healHp HP. Morale: ${hero.morale}. Rany: ${hero.wounds.size}"
    }
}
