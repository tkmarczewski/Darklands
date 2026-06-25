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

        register(Item(
            id = "axe_1h",
            name = "Topór bojowy",
            type = "weapon",
            slot = "weapon",
            value = 25,
            weight = 3.0,
            lore = "Ostra krawędź wykuta w głębiach Gór Południowych. Idealna do rozłupywania tarcz i czaszek.",
            effects = mapOf("attack" to 12, "minStrength" to 9),
            properties = mapOf("weaponType" to "axe", "icon" to "ic_item_axe_1h")
        ))

        // ARMOR
        register(Item(
            id = "armor_leather",
            name = "Skórzana zbroja",
            type = "armor",
            slot = "armor",
            value = 20,
            weight = 5.0,
            lore = "Wzmocniona skóra dzikiego zwierza z Ziem Dzikich. Zapewnia podstawową ochronę bez krępowania ruchów.",
            effects = mapOf("armor" to 2, "defense" to 1),
            properties = mapOf("icon" to "ic_item_armor_leather")
        ))

        register(Item(
            id = "armor_plate",
            name = "Pancerz płytowy",
            type = "armor",
            slot = "armor",
            value = 80,
            weight = 15.0,
            lore = "Majstersztyk kowalski z czasów przed Era of Fracture. Ciężka stal, która może zatrzymać nawet ciosy demonów.",
            effects = mapOf("armor" to 8, "defense" to -2, "minStrength" to 12),
            properties = mapOf("icon" to "ic_item_armor_plate")
        ))

        // SHIELDS
        register(Item(
            id = "shield_round",
            name = "Okrągła tarcza",
            type = "shield",
            slot = "shield",
            value = 15,
            weight = 4.0,
            lore = "Drewniana tarcza okuta żelazem. Prosta, ale skuteczna w rękach wprawnego wojownika.",
            effects = mapOf("defense" to 4, "armor" to 1),
            properties = mapOf("icon" to "ic_item_shield_round")
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

        // ACCESSORIES
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
            lore = "Kawałek rzeczywistości, która przestała istnieć. Jest tak czarny, że wzrok ześlizguje się z jego krawędzi. Prawdziwy skarb dla kolekcjonerów anomalii.",
            effects = mapOf("corruption" to 5),
            properties = mapOf("icon" to "ic_artifact_core")
        ))

        // FACTION EXCLUSIVE ITEMS (NEW)
        register(Item(
            id = "inquisitor_seal",
            name = "Pieczęć Inkwizytora",
            type = "relic",
            slot = "accessory",
            value = 300,
            weight = 0.2,
            rarity = "rare",
            lore = "Złoty sygnet z wyrytym okiem Trybunału. Daje prawo do zadawania pytań, na które nikt nie chce odpowiadać.",
            effects = mapOf("perception" to 5, "piety" to 3, "armor" to 2),
            properties = mapOf("icon" to "ic_artifact_eye", "faction" to "inkwizycja")
        ))

        register(Item(
            id = "dawns_ember",
            name = "Żar Świtu",
            type = "relic",
            slot = "accessory",
            value = 300,
            weight = 0.1,
            rarity = "rare",
            lore = "Wiecznie ciepły odłamek pierwszej latarni Zakonu. Rozprasza mrok w duszy i chroni przed szaleństwem.",
            effects = mapOf("sanity" to 10, "piety" to 5, "defense" to 3),
            properties = mapOf("icon" to "ic_artifact_stone", "faction" to "zakon")
        ))
    }
}
