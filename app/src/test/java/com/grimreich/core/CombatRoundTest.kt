package com.grimreich.core

import com.grimreich.systems.SkillCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FixedRandomProvider(
    private val floats: MutableList<Float> = mutableListOf(),
    private val ints: MutableList<Int> = mutableListOf()
) : CombatRandomProvider {
    override fun nextFloat(): Float = if (floats.isNotEmpty()) floats.removeAt(0) else 0.5f
    
    override fun nextInt(until: Int): Int {
        if (until <= 0) return 0
        return (if (ints.isNotEmpty()) ints.removeAt(0) else 0).coerceIn(0, until - 1)
    }
    
    override fun nextInt(from: Int, until: Int): Int {
        if (until <= from) return from
        val raw = if (ints.isNotEmpty()) ints.removeAt(0) else from
        return raw.coerceIn(from, until - 1)
    }
}

class CombatRoundTest {

    private fun makeCombatant(
        name: String,
        hp: Int = 30,
        maxHp: Int = 30,
        endurance: Int = 20,
        morale: Int = 80,
        armor: Int = 0,
        attackBase: Int = 10,
        strength: Int = 10,
        agility: Int = 10,
        perception: Int = 10,
        intelligence: Int = 10
    ) = CombatantState(
        name = name,
        hp = hp,
        maxHp = maxHp,
        endurance = endurance,
        morale = morale,
        armor = armor,
        attackBase = attackBase,
        strength = strength,
        agility = agility,
        perception = perception,
        intelligence = intelligence
    )

    @Test
    fun skillDamage_shouldBeReported() {
        val morale = MoraleSystem()
        val rng = FixedRandomProvider()
        val combat = CombatRound(morale, rng)

        val attacker = makeCombatant(name = "A", strength = 12)
        val defender = makeCombatant(name = "D", armor = 0)

        // "bash" is registered in SkillCatalogue
        val result = combat.resolveRound(attacker, defender, "bash")

        assertTrue("Damage should be > 0", result.attackerDamage > 0)
        assertTrue("Defender HP should decrease", defender.hp < 30)
    }

    @Test
    fun postCombatRecovery_shouldClampAndHeal() {
        val morale = MoraleSystem()
        val rng = FixedRandomProvider(ints = mutableListOf(10))
        val combat = CombatRound(morale, rng)

        val unit = makeCombatant(name = "X", hp = 1, maxHp = 10, endurance = 1, morale = 10)
        unit.wounds.add(WoundType.LIGHT)

        val result = combat.postCombatRecovery(unit)

        assertTrue(result.contains("Leczenie"))
        assertTrue("HP should be within bounds", unit.hp in 1..10)
        assertTrue("Endurance should be within bounds", unit.endurance in 1..99)
        assertTrue("At most one wound should remain (one was removed)", unit.wounds.size <= 1)
    }

    @Test
    fun woundShouldNotDuplicate() {
        val morale = MoraleSystem()
        val rng = FixedRandomProvider()
        val combat = CombatRound(morale, rng)

        val unit = makeCombatant(name = "X", hp = 1, maxHp = 10, endurance = 1)
        val method = combat.javaClass.getDeclaredMethod("applyWound", CombatantState::class.java, MutableList::class.java)
        method.isAccessible = true

        method.invoke(combat, unit, mutableListOf<String>())
        method.invoke(combat, unit, mutableListOf<String>())

        assertEquals("Should only have 1 unique wound of this type", 1, unit.wounds.distinct().size)
    }
}
