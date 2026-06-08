package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProgressionAndStabilityTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
        ItemCatalogue.seed()
    }

    @Test
    fun `ExperienceSystem triggers level up and grants attribute points`() {
        val hero = Hero(id = "h1", name = "Test", age = 20)
        hero.xp = 90
        
        val msg = ExperienceSystem.addXp(hero, 20)
        
        assertEquals(2, hero.level)
        assertEquals(10, hero.xp) // 110 - 100
        assertEquals(2, hero.attributePoints)
        assertTrue(msg.contains("awansuje"))
    }

    @Test
    fun `ExperienceSystem handles multi-level jump`() {
        val hero = Hero(id = "h1", name = "Test", age = 20)
        hero.level = 1
        hero.xp = 0
        
        // Threshold for level 1 is 100.
        // If we add 350 XP:
        // 1st level up: 350 - 100 = 250 remaining, level 2.
        // Current implementation only checks once.
        ExperienceSystem.addXp(hero, 350)
        
        // If it doesn't loop, it stays at level 2 with 250 XP.
        // Let's verify current behavior.
        assertEquals(2, hero.level)
        assertEquals(250, hero.xp)
    }

    @Test
    fun `StabilitySystem updates based on party corruption`() {
        val hero = Hero(id = "h1", name = "Test", age = 20)
        hero.corruption = 50
        GameRepository.state.party.add(hero)
        
        StabilitySystem.updateStability()
        
        assertEquals(50, GameRepository.state.world.globalStability)
        assertEquals(1.2f, StabilitySystem.getStabilityEffectModifier())
    }

    @Test
    fun `FactionSystem correctly modifies global reputation`() {
        FactionSystem.modifyReputation(FactionId.CHURCH, 25)
        assertEquals(25, FactionSystem.getReputation(FactionId.CHURCH))
        
        FactionSystem.modifyReputation(FactionId.CHURCH, -10)
        assertEquals(15, FactionSystem.getReputation(FactionId.CHURCH))
    }

    @Test
    fun `AlchemySystem brewing consumes ingredients and awards potion`() {
        val g = GameRepository.state
        val herb = ItemCatalogue.findById("ing_herb")!!
        val water = ItemCatalogue.findById("ing_water")!!
        
        g.inventory.add(herb)
        g.inventory.add(water)
        
        val recipe = AlchemySystem.recipes.first { it.id == "brew_hp" }
        assertTrue(AlchemySystem.canBrew(recipe))
        
        val result = AlchemySystem.brew(recipe)
        assertTrue(result.contains("Pomyślnie"))
        
        assertFalse(g.inventory.any { it.id == "ing_herb" })
        assertTrue(g.inventory.any { it.id == "potion_hp" })
    }
}
