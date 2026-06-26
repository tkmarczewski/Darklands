package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventorySystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val partyRepository: PartyRepository
) {

    fun equip(heroId: String, itemId: String): String {
        val state = gameRepository.currentState()
        val hero   = state.party.firstOrNull { it.id == heroId } ?: return "Brak bohatera: $heroId"
        
        // CRITICAL FIX: Verify item exists in inventory before equipping
        val item = state.inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono przedmiotu $itemId w plecaku"

        val slot = item.slot ?: return "${item.name} nie ma przypisanego slotu"
        
        val minStr = item.effects["minStrength"] ?: 0
        if (minStr > 0 && hero.strength < minStr) {
            return "${hero.name} za słaby (siła ${hero.strength}, wymaga $minStr)"
        }

        hero.equipment[slot] = itemId
        gameRepository.log("${hero.name} założył ${item.name} [$slot]")
        gameRepository.persistCurrentState()
        return "${hero.name} założył ${item.name} (slot: $slot)"
    }

    fun unequip(heroId: String, slot: String): String {
        val state = gameRepository.currentState()
        val hero = state.party.firstOrNull { it.id == heroId } ?: return "Brak bohatera: $heroId"
        val itemId = hero.equipment[slot] ?: return "Slot $slot jest pusty"
        val item = state.inventory.firstOrNull { it.id == itemId }
        hero.equipment[slot] = null

        gameRepository.persistCurrentState()
        return "${hero.name} zdjął ${item?.name ?: itemId}"
    }

    fun listInventory(): String {
        val items = gameRepository.currentState().inventory
        if (items.isEmpty()) return "Ekwipunek jest pusty"
        return items.joinToString("\n") { item ->
            val rarityLabel = if (item.rarity != "normal") " [${item.rarity.uppercase()}]" else ""
            val extra = when (item.type) {
                "weapon" -> " (ATK:${item.effects["attack"] ?: 0})"
                "armor" -> " (DEF:${item.effects["defense"] ?: 0})"
                "potion" -> " (HEAL:${item.effects["heal"] ?: 0})"
                else -> " (${item.type})"
            }
            "- ${item.name}$rarityLabel$extra | ${item.weight}kg"
        }
    }

    fun totalWeight(heroId: String): Float {
        val state = gameRepository.currentState()
        val hero  = state.party.firstOrNull { it.id == heroId } ?: return 0f
        return hero.equipment.values
            .filterNotNull()
            .mapNotNull { id -> state.inventory.firstOrNull { it.id == id } }
            .sumOf { it.weight }
            .toFloat()
    }

    fun transferItem(fromHeroId: String, toHeroId: String, itemId: String): String {
        val state = gameRepository.currentState()
        val party = state.party
        val from = party.firstOrNull { it.id == fromHeroId } ?: return "Brak bohatera: $fromHeroId"
        val to   = party.firstOrNull { it.id == toHeroId } ?: return "Brak bohatera: $toHeroId"
        val item = state.inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono: $itemId"

        val equippedSlot = from.equipment.entries.firstOrNull { it.value == itemId }?.key
        if (equippedSlot != null) {
            from.equipment[equippedSlot] = null
        }

        gameRepository.log("Transfer ${item.name}: ${from.name} -> ${to.name}")
        gameRepository.persistCurrentState()
        return "Transfer ${item.name}: ${from.name} -> ${to.name}"
    }

    fun itemDetail(itemId: String): String {
        val item = gameRepository.currentState().inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono: $itemId"
        val effects = item.effects.entries.joinToString(", ") { (k, v) -> "$k=$v" }
        return buildString {
            appendLine(item.name)
            appendLine("typ: ${item.type}")
            appendLine("waga: ${item.weight}")
            if (effects.isNotEmpty()) appendLine("efekty: $effects")
        }.trim()
    }

    fun useItem(itemId: String): String {
        val state = gameRepository.currentState()
        val item = state.inventory.firstOrNull { it.id == itemId } ?: return "Nie znaleziono: $itemId"
        val hero = partyRepository.activeHero() ?: return "Brak aktywnego bohatera."
        
        val targetHero = state.party.firstOrNull { it.id == hero.id } ?: return "Brak bohatera w stanie sesji."

        val heal = item.effects["heal"] ?: 0
        if (heal > 0) {
            targetHero.hp = (targetHero.hp + heal).coerceAtMost(targetHero.maxHp)
        }

        state.inventory.remove(item)
        gameRepository.persistCurrentState()
        return "${targetHero.name} użył ${item.name}. +$heal HP"
    }

    fun getEquippedItems(hero: Hero): EquippedItems {
        val state = gameRepository.currentState()
        val gear = EquippedItems()
        hero.equipment["weapon"]?.let { id -> gear.weapon = state.inventory.firstOrNull { it.id == id } }
        hero.equipment["armor"]?.let { id -> gear.bodyArmor = state.inventory.firstOrNull { it.id == id } }
        hero.equipment["helmet"]?.let { id -> gear.helmet = state.inventory.firstOrNull { it.id == id } }
        hero.equipment["shield"]?.let { id -> gear.shield = state.inventory.firstOrNull { it.id == id } }
        return gear
    }
}
