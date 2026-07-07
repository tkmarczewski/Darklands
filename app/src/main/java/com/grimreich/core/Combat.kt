package com.grimreich.core

import com.grimreich.systems.SkillCatalogue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// ==================== MORALE SYSTEM ====================
enum class MoraleStatus {
    HEROIC, STEADY, SHAKEN, PANICKED, ROUTED;

    fun attackModifier(): Float = when (this) {
        HEROIC   -> 1.2f
        STEADY   -> 1.0f
        SHAKEN   -> 0.8f
        PANICKED -> 0.5f
        ROUTED   -> 0.0f
    }

    fun defenseModifier(): Float = when (this) {
        HEROIC   -> 1.1f
        STEADY   -> 1.0f
        SHAKEN   -> 0.85f
        PANICKED -> 0.6f
        ROUTED   -> 0.0f
    }
}

@Singleton
class MoraleSystem @Inject constructor() {
    fun computeStatus(morale: Int): MoraleStatus = when {
        morale >= GrimConstants.Combat.MORALE_HEROIC_THRESHOLD   -> MoraleStatus.HEROIC
        morale >= GrimConstants.Combat.MORALE_STEADY_THRESHOLD   -> MoraleStatus.STEADY
        morale >= GrimConstants.Combat.MORALE_SHAKEN_THRESHOLD   -> MoraleStatus.SHAKEN
        morale >= GrimConstants.Combat.MORALE_PANICKED_THRESHOLD -> MoraleStatus.PANICKED
        else                                                      -> MoraleStatus.ROUTED
    }

    fun moraleAfterHit(morale: Int, dmgTaken: Int): Int =
        (morale - (dmgTaken / 2)).coerceAtLeast(0)

    fun moraleAfterKill(morale: Int): Int =
        (morale + GrimConstants.Combat.KILL_MORALE_BONUS).coerceAtMost(GrimConstants.Combat.MAX_MORALE)

    fun moraleAfterFlee(morale: Int): Int =
        (morale - GrimConstants.Combat.FLEE_MORALE_PENALTY).coerceAtLeast(0)
}

// ==================== STATUS EFFECTS ====================
enum class StatusEffectType { POISON, BLEED, FIRE, FREEZE, WET, SHOCK }

data class StatusEffect(
    val type: StatusEffectType,
    var duration: Int,
    val strength: Int
)

// ==================== COMBAT MODELS ====================
enum class SkillType { MELEE, RANGED, PRAYER, ALCHEMY }

data class SkillResult(
    val damage: Int = 0,
    val statusApplied: Boolean = false,
    val message: String = ""
)

data class CombatSkill(
    val id: String,
    val name: String,
    val type: SkillType,
    val staminaCost: Int = 0,
    val favorCost: Int = 0,
    val description: String = "",
    val effect: (CombatantState, CombatantState) -> SkillResult
)

enum class WoundType { NONE, LIGHT, SERIOUS, CRITICAL }

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
    var perception: Int = 5,
    var charisma: Int = 5,
    var piety: Int = 5,
    var activeEffects: MutableList<StatusEffect> = mutableListOf(),
    var wounds: MutableList<WoundType> = mutableListOf()
) {
    fun normalize() {
        hp = hp.coerceIn(0, maxHp)
        endurance = endurance.coerceAtLeast(0)
        morale = morale.coerceIn(0, GrimConstants.Combat.MAX_MORALE)
    }
}

data class RoundResult(
    val attackerDamage: Int,
    val defenderDamage: Int,
    val attackerMorale: Int,
    val defenderMorale: Int,
    val attackerWound: WoundType,
    val defenderWound: WoundType,
    val log: List<String>,
    val visualEvents: List<CombatVisualEvent> = emptyList()
)

data class CombatVisualEvent(
    val target: String,
    val type: String,
    val value: Int = 0,
    val label: String = ""
)

interface CombatRandomProvider {
    fun nextFloat(): Float
    fun nextInt(until: Int): Int
    fun nextInt(from: Int, until: Int): Int
}

@Singleton
class DefaultCombatRandomProvider @Inject constructor() : CombatRandomProvider {
    override fun nextFloat(): Float = Random.nextFloat()
    override fun nextInt(until: Int): Int = Random.nextInt(until)
    override fun nextInt(from: Int, until: Int): Int = Random.nextInt(from, until)
}

@Singleton
class CombatRound @Inject constructor(
    private val moraleSystem: MoraleSystem,
    private val randomProvider: CombatRandomProvider
) {

    fun resolveRound(
        attacker: CombatantState,
        defender: CombatantState,
        skillId: String? = null
    ): RoundResult {
        val log = mutableListOf<String>()

        applyStatusTick(attacker, log)
        applyStatusTick(defender, log)
        
        if (isDefeated(attacker)) {
            return RoundResult(0, 0, attacker.morale, defender.morale,
                WoundType.NONE, WoundType.NONE, log)
        }

        val skill = SkillCatalogue.allSkills.find { it.id == skillId }
        val dmgToDefender: Int
        if (skill != null && attacker.endurance >= skill.staminaCost) {
            attacker.endurance -= skill.staminaCost
            log.add("${attacker.name} używa ${skill.name}!")
            val hpBefore = defender.hp
            val result = skill.effect(attacker, defender)
            if (result.message.isNotBlank()) log.add(result.message)
            dmgToDefender = maxOf(result.damage, (hpBefore - defender.hp).coerceAtLeast(0))
            if (result.statusApplied && dmgToDefender == 0) {
                log.add("Efekt specjalny został zastosowany.")
            }
        } else {
            if (skill != null) log.add("${attacker.name} jest zbyt zmęczony na ${skill.name}!")
            dmgToDefender = resolveAttack(attacker, defender, log)
        }

        val dmgToAttacker = if (!isDefeated(defender)) {
            resolveCounterAttack(attacker, defender, log)
        } else {
            0
        }

        val defenderWound = applyWound(defender, log)
        val attackerWound  = applyWound(attacker, log)

        attacker.normalize()
        defender.normalize()
        
        if (log.size > 50) {
            val lastEntries = log.takeLast(50)
            log.clear()
            log.addAll(lastEntries)
        }

        return RoundResult(
            attackerDamage = dmgToDefender,
            defenderDamage = dmgToAttacker,
            attackerMorale  = attacker.morale,
            defenderMorale  = defender.morale,
            attackerWound   = attackerWound,
            defenderWound   = defenderWound,
            log = log
        )
    }

    private fun resolveAttack(
        attacker: CombatantState,
        defender: CombatantState,
        log: MutableList<String>
    ): Int {
        if (attacker.maxHp <= 0 || defender.maxHp <= 0) return 0

        val dodgeChance = (GrimConstants.Combat.BASE_DODGE_CHANCE +
            ((defender.agility - 10) * GrimConstants.Combat.AGILITY_DODGE_MODIFIER))
            .coerceIn(0.05f, 0.8f)
        val dodged = randomProvider.nextFloat() < dodgeChance
        if (dodged) {
            log.add("${defender.name} unika ataku!")
            return 0
        }

        var rawAtk = attacker.attackBase + (attacker.strength / 2)
        if (defender.activeEffects.any { it.type == StatusEffectType.WET } &&
            attacker.activeEffects.any { it.type == StatusEffectType.SHOCK }) {
            rawAtk = (rawAtk * 1.5f).toInt()
            log.add("Przewodnictwo! Mokry wróg otrzymuje zwiększone obrażenia od porażenia.")
        }

        val attackerStatus = moraleSystem.computeStatus(attacker.morale)
        val defenderStatus = moraleSystem.computeStatus(defender.morale)

        // FIX (M-01): Routed combatants deal NO damage
        if (attackerStatus == MoraleStatus.ROUTED) {
            log.add("${attacker.name} jest zbyt przerażony, by walczyć!")
            return 0
        }

        val defArmor = defender.armor

        val critChance = (attacker.perception * GrimConstants.Combat.PERCEPTION_CRIT_MODIFIER)
            .coerceIn(0f, 0.8f) // FIX (M-02): Clamp crit chance
        val isCrit   = randomProvider.nextFloat() < critChance
        val critMod  = if (isCrit) GrimConstants.Combat.CRITICAL_HIT_MULTIPLIER else 1.0f

        val attackRoll = (rawAtk * attackerStatus.attackModifier() *
            (0.7f + randomProvider.nextFloat() * 0.6f) * critMod).toInt()
        val defendRoll = (defArmor * defenderStatus.defenseModifier() *
            (0.5f + randomProvider.nextFloat() * 0.5f)).toInt()
        val dmg = maxOf(1, attackRoll - defendRoll)

        if (isCrit) log.add("KRYTYK! ${attacker.name} zadaje potężny cios.")
        defender.hp        = (defender.hp - dmg).coerceAtLeast(0)
        defender.endurance = (defender.endurance - dmg / 2).coerceAtLeast(0)
        defender.morale    = moraleSystem.moraleAfterHit(defender.morale, dmg)
        log.add("${attacker.name} atakuje ${defender.name}: $dmg obrażeń.")

        if (attacker.charisma >= 10) {
            val regen = ((attacker.charisma - 10).coerceAtLeast(0) / 2 + 1) *
                GrimConstants.Combat.CHARISMA_MORALE_REGEN
            attacker.morale = (attacker.morale + regen)
                .coerceAtMost(GrimConstants.Combat.MAX_MORALE)
            if (regen > 0) log.add("${attacker.name} zagrzewa siebie do walki. (+${regen} Morale)")
        }

        tryApplyStatus(attacker, defender, log)
        return dmg
    }

    private fun resolveCounterAttack(
        attacker: CombatantState,
        defender: CombatantState,
        log: MutableList<String>
    ): Int {
        val attackerStatus = moraleSystem.computeStatus(attacker.morale)
        val defenderStatus = moraleSystem.computeStatus(defender.morale)
        
        // FIX (M-01): Routed defenders don't counter
        if (defenderStatus == MoraleStatus.ROUTED) return 0

        val counterAtk  = (defender.attackBase * defenderStatus.attackModifier() *
            (0.6f + randomProvider.nextFloat() * 0.8f)).toInt()
        val attackerDef = (attacker.armor * attackerStatus.defenseModifier() *
            (0.5f + randomProvider.nextFloat() * 0.5f)).toInt()
        val dmg = maxOf(0, counterAtk - attackerDef)
        attacker.hp        = (attacker.hp - dmg).coerceAtLeast(0)
        attacker.endurance = (attacker.endurance - dmg / 2).coerceAtLeast(0)
        attacker.morale    = moraleSystem.moraleAfterHit(attacker.morale, dmg)
        if (dmg > 0) log.add("${defender.name} kontratakuje: $dmg obrażeń.")
        return dmg
    }

    private fun applyWound(combatant: CombatantState, log: MutableList<String>): WoundType {
        val wound = computeWound(combatant)
        if (wound != WoundType.NONE && !combatant.wounds.contains(wound)) {
            combatant.wounds.add(wound)
            log.add("${combatant.name} otrzymuje ranę: $wound")
        }
        return wound
    }

    private fun applyStatusTick(combatant: CombatantState, log: MutableList<String>) {
        val it = combatant.activeEffects.iterator()
        while (it.hasNext()) {
            val effect = it.next()
            when (effect.type) {
                StatusEffectType.POISON -> {
                    combatant.hp = (combatant.hp - effect.strength).coerceAtLeast(0)
                    log.add("${combatant.name} cierpi od trucizny: -${effect.strength} HP.")
                }
                StatusEffectType.BLEED -> {
                    combatant.hp        = (combatant.hp - effect.strength).coerceAtLeast(0)
                    combatant.endurance = (combatant.endurance - 1).coerceAtLeast(0)
                    log.add("${combatant.name} krwawi: -${effect.strength} HP.")
                }
                StatusEffectType.FIRE -> {
                    combatant.hp    = (combatant.hp - effect.strength).coerceAtLeast(0)
                    combatant.morale = (combatant.morale - 2).coerceAtLeast(0)
                    log.add("${combatant.name} płonie: -${effect.strength} HP.")
                }
                StatusEffectType.FREEZE -> {
                    combatant.morale = (combatant.morale - 1).coerceAtLeast(0)
                    log.add("${combatant.name} jest przemarznięty.")
                }
                StatusEffectType.WET -> {
                    log.add("${combatant.name} jest przemoczony.")
                    val shockActive = combatant.activeEffects.any {
                        it !== effect && it.type == StatusEffectType.SHOCK
                    }
                    if (shockActive) {
                        val shockDmg = effect.strength * 2
                        combatant.hp = (combatant.hp - shockDmg).coerceAtLeast(0)
                        log.add("Mokre ciało przewodzi prąd! ${combatant.name}: -${shockDmg} HP.")
                    }
                }
                StatusEffectType.SHOCK -> {
                    combatant.endurance = (combatant.endurance - 2).coerceAtLeast(0)
                    log.add("${combatant.name} drży od wyładowań.")
                }
            }
            effect.duration--
            if (effect.duration <= 0) it.remove()
        }
    }

    private fun tryApplyStatus(
        attacker: CombatantState,
        defender: CombatantState,
        log: MutableList<String>
    ) {
        val statusChance = (GrimConstants.Combat.STATUS_CHANCE_BASE +
            ((attacker.intelligence - 10) * GrimConstants.Combat.STATUS_CHANCE_INT_MOD))
            .coerceIn(0.0f, 0.8f)
        if (randomProvider.nextFloat() < statusChance) {
            val effectType = StatusEffectType.entries[randomProvider.nextInt(StatusEffectType.entries.size)]
            val existing   = defender.activeEffects.find { it.type == effectType }
            if (existing != null) {
                existing.duration = (existing.duration + 2).coerceAtMost(10)
            } else {
                defender.activeEffects.add(
                    StatusEffect(effectType, 3, 2 + attacker.intelligence / 4)
                )
            }
            log.add("${defender.name} otrzymuje status: $effectType!")
        }
    }

    private fun computeWound(state: CombatantState): WoundType {
        val hpPercent = if (state.maxHp > 0) state.hp.toFloat() / state.maxHp else 0f
        return when {
            hpPercent <= 0f                                                          -> WoundType.CRITICAL
            hpPercent <= GrimConstants.Combat.WOUND_THRESHOLD_SERIOUS && state.endurance < 5  -> WoundType.SERIOUS
            hpPercent <= GrimConstants.Combat.WOUND_THRESHOLD_LIGHT   && state.endurance < 10 -> WoundType.LIGHT
            else                                                                     -> WoundType.NONE
        }
    }

    fun isDefeated(state: CombatantState): Boolean =
        state.hp <= 0 || moraleSystem.computeStatus(state.morale) == MoraleStatus.ROUTED

    fun postCombatRecovery(hero: CombatantState): String {
        val healHp = (hero.maxHp * GrimConstants.Combat.HP_RECOVERY_RATIO)
            .toInt().coerceAtLeast(1)
        hero.hp = (hero.hp + healHp).coerceAtMost(hero.maxHp)
        val enduranceHeal = randomProvider.nextInt(
            GrimConstants.Combat.POST_COMBAT_HEAL_HP_MIN,
            GrimConstants.Combat.POST_COMBAT_HEAL_HP_MAX + 1
        )
        hero.endurance = (hero.endurance + enduranceHeal).coerceAtMost(99)
        hero.morale    = moraleSystem.moraleAfterKill(hero.morale)
        if (hero.wounds.isNotEmpty()) hero.wounds.removeAt(hero.wounds.lastIndex)
        hero.normalize()
        return "Leczenie: +$healHp HP, +$enduranceHeal Endurance. Morale: ${hero.morale}. Rany: ${hero.wounds.size}"
    }
}
