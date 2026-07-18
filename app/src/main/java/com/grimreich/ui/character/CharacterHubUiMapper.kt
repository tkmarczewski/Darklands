package com.grimreich.ui.character

import android.content.Context
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CharacterHubUiMapper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun map(state: GameState): CharacterHubUiState {
        return CharacterHubUiState(
            heroes = state.party.map { mapHero(it, state, it.id == state.activeHeroId) },
            inventory = state.inventory.map { mapItem(it, state) },
            selectedHeroId = state.activeHeroId,
            isLoading = false
        )
    }

    private fun mapHero(hero: Hero, state: GameState, isActive: Boolean): HeroUi {
        val status = when {
            hero.isDead -> HeroStatusUi.dead
            hero.hp < hero.maxHp / 2 -> HeroStatusUi.wounded
            else -> HeroStatusUi.alive
        }

        val combatStats = HeroCombatStatsUi(
            strength = hero.effectiveStrength(),
            agility = hero.effectiveAgility(),
            intelligence = hero.effectiveIntelligence(),
            perception = hero.effectivePerception(),
            endurance = hero.effectiveEndurance(),
            charisma = hero.effectiveCharisma(),
            piety = hero.effectivePiety(),
            attack = hero.effectiveAttack(state.inventory),
            armor = hero.effectiveArmor(state.inventory)
        )

        return HeroUi(
            id = hero.id,
            name = hero.name,
            classLabel = hero.currentCareer?.displayName ?: "Wędrowiec",
            portraitResId = getResId(hero.portraitRes, "drawable"),
            level = hero.level,
            hp = hero.hp,
            maxHp = hero.maxHp,
            status = status,
            combatStats = combatStats,
            attributePoints = hero.attributePoints,
            activeEffects = hero.activeMutations.map { HeroEffectUi(it.id, it.name, true) },
            isActiveHero = isActive,
            masteryTraitLabel = hero.masteryTrait?.let { key ->
                val resId = context.resources.getIdentifier(key, "string", context.packageName)
                if (resId != 0) context.getString(resId) else key
            }
        )
    }

    private fun mapItem(item: Item, state: GameState): InventoryItemUi {
        val isEquipped = state.party.any { it.equipment.values.contains(item.instanceId) }
        
        return InventoryItemUi(
            instanceId = item.instanceId,
            templateId = item.templateId,
            name = item.name,
            iconResId = getResId("ic_item_${item.type}", "drawable"),
            type = item.type,
            weight = item.weight.toFloat(),
            value = item.value,
            rarity = item.rarity,
            slot = item.slot,
            isEquipped = isEquipped
        )
    }

    private fun getResId(name: String, defType: String): Int {
        val id = context.resources.getIdentifier(name, defType, context.packageName)
        return if (id == 0) {
            // Fallback for portraits
            if (defType == "drawable") context.resources.getIdentifier("port_peasant", "drawable", context.packageName)
            else 0
        } else id
    }
}
