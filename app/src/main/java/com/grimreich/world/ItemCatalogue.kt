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
        
        // Weapons
        register(Item("sword_short", "Krótki miecz", "weapon", "weapon", 15, 1.5, rarity = "normal", effects = mapOf("attack" to 6, "minStrength" to 4), properties = mapOf("weaponType" to "sword")))
        register(Item("sword_long", "Długi miecz", "weapon", "weapon", 35, 2.5, rarity = "normal", effects = mapOf("attack" to 9, "minStrength" to 7), properties = mapOf("weaponType" to "sword")))
        register(Item("axe_hand", "Toporek", "weapon", "weapon", 12, 1.8, rarity = "normal", effects = mapOf("attack" to 7, "minStrength" to 5), properties = mapOf("weaponType" to "axe")))
        register(Item("mace", "Maczuga", "weapon", "weapon", 18, 2.0, rarity = "normal", effects = mapOf("attack" to 8, "minStrength" to 6), properties = mapOf("weaponType" to "mace")))
        register(Item("spear", "Włócznia", "weapon", "weapon", 14, 2.2, rarity = "normal", effects = mapOf("attack" to 8, "minStrength" to 5, "minAgility" to 3), properties = mapOf("weaponType" to "spear")))
        
        // Rare Weapons
        register(Item("sword_shadow", "Miecz Cienia", "weapon", "weapon", 500, 2.0, rarity = "rare", effects = mapOf("attack" to 15, "poison_chance" to 20), properties = mapOf("weaponType" to "sword")))
        
        // Armor
        register(Item("cloth_robe", "Płaszcz płócienny", "armor", "armor", 5, 0.5, effects = mapOf("defense" to 1)))
        register(Item("leather_vest", "Skórzana kamizelka", "armor", "armor", 15, 1.5, effects = mapOf("defense" to 3, "minStrength" to 2)))
        register(Item("chain_shirt", "Kolczuga", "armor", "armor", 45, 4.0, effects = mapOf("defense" to 6, "minStrength" to 5)))
        register(Item("plate_armor", "Zbroja płytowa", "armor", "armor", 120, 8.0, rarity = "epic", effects = mapOf("defense" to 12, "minStrength" to 9)))
        
        // Helmets & Shields
        register(Item("helmet_iron", "Żelazny hełm", "armor", "helmet", 25, 1.5, effects = mapOf("defense" to 4, "minStrength" to 3)))
        register(Item("shield_wooden", "Drewniana tarcza", "armor", "shield", 12, 2.0, effects = mapOf("defense" to 3)))
        
        // Consumables
        register(Item("potion_hp", "Mikstura Zdrowia", "potion", null, 25, 0.5, effects = mapOf("heal" to 15)))
        
        // Relics
        register(Item("relic_rosary", "Zbezczeszczony Różaniec", "relic", "trinket", 300, 0.2, rarity = "rare", effects = mapOf("piety" to 5, "corruption" to 2)))
        
        // Ingredients
        register(Item("ing_herb", "Święte Ziele", "ingredient", null, 10, 0.1))
        register(Item("ing_root", "Przeklęty Korzeń", "ingredient", null, 15, 0.2))
        register(Item("ing_water", "Woda Święcona", "ingredient", null, 20, 0.5))
    }
}
