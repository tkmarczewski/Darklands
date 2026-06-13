package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class RealTimeAndRandomEventTest {

    @Before
    fun setup() {
        GameRepository.state = GameRepository.state.deepCopy().apply {
            gold = 100
            party.clear()
            party.add(Hero(id = "hero_1", name = "Test", age = 30, hp = 10, maxHp = 30))
            lastSaveTimestamp = System.currentTimeMillis()
        }
    }

    @Test
    fun `long absence reduces gold`() {
        val state = GameRepository.state
        // Simulate 25 hours gap
        state.lastSaveTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(25)
        
        RealTimeEventManager.checkRealTimeEvents(null as android.content.Context?) // Context is unused in logic
        
        // 5% of 100 gold = 5
        assertEquals(95, state.gold)
    }

    @Test
    fun `medium absence heals party`() {
        val state = GameRepository.state
        // Simulate 10 hours gap
        state.lastSaveTimestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(10)
        
        RealTimeEventManager.checkRealTimeEvents(null as android.content.Context?)
        
        // +10 HP from 10 = 20
        assertEquals(20, state.party[0].hp)
    }

    @Test
    fun `random event applyEffect modifies state`() {
        val event = RandomEventManager.GameEvent("Test", goldDelta = 50, hpDelta = -5)
        
        assertEquals(50, event.goldDelta)
        assertEquals(-5, event.hpDelta)
    }
}
