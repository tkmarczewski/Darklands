package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Test

class HeroTest {

    @Test
    fun `normalize calculates maxHp correctly`() {
        val hero = Hero(id = "h1", name = "Test", age = 25, endurance = 10)
        hero.normalize()
        // maxHp = 10 * 2 + 20 = 40
        assertEquals(40, hero.maxHp)
        assertEquals(40, hero.hp)
    }

    @Test
    fun `increasing endurance increases maxHp and current hp`() {
        val hero = Hero(id = "h1", name = "Test", age = 25, endurance = 10)
        hero.normalize()
        assertEquals(40, hero.maxHp)
        assertEquals(40, hero.hp)

        // Upgrade endurance
        hero.endurance = 15
        hero.normalize()
        
        // newMaxHp = 15 * 2 + 20 = 50
        // oldMaxHp = 40
        // hp increase = 50 - 40 = 10
        // newHp = 40 + 10 = 50
        assertEquals(50, hero.maxHp)
        assertEquals(50, hero.hp)
    }

    @Test
    fun `hp does not exceed maxHp after normalization`() {
        val hero = Hero(id = "h1", name = "Test", age = 25, endurance = 10)
        hero.normalize()
        hero.hp = 100
        hero.normalize()
        assertEquals(40, hero.hp)
    }

    @Test
    fun `hero cannot have negative hp`() {
        val hero = Hero(id = "h1", name = "Test", age = 25, endurance = 10)
        hero.normalize()
        hero.hp = -10
        hero.normalize()
        assertEquals(0, hero.hp)
    }
}
