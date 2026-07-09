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
    var enemyType: String? = null,
    var heroArmorBonus: Int = 0,
    var heroHp: Int = 0,
    var currentTargetHeroId: String? = null, // NOWE: Cel ataku wroga
    var activeHeroId: String? = null,        // NOWE: Kto teraz wykonuje akcję gracza
    val enemyEffects: MutableList<StatusEffect> = mutableListOf(),
    val heroEffects: MutableList<StatusEffect> = mutableListOf(),
    val log: MutableList<String> = mutableListOf()
)
