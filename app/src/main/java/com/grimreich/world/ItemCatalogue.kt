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
        
        // WEAPONS
        register(Item(id = "sword_basic", name = "Miecz", type = "weapon", slot = "weapon", value = 20, weight = 2.0, effects = mapOf("attack" to 5)))
        register(Item(id = "sword_long", name = "Długi miecz", type = "weapon", slot = "weapon", value = 40, weight = 3.0, effects = mapOf("attack" to 8)))
        register(Item(id = "sword_short", name = "Krótki miecz", type = "weapon", slot = "weapon", value = 15, weight = 1.5, effects = mapOf("attack" to 4)))
        register(Item(id = "axe_hand", name = "Toporek", type = "weapon", slot = "weapon", value = 15, weight = 2.5, effects = mapOf("attack" to 6)))
        register(Item(id = "spear_basic", name = "Włócznia", type = "weapon", slot = "weapon", value = 10, weight = 3.0, effects = mapOf("attack" to 5)))
        register(Item(id = "dagger_basic", name = "Sztylet", type = "weapon", slot = "weapon", value = 8, weight = 0.5, effects = mapOf("attack" to 3)))
        register(Item(id = "dagger_stiletto", name = "Stilet", type = "weapon", slot = "weapon", value = 12, weight = 0.4, effects = mapOf("attack" to 4)))
        register(Item(id = "staff_basic", name = "Kostur", type = "weapon", slot = "weapon", value = 5, weight = 1.5, effects = mapOf("attack" to 2, "magic" to 2)))
        register(Item(id = "mace_basic", name = "Buława", type = "weapon", slot = "weapon", value = 18, weight = 3.5, effects = mapOf("attack" to 7)))
        register(Item(id = "hammer_basic", name = "Młot", type = "weapon", slot = "weapon", value = 15, weight = 4.0, effects = mapOf("attack" to 8)))

        // ARMORS
        register(Item(id = "armor_cloth", name = "Szaty", type = "armor", slot = "armor", value = 5, weight = 1.0, effects = mapOf("armor" to 1)))
        register(Item(id = "armor_leather", name = "Skórzana zbroja", type = "armor", slot = "armor", value = 25, weight = 5.0, effects = mapOf("armor" to 3)))
        register(Item(id = "armor_leather_light", name = "Lekka skóra", type = "armor", slot = "armor", value = 15, weight = 3.0, effects = mapOf("armor" to 2)))
        register(Item(id = "armor_chainmail", name = "Kolczuga", type = "armor", slot = "armor", value = 60, weight = 15.0, effects = mapOf("armor" to 6)))
        register(Item(id = "armor_plate_partial", name = "Półpancerz", type = "armor", slot = "armor", value = 120, weight = 25.0, effects = mapOf("armor" to 10)))

        // POTIONS
        register(Item(id = "pot_heal", name = "Mikstura Zdrowia", type = "potion", value = 25, weight = 0.5, effects = mapOf("heal" to 20)))
        register(Item(id = "pot_mana", name = "Mikstura Many", type = "potion", value = 25, weight = 0.5, effects = mapOf("mana" to 15)))
        register(Item(id = "pot_sanity", name = "Kojący Wywar", type = "potion", value = 40, weight = 0.3, effects = mapOf("sanity" to 15)))
        register(Item(id = "pot_str", name = "Eliksir Siły", type = "potion", value = 50, weight = 0.5, effects = mapOf("strength" to 2)))
        register(Item(id = "pot_agi", name = "Eliksir Zręczności", type = "potion", value = 50, weight = 0.5, effects = mapOf("agility" to 2)))

        // INGREDIENTS
        register(Item(id = "ing_herb", name = "Zioła", type = "ingredient", value = 5, weight = 0.1))
        register(Item(id = "ing_blue_dust", name = "Niebieski pył", type = "ingredient", value = 10, weight = 0.1))
        register(Item(id = "ing_red_dust", name = "Czerwony pył", type = "ingredient", value = 10, weight = 0.1))
        register(Item(id = "ing_yellow_dust", name = "Żółty pył", type = "ingredient", value = 10, weight = 0.1))
        register(Item(id = "ing_bone", name = "Kość", type = "ingredient", value = 2, weight = 0.5))
        register(Item(id = "ing_feather", name = "Pióro", type = "ingredient", value = 3, weight = 0.05))
        register(Item(id = "ing_echo_dust", name = "Pył Echa", type = "ingredient", value = 15, weight = 0.1))

        // QUEST ITEMS
        register(Item(
            id = "quest_corpse", 
            name = "Zwłoki towarzysza", 
            type = "quest", 
            value = 0, 
            weight = 50.0, 
            lore = "Ciało, które niegdyś miało duszę. Ciężkie i emanujące chłodem."
        ))
    }
}
