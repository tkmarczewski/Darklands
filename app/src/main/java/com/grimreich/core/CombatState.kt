package com.grimreich.core

import kotlinx.serialization.Serializable

@Serializable
data class InitiativeSlot(
    val id: String,
    val isPlayer: Boolean,
    val initiativeValue: Int
)

@Serializable
data class CombatState(
    var active: Boolean = false,
    var round: Int = 0,
    var enemyName: String = "",
    var enemyHp: Int = 0,
    var enemyMaxHp: Int = 0,
    var enemyAttack: Int = 5,
    var enemyDefense: Int = 3,
    var enemyAgility: Int = 5,
    var enemyIntelligence: Int = 5,
    var enemyStrength: Int = 5,
    var enemyStamina: Int = 10,
    var heroStamina: Int = 10,
    var enemyType: String? = null,
    var heroArmorBonus: Int = 0,
    var heroHp: Int = 0,
    var currentTargetHeroId: String? = null,
    var activeHeroId: String? = null,
    val enemyEffects: MutableList<StatusEffect> = mutableListOf(),
    val heroEffects: MutableList<StatusEffect> = mutableListOf(),
    val log: MutableList<String> = mutableListOf(),
    val initiativeOrder: MutableList<InitiativeSlot> = mutableListOf(),
    var currentTurnIndex: Int = 0
)

