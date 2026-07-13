package com.grimreich.ui.character

data class HeroUi(
    val id: String,
    val name: String,
    val classLabel: String,
    val portraitResId: Int,
    val level: Int,
    val hp: Int,
    val maxHp: Int,
    val status: HeroStatusUi,
    val combatStats: HeroCombatStatsUi,
    val attributePoints: Int,
    val activeEffects: List<HeroEffectUi>,
    val isActiveHero: Boolean
)

data class HeroCombatStatsUi(
    val strength: Int,
    val agility: Int,
    val intelligence: Int,
    val perception: Int,
    val endurance: Int,
    val charisma: Int,
    val piety: Int,
    val attack: Int,
    val armor: Int
)

data class HeroEffectUi(
    val id: String,
    val label: String,
    val isBuff: Boolean
)

enum class HeroStatusUi { ALIVE, WOUNDED, DEAD }

data class InventoryItemUi(
    val instanceId: String,
    val templateId: String,
    val name: String,
    val iconResId: Int,
    val type: String,
    val weight: Double,
    val value: Int,
    val rarity: String,
    val slot: String?,
    val isEquipped: Boolean = false,
    val canEquip: Boolean = true,
    val statPreview: String? = null
)
