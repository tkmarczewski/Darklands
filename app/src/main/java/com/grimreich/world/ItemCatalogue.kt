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
        
        register(Item(
            id = "sword_short", 
            name = "Krótki miecz", 
            type = "weapon", 
            slot = "weapon", 
            value = 15, 
            weight = 1.5, 
            rarity = "normal", 
            lore = "Zwyczajna stal, hartowana w rzadkim świetle pełni. Na głowni widnieje zatarte godło dawnej straży miejskiej.",
            effects = mapOf("attack" to 6, "minStrength" to 4), 
            properties = mapOf("weaponType" to "sword", "icon" to "ic_item_sword_1h")
        ))

        register(Item(
            id = "sword_long", 
            name = "Długi miecz", 
            type = "weapon", 
            slot = "weapon", 
            value = 35, 
            weight = 2.5, 
            rarity = "normal", 
            lore = "Ciężka, dwuręczna głownia, która pamięta czasy przed Pęknięciem. Wymaga silnego ramienia i jeszcze silniejszej woli.",
            effects = mapOf("attack" to 9, "minStrength" to 7), 
            properties = mapOf("weaponType" to "sword", "icon" to "ic_item_sword_1h")
        ))

        register(Item(
            id = "potion_hp", 
            name = "Mikstura Zdrowia", 
            type = "potion", 
            slot = null, 
            value = 25, 
            weight = 0.5, 
            lore = "Gęsty, szkarłatny płyn, który smakuje metalem i ziemią. Legenda mówi, że pierwsze mikstury warzono z krwi tych, którzy widzieli Absolut.",
            effects = mapOf("heal" to 15), 
            properties = mapOf("icon" to "ic_item_potion_hp")
        ))

        register(Item(
            id = "potion_mana", 
            name = "Mikstura Many", 
            type = "potion", 
            slot = null, 
            value = 25, 
            weight = 0.5, 
            lore = "Błękitna esencja, która pulsuje w rytm uderzeń serca. Odświeża umysł, ale pozostawia po sobie dziwne, obce wspomnienia.",
            effects = mapOf("mana" to 15), 
            properties = mapOf("icon" to "ic_item_potion_mana")
        ))

        register(Item(
            id = "amulet_pilgrim",
            name = "Amulet Pielgrzyma",
            type = "relic",
            value = 100,
            weight = 0.1,
            rarity = "unique",
            lore = "Symbol wiary w niedokończonym świecie. Ten, kto go nosi, słyszy szepty Proroków wyraźniej niż inni.",
            effects = mapOf("piety" to 2),
            properties = mapOf("icon" to "ic_artifact_blood")
        ))

        register(Item(
            id = "void_fragment",
            name = "Fragment Pustki",
            type = "artifact",
            value = 500,
            weight = 1.0,
            rarity = "unique",
            lore = "Kawałek rzeczywistości, która przestała istnieć. Jest tak czarny, że wzrok ześlizguje się z jego krawędzi. Prawdziwy skarb dla kolekcjonerów anomalii.",
            effects = mapOf("corruption" to 5),
            properties = mapOf("icon" to "ic_artifact_core")
        ))
    }
}
