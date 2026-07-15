package com.grimreich.content.enemy

data class EnemyContentStats(
    val hp: Int,
    val strength: Int,
    val agility: Int,
    val intellect: Int,
    val constitution: Int,
    val armor: Int
) {
    init {
        require(hp > 0) { "HP must be positive" }
        require(armor >= 0) { "Armor cannot be negative" }
    }
}

enum class EnemyContentCategory {
    HUMANOID, MONSTER, UNDEAD, ANIMAL, DEMON
}

data class EnemyContentType(
    val id: String,
    val name: String,
    val type: EnemyContentCategory,
    val baseStats: EnemyContentStats,
    val specialTraits: List<String> = emptyList()
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
    }
}
