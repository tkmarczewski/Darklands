package com.grimreich.systems

import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import org.junit.Assert.*
import org.junit.Test

class CharacterSheetTest {

    @Test
    fun `hero equipment slots are managed correctly`() {
        val hero = Hero(id = "h1", name = "Elara", age = 25)
        
        // Initial state
        assertNull(hero.equipment["weapon"])
        
        // Equip logic (from InventorySystem)
        hero.equipment["weapon"] = "sword_01"
        assertEquals("sword_01", hero.equipment["weapon"])
    }

    @Test
    fun `item effects calculations`() {
        val sword = Item(id = "s1", name = "Sword", type = "weapon", effects = mapOf("attack" to 5))
        val armor = Item(id = "a1", name = "Armor", type = "armor", effects = mapOf("defense" to 3))
        
        assertEquals(5, sword.effects["attack"])
        assertEquals(3, armor.effects["defense"])
    }
}
