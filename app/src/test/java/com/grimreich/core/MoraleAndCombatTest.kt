package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoraleAndCombatTest {

    @Test
    fun `MoraleSystem buckets cover full range`() {
        assertEquals(MoraleStatus.HEROIC, MoraleSystem.computeStatus(100))
        assertEquals(MoraleStatus.HEROIC, MoraleSystem.computeStatus(80))
        assertEquals(MoraleStatus.STEADY, MoraleSystem.computeStatus(60))
        assertEquals(MoraleStatus.SHAKEN, MoraleSystem.computeStatus(30))
        assertEquals(MoraleStatus.PANICKED, MoraleSystem.computeStatus(10))
        assertEquals(MoraleStatus.ROUTED, MoraleSystem.computeStatus(0))
    }

    @Test
    fun `moraleAfterHit floors at zero`() {
        assertEquals(0, MoraleSystem.moraleAfterHit(5, 30))
    }

    @Test
    fun `moraleAfterKill caps at 100`() {
        assertEquals(100, MoraleSystem.moraleAfterKill(95))
    }

    @Test
    fun `isDefeated triggers on hp zero or routed morale`() {
        val a = CombatantState("X", hp = 0, maxHp = 30, endurance = 5, morale = 80)
        assertTrue(CombatRound.isDefeated(a))

        val b = CombatantState("Y", hp = 10, maxHp = 30, endurance = 5, morale = 0)
        assertTrue(CombatRound.isDefeated(b))

        val c = CombatantState("Z", hp = 30, maxHp = 30, endurance = 5, morale = 80)
        assertEquals(false, CombatRound.isDefeated(c))
    }

    @Test
    fun `postCombatRecovery heals at least 1 hp and bumps morale`() {
        val hero = CombatantState("Hero", hp = 5, maxHp = 30, endurance = 5, morale = 50)
        hero.wounds.add(WoundType.LIGHT)
        val msg = CombatRound.postCombatRecovery(hero)
        assertTrue(hero.hp > 5)
        assertTrue("expected morale increase", hero.morale > 50)
        assertEquals(0, hero.wounds.size)
        assertTrue(msg.contains("Leczenie"))
    }
}
