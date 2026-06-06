package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReligionSystemTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
    }

    @Test
    fun `pray increases hero piety and global faith`() {
        val hero = Hero(id = "h1", name = "Test", age = 20)
        val initialPiety = hero.piety
        val initialFaith = GameRepository.state.prayer.faith
        
        ReligionSystem.pray(hero)
        
        assertTrue(hero.piety > initialPiety)
        assertTrue(GameRepository.state.prayer.faith > initialFaith)
    }

    @Test
    fun `allSaints returns canonical prophets`() {
        val saints = ReligionSystem.allSaints()
        assertTrue(saints.any { it.name.contains("Aelion") })
    }
}
