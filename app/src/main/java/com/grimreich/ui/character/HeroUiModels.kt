package com.grimreich.ui.character

import androidx.compose.runtime.Immutable

@Immutable
data class CharacterHubUiState(
    val selectedHeroId: String? = null,
    val selectedTab: CharacterHubTab = CharacterHubTab.OVERVIEW,
    val heroes: List<HeroUi> = emptyList(),
    val inventory: List<InventoryItemUi> = emptyList(),
    val isLoading: Boolean = false
) {
    val selectedHero: HeroUi? get() = heroes.find { it.id == selectedHeroId }
}

enum class CharacterHubTab { OVERVIEW, EQUIPMENT, PARTY }

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
    val isActiveHero: Boolean,
    val masteryTraitLabel: String? = null
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
    val isPositive: Boolean
)

enum class HeroStatusUi { 
    alive, wounded, dead;

    companion object {
        @JvmField val ALIVE = alive
        @JvmField val WOUNDED = wounded
        @JvmField val DEAD = dead
    }
}

data class InventoryItemUi(
    val instanceId: String,
    val templateId: String,
    val name: String,
    val iconResId: Int,
    val type: String,
    val weight: Float,
    val value: Int,
    val rarity: String,
    val slot: String? = null,
    val isEquipped: Boolean = false,
    val canEquip: Boolean = true
)
