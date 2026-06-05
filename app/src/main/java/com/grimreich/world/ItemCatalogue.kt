package com.grimreich.world

import com.grimreich.grimreich.v1.Item

object ItemCatalogue {
    private val items = linkedMapOf<String, Item>()

    init {
        seed()
    }

    fun register(item: Item) {
        items[item.id] = item
    }

    fun findById(id: String): Item? = items[id]
    fun all(): List<Item> = items.values.toList()

    fun seed() {
        if (items.isNotEmpty()) return
        
        register(Item("sword_iron", "Żelazny Miecz", "weapon", "weapon", 50, 2.5, rarity = "normal", effects = mapOf("attack" to 8, "minStrength" to 12)))
        register(Item("sword_shadow", "Miecz Cienia", "weapon", "weapon", 500, 2.0, rarity = "rare", effects = mapOf("attack" to 15, "poison_chance" to 20)))
        register(Item("armor_leather", "Skórzana Zbroja", "armor", "armor", 80, 5.0, effects = mapOf("defense" to 4)))
        register(Item("armor_plate", "Płyta Rycerska", "armor", "armor", 1200, 15.0, rarity = "epic", effects = mapOf("defense" to 12, "minStrength" to 15)))
        register(Item("potion_hp", "Mikstura Zdrowia", "potion", null, 25, 0.5, effects = mapOf("heal" to 15)))
        register(Item("relic_rosary", "Zbezczeszczony Różaniec", "relic", "trinket", 300, 0.2, rarity = "rare", effects = mapOf("piety" to 5, "corruption" to 2)))
    }
}
