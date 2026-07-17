package com.grimreich.systems

import com.grimreich.core.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CombatAuditTest {

    @Test
    fun testEnemyTypeExistence() {
        // Verify that past_shade_elite is now part of the enum
        val type = EnemyType.valueOf("past_shade_elite")
        assertEquals("past_shade_elite", type.name)
    }

    @Test
    fun testDeathConsistencyInNormalize() {
        val hero = Hero(id = "test", name = "Test", hp = 10, maxHp = 10, endurance = 5)
        hero.hp = 0
        hero.normalize()
        assertTrue(hero.isDead, "Hero should be dead after HP reaches 0 and normalize is called")
    }
}
