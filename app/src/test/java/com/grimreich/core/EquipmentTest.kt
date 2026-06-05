package com.grimreich.core

import com.grimreich.world.ItemCatalogue
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
    fun `ItemCatalogue exposes sword`() {
        val w = ItemCatalogue.findById("sword_short")!!
        assertEquals("weapon", w.type)
        assertEquals("weapon", w.slot)
    }

    @Test
    fun `EquippedItems total stats and weight aggregate`() {
        val sword = ItemCatalogue.findById("sword_long")!!
        val chain = ItemCatalogue.findById("chain_shirt")!!
        val helm = ItemCatalogue.findById("helmet_iron")!!

        val gear = EquippedItems(weapon = sword, bodyArmor = chain, helmet = helm)

        assertEquals(sword.effects["attack"], gear.totalAttack())
        assertEquals((chain.effects["defense"] ?: 0) + (helm.effects["defense"] ?: 0), gear.totalDefense())
        assertEquals((sword.weight + chain.weight + helm.weight).toFloat(), gear.totalWeight(), 0.0001f)
    }

    @Test
    fun `canEquip enforces strength and agility`() {
        val longSword = ItemCatalogue.findById("sword_long")!! // requires str 7
        val weak = newHero(str = 3, agi = 10)
        val strong = newHero(str = 8, agi = 10)
        val gear = EquippedItems()
        assertFalse(gear.canEquip(longSword, weak))
        assertTrue(gear.canEquip(longSword, strong))
    }

    @Test
    fun `WeaponQualitySystem mace has higher penetration than sword`() {
        val mace = ItemCatalogue.findById("mace")!!
        val sword = ItemCatalogue.findById("sword_long")!!
        assertTrue(
            WeaponQualitySystem.armorPenetration(mace) >
            WeaponQualitySystem.armorPenetration(sword)
        )
    }

    @Test
    fun `WeaponQualitySystem netDamage respects penetration`() {
        val mace = ItemCatalogue.findById("mace")!!
        val chain = ItemCatalogue.findById("chain_shirt")!!
        val net = WeaponQualitySystem.netDamage(
            mace, ItemQuality.NORMAL, chain, ItemQuality.NORMAL
        )
        assertTrue("net damage should be at least 1", net >= 1)
    }

    @Test
    fun `effectiveDamage applies quality bonus`() {
        val sword = ItemCatalogue.findById("sword_long")!!
        val normal = WeaponQualitySystem.effectiveDamage(sword, ItemQuality.NORMAL)
        val masterwork = WeaponQualitySystem.effectiveDamage(sword, ItemQuality.MASTERWORK)
        assertTrue(masterwork > normal)
    }
}
