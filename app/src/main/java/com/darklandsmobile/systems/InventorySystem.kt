package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.PartyRepository
import com.darklandsmobile.core.Equipment.EquipmentSlot
import com.darklandsmobile.core.Equipment.Weapon
import com.darklandsmobile.core.Equipment.Armor

object InventorySystem {

    fun equip(heroId: String, itemId: String): String {
        val state = GameRepository.state
        val hero  = PartyRepository.getById(heroId) ?: return "Brak bohatera: $heroId"
        val item  = state.inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono: $itemId"

        val slot: EquipmentSlot = when (item) {
            is Weapon -> EquipmentSlot.WEAPON
            is Armor  -> EquipmentSlot.ARMOR
            else      -> return "${item.name} nie ma slotu"
        }

        if (item is Weapon && hero.strength < item.minStrength) {
            return "${hero.name} za slaby (sila ${hero.strength}, wymaga ${item.minStrength})"
        }

        hero.equipped[slot] = itemId
        GameRepository.log("${hero.name} zalozyl ${item.name} [${slot.name}]")
        return "${hero.name} zalozyl ${item.name} (slot: ${slot.name})"
    }

    fun unequip(heroId: String, slot: EquipmentSlot): String {
        val hero   = PartyRepository.getById(heroId) ?: return "Brak bohatera: $heroId"
        val itemId = hero.equipped[slot] ?: return "Slot $slot jest pusty"
        val item   = GameRepository.state.inventory.firstOrNull { it.id == itemId }
        hero.equipped[slot] = null
        return "${hero.name} zdjal ${item?.name ?: itemId}"
    }

    fun listInventory(): String {
        val items = GameRepository.state.inventory
        if (items.isEmpty()) return "Ekwipunek jest pusty"
        return items.joinToString("\n") { item ->
            val extra = when (item) {
                is Weapon -> " ATK:${item.attack} waga:${item.weight}"
                is Armor  -> " DEF:${item.defense} waga:${item.weight}"
                else      -> " (${item.type})"
            }
            "- ${item.name}$extra"
        }
    }

    fun totalWeight(heroId: String): Float {
        val hero  = PartyRepository.getById(heroId) ?: return 0f
        val state = GameRepository.state
        return hero.equipped.values
            .filterNotNull()
            .mapNotNull { id -> state.inventory.firstOrNull { it.id == id } }
            .sumOf { it.weight.toDouble() }
            .toFloat()
    }
}
