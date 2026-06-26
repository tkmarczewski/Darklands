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
            effects = mapOf("attack" to 10, "minStrength" to 7), 
            properties = mapOf("weaponType" to "sword", "icon" to "ic_item_sword_1h")
        ))

        // POTIONS
        register(Item(
            id = "potion_hp", 
            name = "Mikstura Zdrowia", 
            type = "potion", 
            slot = null, 
            value = 25, 
            weight = 0.5, 
            lore = "Gęsty, szkarłatny płyn, który smakuje metalem i ziemią. Legenda mówi, że pierwsze mikstury warzono z krwi tych, którzy widzieli Absolut.",
            effects = mapOf("heal" to 20), 
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
            id = "potion_sanity",
            name = "Kojący Wywar",
            type = "potion",
            value = 40,
            weight = 0.3,
            lore = "Napój o smaku lawendy i zapomnienia. Pozwala na chwilę uciszyć głosy Skrybów.",
            effects = mapOf("sanity" to 15),
            properties = mapOf("icon" to "ic_scroll_ice")
        ))

        // ALCHEMY INGREDIENTS
        register(Item(
            id = "ing_echo_dust",
            name = "Pył Echa",
            type = "ingredient",
            value = 10,
            weight = 0.1,
            lore = "Drobinki rzeczywistości, które osiadają na przedmiotach dotkniętych Pęknięciem.",
            properties = mapOf("icon" to "ic_artifact_stone")
        ))

        register(Item(
            id = "ing_blood_root",
            name = "Krwawy Korzeń",
            type = "ingredient",
            value = 15,
            weight = 0.2,
            lore = "Roślina, która rośnie tylko tam, gdzie ziemia piła krew królów.",
            properties = mapOf("icon" to "ic_artifact_blood")
        ))

        register(Item(
            id = "ing_mist_essence",
            name = "Esencja Mgły",
            type = "ingredient",
            value = 20,
            weight = 0.05,
            lore = "Uwięziony w butelce opar z Wybrzeża Północnego. Ulotny i niebezpieczny.",
            properties = mapOf("icon" to "ic_artifact_core")
        ))

        // UNIQUE
        register(Item(
            id = "amulet_pilgrim",
            name = "Amulet Pielgrzyma",
            type = "relic",
            slot = "accessory",
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
            slot = "accessory",
            value = 500,
            weight = 1.0,
            rarity = "unique",
            lore = "Kawałek rzeczywistości, która przestała istnieć. Jest tak czarny, że wzrok ześlizguje się z jego krawędzi.",
            effects = mapOf("corruption" to 5),
            properties = mapOf("icon" to "ic_artifact_core")
        ))
    }
}
