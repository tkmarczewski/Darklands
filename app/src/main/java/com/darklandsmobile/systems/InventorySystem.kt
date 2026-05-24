package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.PartyRepository

// Inventory operuje na top-levelowym `Item` (Item.kt), gdzie typ broni/zbroi jest stringiem,
// a statystyki (attack/defense) trzymane sa w mapie `effects`. Slot trzymany jako string ("weapon", "armor", ...).
object InventorySystem {

    fun equip(heroId: String, itemId: String): String {
        val state = GameRepository.state
        val hero  = state.party.firstOrNull { it.id == heroId } ?: return "Brak bohatera: $heroId"
        val item  = state.inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono: $itemId"

        val slot = item.slot ?: return "${item.name} nie ma slotu"

        // Niektore bronie wymagaja minimalnej sily - efekt "minStrength" pelni te funkcje.
        val minStr = item.effects["minStrength"] ?: 0
        if (minStr > 0 && hero.strength < minStr) {
            return "${hero.name} za slaby (sila ${hero.strength}, wymaga $minStr)"
        }

        hero.equipment[slot] = itemId
        GameRepository.log("${hero.name} zalozyl ${item.name} [$slot]")
        return "${hero.name} zalozyl ${item.name} (slot: $slot)"
    }

    fun unequip(heroId: String, slot: String): String {
        val hero   = GameRepository.state.party.firstOrNull { it.id == heroId } ?: return "Brak bohatera: $heroId"
        val itemId = hero.equipment[slot] ?: return "Slot $slot jest pusty"
        val item   = GameRepository.state.inventory.firstOrNull { it.id == itemId }
        hero.equipment[slot] = null
        return "${hero.name} zdjal ${item?.name ?: itemId}"
    }

    fun listInventory(): String {
        val items = GameRepository.state.inventory
        if (items.isEmpty()) return "Ekwipunek jest pusty"
        return items.joinToString("\n") { item ->
            val extra = when (item.type) {
                "weapon" -> " ATK:${item.effects["attack"] ?: 0} waga:${item.weight}"
                "armor"  -> " DEF:${item.effects["defense"] ?: 0} waga:${item.weight}"
                else     -> " (${item.type})"
            }
            "- ${item.name}$extra"
        }
    }

    fun totalWeight(heroId: String): Float {
        val hero  = GameRepository.state.party.firstOrNull { it.id == heroId } ?: return 0f
        val state = GameRepository.state
        return hero.equipment.values
            .filterNotNull()
            .mapNotNull { id -> state.inventory.firstOrNull { it.id == id } }
            .sumOf { it.weight }
            .toFloat()
    }

    // Sprint 12: logiczny transfer przedmiotu miedzy postaciami w obrebie wspolnego inventory druzyny.
    // Aktualny model trzyma inventory na poziomie GameRepository.state, wiec transfer to zapis w logu
    // potwierdzajacy przekazanie - bez fizycznej zmiany lokalizacji itemu w pamieci stanu.
    fun transferItem(fromHeroId: String, toHeroId: String, itemId: String): String {
        val party = GameRepository.state.party
        val from = party.firstOrNull { it.id == fromHeroId } ?: return "Brak bohatera: $fromHeroId"
        val to   = party.firstOrNull { it.id == toHeroId }   ?: return "Brak bohatera: $toHeroId"
        val item = GameRepository.state.inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono: $itemId"
        GameRepository.log("Transfer ${item.name}: ${from.name} -> ${to.name}")
        return "Transfer ${item.name}: ${from.name} -> ${to.name}"
    }

    // Sprint 12: zwraca skrocone szczegoly pojedynczego przedmiotu (lub komunikat).
    fun itemDetail(itemId: String): String {
        val item = GameRepository.state.inventory.firstOrNull { it.id == itemId }
            ?: return "Nie znaleziono: $itemId"
        val effects = item.effects.entries.joinToString(", ") { (k, v) -> "$k=$v" }
        return buildString {
            appendLine(item.name)
            appendLine("typ: ${item.type}")
            appendLine("waga: ${item.weight}")
            if (effects.isNotEmpty()) appendLine("efekty: $effects")
        }.trim()
    }

    // Aktywne uzycie itemu (eliksir, ziolo) - zdejmuje przedmiot z inventory, leczy aktywnego bohatera.
    fun useItem(itemId: String): String {
        val state = GameRepository.state
        val item = state.inventory.firstOrNull { it.id == itemId } ?: return "Nie znaleziono: $itemId"
        val hero = PartyRepository.activeHero() ?: return "Brak aktywnego bohatera."
        val heal = item.effects["heal"] ?: 0
        if (heal > 0) {
            hero.hp = (hero.hp + heal).coerceAtMost(hero.maxHp)
        }
        state.inventory.remove(item)
        return "${hero.name} uzyl ${item.name}. +$heal HP"
    }
}
