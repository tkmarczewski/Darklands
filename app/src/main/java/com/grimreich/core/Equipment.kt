package com.grimreich.core

import com.grimreich.grimreich.v1.Item

// ==================== EQUIPMENT SLOT ====================

enum class EquipmentSlot {
    WEAPON, SHIELD, HELMET, BODY_ARMOR, GLOVES, BOOTS, RING, AMULET
}

// ==================== EQUIPPED ITEMS ====================

data class EquippedItems(
    var weapon: Item? = null,
    var shield: Item? = null,
    var helmet: Item? = null,
    var bodyArmor: Item? = null,
    var accessory: Item? = null
) {
    fun totalDefense(): Int =
        (shield?.effects?.get("defense") ?: 0) + 
        (helmet?.effects?.get("defense") ?: 0) + 
        (bodyArmor?.effects?.get("defense") ?: 0) +
        (accessory?.effects?.get("defense") ?: 0)

    fun totalAttack(): Int =
        (weapon?.effects?.get("attack") ?: 1) +
        (accessory?.effects?.get("attack") ?: 0)

    fun totalWeight(): Float =
        ((weapon?.weight ?: 0.0) + (shield?.weight ?: 0.0) +
        (helmet?.weight ?: 0.0) + (bodyArmor?.weight ?: 0.0) +
        (accessory?.weight ?: 0.0)).toFloat()

    fun canEquip(item: Item, hero: Hero): Boolean {
        val reqStr = item.effects["minStrength"] ?: 0
        val reqAgi = item.effects["minAgility"] ?: 0
        return hero.strength >= reqStr && hero.agility >= reqAgi
    }
}


// ==================== ITEM QUALITY ====================

enum class ItemQuality(val label: String, val damageBonus: Int, val defenseBonus: Int, val penetrationBonus: Int) {
    POOR       ("Lichej jakosci",      -2, -1, -1),
    NORMAL     ("Standardowe",          0,  0,  0),
    GOOD       ("Dobrej jakosci",       1,  1,  1),
    EXCELLENT  ("Doskonale",            2,  2,  2),
    MASTERWORK ("Mistrzowskie",         3,  3,  3)
}

// ==================== WEAPON QUALITY SYSTEM ====================

object WeaponQualitySystem {

    fun effectiveDamage(item: Item, quality: ItemQuality = ItemQuality.NORMAL): Int {
        val baseAtk = item.effects["attack"] ?: 0
        return (baseAtk + quality.damageBonus).coerceAtLeast(1)
    }

    fun armorPenetration(item: Item, quality: ItemQuality = ItemQuality.NORMAL): Int {
        val basePenetration = when (item.properties["weaponType"]) {
            "mace", "flail" -> 3
            "axe"           -> 2
            "sword"         -> 1
            "dagger"        -> 2
            "spear"         -> 2
            "bow", "crossbow" -> 1
            else            -> 0
        }
        return (basePenetration + quality.penetrationBonus).coerceAtLeast(0)
    }

    fun netDamage(weapon: Item, weaponQuality: ItemQuality, armor: Item, armorQuality: ItemQuality): Int {
        val attack = effectiveDamage(weapon, weaponQuality)
        val pen    = armorPenetration(weapon, weaponQuality)
        val baseDef = armor.effects["defense"] ?: 0
        val defense = (baseDef + armorQuality.defenseBonus - pen).coerceAtLeast(0)
        return (attack - defense).coerceAtLeast(1)
    }

    fun qualityDescription(quality: ItemQuality, itemName: String): String =
        "$itemName [${quality.label}]"
}
