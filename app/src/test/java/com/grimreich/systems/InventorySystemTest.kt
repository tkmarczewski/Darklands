package com.grimreich.systems

import com.grimreich.TestSupport
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InventorySystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoSeeded()
    }

    @Test
    fun `equip puts item id into hero equipment slot`() {
        val msg = InventorySystem.equip("hero_1", "sword_01")

        val hero = GameRepository.state.party.first { it.id == "hero_1" }
        assertEquals("sword_01", hero.equipment["weapon"])
        assertTrue(msg.contains("zalozyl"))
    }

    @Test
    fun `equip rejects unknown hero`() {
        val msg = InventorySystem.equip("nope", "sword_01")
        assertTrue(msg.startsWith("Brak bohatera"))
    }

    @Test
    fun `equip rejects unknown item`() {
        val msg = InventorySystem.equip("hero_1", "ghost_item")
        assertTrue(msg.startsWith("Nie znaleziono"))
    }

    @Test
    fun `equip rejects item with no slot`() {
        val msg = InventorySystem.equip("hero_1", "herb_01")
        assertTrue(msg.endsWith("nie ma slotu"))
    }

    @Test
    fun `equip enforces minimum strength requirement`() {
        GameRepository.state.inventory.add(
            Item("heavy_sword", "Ciezki Miecz", "weapon", "weapon", 200, 6.0, mapOf("attack" to 12, "minStrength" to 20))
        )
        val hero = GameRepository.state.party.first { it.id == "hero_1" }
        val previousSlot = hero.equipment["weapon"]

        val msg = InventorySystem.equip("hero_1", "heavy_sword")

        assertTrue(msg.contains("za slaby"))
        assertEquals(previousSlot, hero.equipment["weapon"])
    }

    @Test
    fun `unequip clears the slot`() {
        InventorySystem.equip("hero_1", "sword_01")
        val hero = GameRepository.state.party.first { it.id == "hero_1" }
        assertEquals("sword_01", hero.equipment["weapon"])

        val msg = InventorySystem.unequip("hero_1", "weapon")
        assertNull(hero.equipment["weapon"])
        assertTrue(msg.contains("zdjal"))
    }

    @Test
    fun `unequip reports empty slot`() {
        val msg = InventorySystem.unequip("hero_1", "helmet")
        assertTrue(msg.startsWith("Slot helmet jest pusty"))
    }

    @Test
    fun `listInventory returns lines per item with type-specific details`() {
        val out = InventorySystem.listInventory()
        assertTrue(out.contains("Zelazny Miecz"))
        assertTrue(out.contains("ATK:8"))
        assertTrue(out.contains("DEF:4"))
        assertTrue(out.contains("Ziele Lecznicze"))
    }

    @Test
    fun `listInventory message when inventory empty`() {
        GameRepository.state.inventory.clear()
        assertEquals("Ekwipunek jest pusty", InventorySystem.listInventory())
    }

    @Test
    fun `totalWeight sums equipped item weights`() {
        InventorySystem.equip("hero_1", "sword_01")
        InventorySystem.equip("hero_1", "armor_01")

        val w = InventorySystem.totalWeight("hero_1")
        // sword 2.5 + armor 5.0 = 7.5
        assertEquals(7.5f, w, 0.001f)
    }

    @Test
    fun `totalWeight returns 0 for unknown hero`() {
        assertEquals(0f, InventorySystem.totalWeight("nope"), 0.001f)
    }

    @Test
    fun `transferItem logs and reports transfer between heroes`() {
        val sizeBefore = GameRepository.state.logEntries.size
        val msg = InventorySystem.transferItem("hero_1", "hero_2", "sword_01")
        assertTrue(msg.contains("Friedrich"))
        assertTrue(msg.contains("Hildegard"))
        assertTrue(GameRepository.state.logEntries.size > sizeBefore)
    }

    @Test
    fun `itemDetail returns formatted block with effects line`() {
        val detail = InventorySystem.itemDetail("sword_01")
        assertTrue(detail.contains("Zelazny Miecz"))
        assertTrue(detail.contains("typ: weapon"))
        assertTrue(detail.contains("waga: 2.5"))
        assertTrue(detail.contains("attack=8"))
    }

    @Test
    fun `itemDetail unknown item`() {
        assertTrue(InventorySystem.itemDetail("nope").startsWith("Nie znaleziono"))
    }

    @Test
    fun `useItem heals active hero and removes the consumable`() {
        val active = GameRepository.state.party.first { it.id == "hero_1" }
        active.hp = 10
        val sizeBefore = GameRepository.state.inventory.size

        val msg = InventorySystem.useItem("potion_01")

        assertEquals(25, active.hp) // 10 + 15
        assertEquals(sizeBefore - 1, GameRepository.state.inventory.size)
        assertNull(GameRepository.state.inventory.firstOrNull { it.id == "potion_01" })
        assertTrue(msg.contains("+15 HP"))
    }

    @Test
    fun `useItem clamps heal to maxHp`() {
        val active = GameRepository.state.party.first { it.id == "hero_1" }
        active.hp = active.maxHp - 2

        InventorySystem.useItem("potion_01")

        assertEquals(active.maxHp, active.hp)
    }

    @Test
    fun `useItem with no active hero returns guard message`() {
        // wyczysc aktywnego herosa
        GameRepository.state.activeHeroId = ""
        val msg = InventorySystem.useItem("potion_01")
        assertTrue(msg.contains("Brak aktywnego bohatera"))
    }
}
