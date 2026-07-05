package com.grimreich.systems

import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.core.GameRepository
import org.mockito.kotlin.mock
import dagger.Lazy
import org.junit.Assert.assertEquals
import org.junit.Test

class ExperienceSystemTest {

    private val gameRepository = mock<GameRepository>()
    private val experienceSystem = ExperienceSystem(object : Lazy<GameRepository> {
        override fun get(): GameRepository = gameRepository
    })

    @Test
    fun `addPartyXpDirect adds XP and handles level up`() {
        val hero = Hero(id = "hero1", name = "Hans", age = 25, endurance = 10)
        hero.normalize() // maxHp = 40
        
        val state = GameState(party = mutableListOf(hero))
        
        // Level 1: 100 XP to level up
        experienceSystem.addPartyXpDirect(state, 50)
        assertEquals(50, hero.xp)
        assertEquals(1, hero.level)
        assertEquals(0, hero.attributePoints)

        experienceSystem.addPartyXpDirect(state, 60)
        assertEquals(10, hero.xp) // 50 + 60 - 100
        assertEquals(2, hero.level)
        assertEquals(2, hero.attributePoints)
    }

    @Test
    fun `level up multiple times in one go`() {
        val hero = Hero(id = "hero1", name = "Hans", age = 25)
        val state = GameState(party = mutableListOf(hero))
        
        // Level 1 -> 2: 100 XP
        // Level 2 -> 3: 200 XP
        // Total: 300 XP to reach Level 3
        experienceSystem.addPartyXpDirect(state, 350)
        
        assertEquals(50, hero.xp)
        assertEquals(3, hero.level)
        assertEquals(4, hero.attributePoints)
    }
}
