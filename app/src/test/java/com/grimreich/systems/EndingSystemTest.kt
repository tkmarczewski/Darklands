package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EndingSystemTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
    }

    @Test
    fun `resolveEnding returns GOOD when faith and stability are high`() {
        val s = GameRepository.state
        s.prayer.faith = 70
        s.prayer.virtue = 60
        s.world.globalStability = 90
        s.prayer.sins = 0
        
        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.GOOD, ending.type)
    }

    @Test
    fun `resolveEnding returns CORRUPTED when stability is low`() {
        val s = GameRepository.state
        s.world.globalStability = 10
        
        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.CORRUPTED, ending.type)
    }
}
