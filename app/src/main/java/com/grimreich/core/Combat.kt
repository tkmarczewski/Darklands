package com.grimreich.core

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

// ==================== STATUS EFFECTS ====================

enum class StatusEffectType {
    POISON, BLEED, FIRE, FREEZE
}

data class StatusEffect(
    val type: StatusEffectType,
    var duration: Int, // rounds remaining
    val strength: Int  // dmg or effect intensity
)

// ==================== COMBAT MODELS ====================

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
    var agility: Int = 5,
    var intelligence: Int = 5,
    var strength: Int = 5,
    var activeEffects: MutableList<StatusEffect> = mutableListOf(),
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

        // 1. Tick Status Effects for attacker
        applyStatusTick(attacker, log)
        if (isDefeated(attacker)) return RoundResult(0, 0, attacker.morale, defender.morale, WoundType.NONE, WoundType.NONE, log)

        val attackerStatus = MoraleSystem.computeStatus(attacker.morale)
        val defenderStatus = MoraleSystem.computeStatus(defender.morale)

        // 2. Dodge Roll (Agility based)
        val dodgeChance = 0.05f + (defender.agility * 0.02f)
        val dodged = Random.nextFloat() < dodgeChance

        val dmgToDefender = if (dodged) {
            log.add("${defender.name} unika ataku!")
            0
        } else {
            // Atak
            val rawAtk = attacker.attackBase + (attacker.strength / 2) + attackerEquipped.totalAttack()
            val atkMod = attackerStatus.attackModifier()
            val defMod = defenderStatus.defenseModifier()
            val defArmor = defender.armor + attackerEquipped.totalDefense()

            val attackRoll = (rawAtk * atkMod * Random.nextFloat().let { 0.7f + it * 0.6f }).toInt()
            val defendRoll = (defArmor * defMod * Random.nextFloat().let { 0.5f + it * 0.5f }).toInt()

            val dmg = maxOf(1, attackRoll - defendRoll)
            defender.hp -= dmg
            defender.endurance = (defender.endurance - dmg / 2).coerceAtLeast(0)
            defender.morale = MoraleSystem.moraleAfterHit(defender.morale, dmg)
            log.add("${attacker.name} atakuje ${defender.name}: $dmg obrażeń.")

            // 3. Status application (Knowledge based)
            tryApplyStatus(attacker, defender, log)
            dmg
        }

        // 4. Counterattack
        var dmgToAttacker = 0
        if (!isDefeated(defender)) {
            val counterAtk = (defender.attackBase * defenderStatus.attackModifier() *
                Random.nextFloat().let { 0.6f + it * 0.8f }).toInt()
            val attackerDef = (attacker.armor * attackerStatus.defenseModifier() *
                Random.nextFloat().let { 0.5f + it * 0.5f }).toInt()
            dmgToAttacker = maxOf(0, counterAtk - attackerDef)
            attacker.hp -= dmgToAttacker
            attacker.endurance = (attacker.endurance - dmgToAttacker / 2).coerceAtLeast(0)
            attacker.morale = MoraleSystem.moraleAfterHit(attacker.morale, dmgToAttacker)
            if (dmgToAttacker > 0) log.add("${defender.name} kontratakuje: $dmgToAttacker obrażeń.")
        }

        // 5. Wounds
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
            attackerMorale = attacker.morale,
            defenderMorale = defender.morale,
            attackerWound = attackerWound,
            defenderWound = defenderWound,
            log = log
        )
    }

    private fun applyStatusTick(combatant: CombatantState, log: MutableList<String>) {
        val it = combatant.activeEffects.iterator()
        while (it.hasNext()) {
            val effect = it.next()
            when (effect.type) {
                StatusEffectType.POISON -> {
                    combatant.hp -= effect.strength
                    log.add("${combatant.name} cierpi od trucizny: -${effect.strength} HP.")
                }
                StatusEffectType.BLEED -> {
                    combatant.hp -= effect.strength
                    combatant.endurance = (combatant.endurance - 1).coerceAtLeast(0)
                    log.add("${combatant.name} krwawi: -${effect.strength} HP.")
                }
                StatusEffectType.FIRE -> {
                    combatant.hp -= effect.strength
                    combatant.morale -= 2
                    log.add("${combatant.name} płonie: -${effect.strength} HP.")
                }
                StatusEffectType.FREEZE -> {
                    // Reduce agility/morale
                    combatant.morale -= 1
                    log.add("${combatant.name} jest przemarznięty.")
                }
            }
            effect.duration--
            if (effect.duration <= 0) it.remove()
        }
    }

    private fun tryApplyStatus(attacker: CombatantState, defender: CombatantState, log: MutableList<String>) {
        // Base chance + Knowledge bonus
        val statusChance = 0.1f + (attacker.intelligence * 0.03f)
        if (Random.nextFloat() < statusChance) {
            // Pick a random effect based on something? For now random
            val effectType = StatusEffectType.entries.toTypedArray().random()
            val existing = defender.activeEffects.find { it.type == effectType }
            if (existing != null) {
                existing.duration += 2
            } else {
                defender.activeEffects.add(StatusEffect(effectType, 3, 2 + attacker.intelligence / 4))
            }
            log.add("${defender.name} otrzymuje status: $effectType!")
        }
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
