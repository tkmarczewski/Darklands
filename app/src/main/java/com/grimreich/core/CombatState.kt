package com.grimreich.core

data class CombatState(
    var active: Boolean = false,
    var round: Int = 0,
    var enemyName: String = "",
    var enemyHp: Int = 0,
    var enemyMaxHp: Int = 0,
    var enemyAttack: Int = 5,
    var enemyDefense: Int = 3,
    var log: MutableList<String> = mutableListOf()
)
