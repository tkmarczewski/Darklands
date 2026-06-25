package com.grimreich.world

import com.grimreich.grimreich.v1.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemCatalogue @Inject constructor() {
    private val items = linkedMapOf<String, Item>()

    fun register(item: Item) {
        items[item.id] = item
    }

    fun findById(id: String): Item? = items[id]
    fun all(): List<Item> = items.values.toList()

    fun getRandomItem(): Item? = items.values.toList().randomOrNull()

    fun get(id: String): Item? = findById(id)

    fun seed() {
        if (items.isNotEmpty()) return
        
        register(Item("sword_short", "Krótki miecz", "weapon", "weapon", 15, 1.5, rarity = "normal", effects = mapOf("attack" to 6, "minStrength" to 4), properties = mapOf("weaponType" to "sword", "icon" to "ic_item_sword_1h")))
        register(Item("sword_long", "Długi miecz", "weapon", "weapon", 35, 2.5, rarity = "normal", effects = mapOf("attack" to 9, "minStrength" to 7), properties = mapOf("weaponType" to "sword", "icon" to "ic_item_sword_1h")))
        register(Item("potion_hp", "Mikstura Zdrowia", "potion", null, 25, 0.5, effects = mapOf("heal" to 15), properties = mapOf("icon" to "ic_item_potion_hp")))
        register(Item("potion_mana", "Mikstura Many", "potion", null, 25, 0.5, effects = mapOf("mana" to 15), properties = mapOf("icon" to "ic_item_potion_mana")))
    }
}
