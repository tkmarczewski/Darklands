package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.PartyRepository

object InventorySystem {
    fun equip(itemId: String): String {
        val item = GameRepository.state.inventory.firstOrNull { it.id == itemId } ?: return "Nie znaleziono: $itemId"
        val hero = PartyRepository.activeHero() ?: return "Brak bohatera"
        val slot = item.slot ?: return "${item.name} nie ma slotu"
        hero.equipment[slot] = itemId
        GameRepository.log("${hero.name} zalozyl ${item.name}")
        return "${hero.name} zalozyl ${item.name} (slot: $slot)"
    }
    fun unequip(slot: String): String {
        val hero = PartyRepository.activeHero() ?: return "Brak bohatera"
        val itemId = hero.equipment[slot] ?: return "Slot $slot jest pusty"
        val item = GameRepository.state.inventory.firstOrNull { it.id == itemId }
        hero.equipment[slot] = null
        return "${hero.name} zdial ${item?.name ?: itemId}"
    }
    fun listInventory(): String {
        val items = GameRepository.state.inventory
        if (items.isEmpty()) return "Ekwipunek jest pusty"
        return items.joinToString("\n") { "- ${it.name} (${it.type})" }
    }
}
