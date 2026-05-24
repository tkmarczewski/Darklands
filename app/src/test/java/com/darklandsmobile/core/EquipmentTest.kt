package com.darklandsmobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EquipmentTest {

    private fun newHero(str: Int = 10, agi: Int = 10) = Hero(
        id = "h", name = "T", age = 25,
        strength = str, agility = agi, intelligence = 10,
        endurance = 10, charisma = 10, piety = 10
    )

    @Test
    fun `WeaponCatalogue exposes sword`() {
        val w = WeaponCatalogue.findById("sword_short")!!
        assertEquals(WeaponType.SWORD, w.type)
        assertEquals(EquipmentSlot.WEAPON, w.slot)
    }

    @Test
    fun `EquippedItems total stats and weight aggregate`() {
        val sword = WeaponCatalogue.findById("sword_long")!!
        val chain = ArmorCatalogue.findById("chain_shirt")!!
        val helm = ArmorCatalogue.findById("helmet_iron")!!

        val gear = EquippedItems(weapon = sword, bodyArmor = chain, helmet = helm)

        assertEquals(sword.damage, gear.totalAttack())
        assertEquals(chain.defense + helm.defense, gear.totalDefense())
        assertEquals(sword.weight + chain.weight + helm.weight, gear.totalWeight(), 0.0001f)
    }

    @Test
    fun `canEquipWeapon enforces strength and agility`() {
        val longSword = WeaponCatalogue.findById("sword_long")!! // requires str 7
        val weak = newHero(str = 3, agi = 10)
        val strong = newHero(str = 8, agi = 10)
        val gear = EquippedItems()
        assertFalse(gear.canEquipWeapon(longSword, weak))
        assertTrue(gear.canEquipWeapon(longSword, strong))
    }

    @Test
    fun `WeaponQualitySystem mace has higher penetration than sword`() {
        val mace = WeaponCatalogue.findById("mace")!!
        val sword = WeaponCatalogue.findById("sword_long")!!
        assertTrue(
            WeaponQualitySystem.armorPenetration(mace) >
            WeaponQualitySystem.armorPenetration(sword)
        )
    }

    @Test
    fun `WeaponQualitySystem netDamage respects penetration`() {
        val mace = WeaponCatalogue.findById("mace")!!
        val chain = ArmorCatalogue.findById("chain_shirt")!!
        val net = WeaponQualitySystem.netDamage(
            mace, ItemQuality.NORMAL, chain, ItemQuality.NORMAL
        )
        assertTrue("net damage should be at least 1", net >= 1)
    }

    @Test
    fun `effectiveDamage applies quality bonus`() {
        val sword = WeaponCatalogue.findById("sword_long")!!
        val normal = WeaponQualitySystem.effectiveDamage(sword, ItemQuality.NORMAL)
        val masterwork = WeaponQualitySystem.effectiveDamage(sword, ItemQuality.MASTERWORK)
        assertTrue(masterwork > normal)
    }
}
