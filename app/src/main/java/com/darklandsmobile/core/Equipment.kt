package com.darklandsmobile.core

// ==================== EQUIPMENT SLOT ====================

enum class EquipmentSlot {
    WEAPON, SHIELD, HELMET, BODY_ARMOR, GLOVES, BOOTS, RING, AMULET
}

// ==================== WEAPON ====================

enum class WeaponType {
    SWORD, AXE, MACE, SPEAR, BOW, CROSSBOW, DAGGER, STAFF, FLAIL
}

enum class WeaponRange {
    MELEE, RANGED
}

data class Weapon(
    val id: String,
    val name: String,
    val type: WeaponType,
    val range: WeaponRange,
    val damage: Int,
    val weight: Float,
    val requiredStrength: Int = 0,
    val requiredAgility: Int = 0,
    val virtueModifier: Int = 0,
    val price: Int = 10,
    val description: String = ""
) {
    val slot: EquipmentSlot get() = EquipmentSlot.WEAPON
}

object WeaponCatalogue {
    val weapons = listOf(
        Weapon("sword_short", "Krótki miecz", WeaponType.SWORD, WeaponRange.MELEE, damage = 6, weight = 1.5f, requiredStrength = 4, price = 15),
        Weapon("sword_long", "Długi miecz", WeaponType.SWORD, WeaponRange.MELEE, damage = 9, weight = 2.5f, requiredStrength = 7, price = 35),
        Weapon("axe_hand", "Toporań", WeaponType.AXE, WeaponRange.MELEE, damage = 7, weight = 1.8f, requiredStrength = 5, price = 12),
        Weapon("axe_battle", "Topor bojowy", WeaponType.AXE, WeaponRange.MELEE, damage = 11, weight = 3.0f, requiredStrength = 9, price = 28),
        Weapon("mace", "Maczuga", WeaponType.MACE, WeaponRange.MELEE, damage = 8, weight = 2.0f, requiredStrength = 6, price = 18),
        Weapon("spear", "Włócznia", WeaponType.SPEAR, WeaponRange.MELEE, damage = 8, weight = 2.2f, requiredStrength = 5, requiredAgility = 3, price = 14),
        Weapon("bow", "Łuk", WeaponType.BOW, WeaponRange.RANGED, damage = 7, weight = 1.0f, requiredAgility = 5, price = 20),
        Weapon("crossbow", "Kusza", WeaponType.CROSSBOW, WeaponRange.RANGED, damage = 10, weight = 2.5f, requiredStrength = 4, price = 30),
        Weapon("dagger", "Sztylet", WeaponType.DAGGER, WeaponRange.MELEE, damage = 4, weight = 0.5f, requiredAgility = 2, price = 8),
        Weapon("staff", "Kostur", WeaponType.STAFF, WeaponRange.MELEE, damage = 5, weight = 1.2f, virtueModifier = 1, price = 10)
    )

    fun findById(id: String) = weapons.firstOrNull { it.id == id }
}

// ==================== ARMOR ====================

enum class ArmorType {
    CLOTH, LEATHER, CHAIN, PLATE, SHIELD
}

data class Armor(
    val id: String,
    val name: String,
    val type: ArmorType,
    val slot: EquipmentSlot,
    val defense: Int,
    val weight: Float,
    val requiredStrength: Int = 0,
    val price: Int = 10,
    val description: String = ""
)

object ArmorCatalogue {
    val armors = listOf(
        Armor("cloth_robe", "Płaszcz płócienny", ArmorType.CLOTH, EquipmentSlot.BODY_ARMOR, defense = 1, weight = 0.5f, price = 5),
        Armor("leather_vest", "Skórzana kamizelka", ArmorType.LEATHER, EquipmentSlot.BODY_ARMOR, defense = 3, weight = 1.5f, requiredStrength = 2, price = 15),
        Armor("chain_shirt", "Kolczuga", ArmorType.CHAIN, EquipmentSlot.BODY_ARMOR, defense = 6, weight = 4.0f, requiredStrength = 5, price = 45),
        Armor("plate_armor", "Zbroja płytowa", ArmorType.PLATE, EquipmentSlot.BODY_ARMOR, defense = 12, weight = 8.0f, requiredStrength = 9, price = 120),
        Armor("helmet_leather", "Skórzany hełm", ArmorType.LEATHER, EquipmentSlot.HELMET, defense = 2, weight = 0.8f, price = 10),
        Armor("helmet_iron", "Żelazny hełm", ArmorType.CHAIN, EquipmentSlot.HELMET, defense = 4, weight = 1.5f, requiredStrength = 3, price = 25),
        Armor("shield_wooden", "Drewniana tarcza", ArmorType.SHIELD, EquipmentSlot.SHIELD, defense = 3, weight = 2.0f, price = 12),
        Armor("shield_iron", "Żelazna tarcza", ArmorType.SHIELD, EquipmentSlot.SHIELD, defense = 6, weight = 3.5f, requiredStrength = 5, price = 30)
    )

    fun findById(id: String) = armors.firstOrNull { it.id == id }
}

// ==================== EQUIPPED ITEMS ====================

data class EquippedItems(
    var weapon: Weapon? = null,
    var shield: Armor? = null,
    var helmet: Armor? = null,
    var bodyArmor: Armor? = null
) {
    fun totalDefense(): Int =
        (shield?.defense ?: 0) + (helmet?.defense ?: 0) + (bodyArmor?.defense ?: 0)

    fun totalAttack(): Int =
        weapon?.damage ?: 1

    fun totalWeight(): Float =
        (weapon?.weight ?: 0f) + (shield?.weight ?: 0f) +
        (helmet?.weight ?: 0f) + (bodyArmor?.weight ?: 0f)

    fun canEquipWeapon(w: Weapon, hero: Hero): Boolean =
        hero.strength >= w.requiredStrength && hero.agility >= w.requiredAgility

    fun canEquipArmor(a: Armor, hero: Hero): Boolean =
        hero.strength >= a.requiredStrength
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

    // Effective damage after quality and armor penetration applied
    fun effectiveDamage(weapon: Weapon, quality: ItemQuality = ItemQuality.NORMAL): Int =
        (weapon.damage + quality.damageBonus).coerceAtLeast(1)

    // Armor penetration: some weapons ignore part of armor defense
    // Maces/hammers vs chain/plate: high penetration
    // Swords vs leather: medium penetration
    fun armorPenetration(weapon: Weapon, quality: ItemQuality = ItemQuality.NORMAL): Int {
        val basePenetration = when (weapon.type) {
            WeaponType.MACE, WeaponType.FLAIL -> 3   // Blunt weapons penetrate chain/plate
            WeaponType.AXE                   -> 2
            WeaponType.SWORD                 -> 1
            WeaponType.DAGGER                -> 2   // Daggers find gaps in armor
            WeaponType.SPEAR                 -> 2
            WeaponType.BOW, WeaponType.CROSSBOW -> 1
            else                             -> 0
        }
        return (basePenetration + quality.penetrationBonus).coerceAtLeast(0)
    }

    // Net damage after armor reduction, considering penetration
    // penetration reduces effective armor defense
    fun netDamage(weapon: Weapon, weaponQuality: ItemQuality, armor: Armor, armorQuality: ItemQuality): Int {
        val attack = effectiveDamage(weapon, weaponQuality)
        val pen    = armorPenetration(weapon, weaponQuality)
        val defense = (armor.defense + armorQuality.defenseBonus - pen).coerceAtLeast(0)
        return (attack - defense).coerceAtLeast(1)
    }

    fun qualityDescription(quality: ItemQuality, itemName: String): String =
        "$itemName [${quality.label}]"
}
