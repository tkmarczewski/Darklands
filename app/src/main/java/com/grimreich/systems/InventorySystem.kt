package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventorySystem @Inject constructor(
    private val gameRepository: GameRepository
) {

    fun equip(heroId: String, instanceId: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: run { result = "Brak bohatera"; return@updateState }
            val item = state.inventory.find { it.instanceId == instanceId } ?: run { result = "Brak przedmiotu"; return@updateState }
            val slot = item.slot ?: run { result = "Brak slotu"; return@updateState }
            
            val minStr = item.effects["minStrength"] ?: 0
            if (minStr > 0 && hero.strength < minStr) {
                result = "Za słaby (wym. $minStr SIŁ)"
                return@updateState
            }

            hero.equipment[slot] = instanceId
            state.logEntries.add("${hero.name} zakłada ${item.name}.")
            result = "Założono"
        }
        return result
    }

    fun unequip(heroId: String, slot: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            val instanceId = hero.equipment[slot] ?: return@updateState
            val item = state.inventory.find { it.instanceId == instanceId }
            hero.equipment[slot] = null
            result = "Zdjęto ${item?.name ?: "przedmiot"}"
            state.logEntries.add("${hero.name} zdejmuje przedmiot.")
        }
        return result
    }

    fun listInventory(): String {
        val items = gameRepository.currentState().inventory
        if (items.isEmpty()) return "Ekwipunek jest pusty"
        
        val maxDisplay = 50
        val displayItems = items.take(maxDisplay)
        val list = displayItems.joinToString("\n") { item ->
            val rarityLabel = if (item.rarity != "normal") " [${item.rarity.uppercase()}]" else ""
            val extra = when (item.type) {
                "weapon" -> " (ATK:${item.effects["attack"] ?: 0})"
                "armor" -> " (DEF:${item.effects["armor"] ?: 0})"
                "potion" -> " (HEAL:${item.effects["heal"] ?: 0})"
                else -> " (${item.type})"
            }
            "- ${item.name}$rarityLabel$extra | ${item.weight}kg"
        }
        
        return if (items.size > maxDisplay) {
            "$list\n... i ${items.size - maxDisplay} więcej przedmiotów."
        } else {
            list
        }
    }

    fun totalWeight(heroId: String): Float {
        val state = gameRepository.currentState()
        val hero  = state.party.firstOrNull { it.id == heroId } ?: return 0f
        return hero.equipment.values
            .filterNotNull()
            .mapNotNull { instId -> state.inventory.firstOrNull { it.instanceId == instId } }
            .sumOf { it.weight }
            .toFloat()
    }

    fun transferItem(fromHeroId: String, toHeroId: String, instanceId: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val from = state.party.find { it.id == fromHeroId } ?: run { result = "Brak nadawcy"; return@updateState }
            val to = state.party.find { it.id == toHeroId } ?: run { result = "Brak odbiorcy"; return@updateState }
            
            val equippedSlot = from.equipment.entries.firstOrNull { it.value == instanceId }?.key
            if (equippedSlot != null) {
                from.equipment[equippedSlot] = null
            }
            
            state.logEntries.add("Przekazano przedmiot od ${from.name} do ${to.name}.")
            result = "Przekazano"
        }
        return result
    }

    fun itemDetail(instanceId: String): String {
        val item = gameRepository.currentState().inventory.firstOrNull { it.instanceId == instanceId }
            ?: return "Nie znaleziono: $instanceId"
        val effects = item.effects.entries.joinToString(", ") { (k, v) -> "$k=$v" }
        return buildString {
            appendLine(item.name)
            appendLine("typ: ${item.type}")
            appendLine("waga: ${item.weight}")
            if (effects.isNotEmpty()) appendLine("efekty: $effects")
        }.trim()
    }

    fun useItem(instanceId: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val item = state.inventory.find { it.instanceId == instanceId } ?: run { result = "Brak"; return@updateState }
            val activeHeroId = state.activeHeroId ?: return@updateState
            val targetHero = state.party.find { it.id == activeHeroId } ?: return@updateState

            val heal = item.effects["heal"] ?: 0
            val sanity = item.effects["sanity"] ?: 0
            
            if (heal > 0) targetHero.hp = (targetHero.hp + heal).coerceAtMost(targetHero.maxHp)
            if (sanity > 0) targetHero.sanity = (targetHero.sanity + sanity).coerceAtMost(100)

            state.inventory.remove(item)
            result = "Użyto ${item.name}"
            state.logEntries.add("${targetHero.name} używa ${item.name}.")
        }
        return result
    }

    fun getEquippedItems(hero: Hero): EquippedItems {
        val state = gameRepository.currentState()
        val gear = EquippedItems()
        hero.equipment["weapon"]?.let { instId -> gear.weapon = state.inventory.find { it.instanceId == instId } }
        hero.equipment["armor"]?.let { instId -> gear.bodyArmor = state.inventory.find { it.instanceId == instId } }
        hero.equipment["helmet"]?.let { instId -> gear.helmet = state.inventory.find { it.instanceId == instId } }
        hero.equipment["shield"]?.let { instId -> gear.shield = state.inventory.find { it.instanceId == instId } }
        hero.equipment["accessory"]?.let { instId -> gear.accessory = state.inventory.find { it.instanceId == instId } }
        return gear
    }
}
