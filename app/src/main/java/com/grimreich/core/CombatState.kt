package com.grimreich.core

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
    var enemyEffects: MutableList<StatusEffect> = mutableListOf(),
    var heroEffects: MutableList<StatusEffect> = mutableListOf(),
    var log: MutableList<String> = mutableListOf()
)
