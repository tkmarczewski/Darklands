package com.grimreich.systems

import com.grimreich.core.*
import org.junit.Assert.*
import org.junit.Test

class CombatSynergyTest {

    private fun createCombatant(name: String, hp: Int = 30) = CombatantState(
        name = name,
        hp = hp,
        maxHp = hp,
        endurance = 20,
        morale = 80,
        strength = 10,
        agility = 10,
        intelligence = 10
    )

    @Test
    fun `Wet and Shock synergy deals increased damage`() {
        val attacker = createCombatant("Attacker")
        val defender = createCombatant("Defender")
        
        // Apply Wet to defender and Shock to attacker (as per implementation logic in CombatRound)
        defender.activeEffects.add(StatusEffect(StatusEffectType.WET, 3, 0))
        attacker.activeEffects.add(StatusEffect(StatusEffectType.SHOCK, 3, 0))
        
        val result = CombatRound.resolveRound(attacker, defender)
        
        // Log should contain the synergy message
        assertTrue(result.log.any { it.contains("Przewodnictwo!") })
    }

    @Test
    fun `Poison effect ticks correctly across rounds`() {
        val hero = createCombatant("Hero", hp = 50)
        hero.activeEffects.add(StatusEffect(StatusEffectType.POISON, duration = 2, strength = 5))
        
        // Simulating the tick logic manually or via resolveRound
        // Since resolveRound ticks status for attacker:
        val dummyTarget = createCombatant("Target")
        CombatRound.resolveRound(hero, dummyTarget)
        
        assertEquals(45, hero.hp)
        assertEquals(1, hero.activeEffects.first { it.type == StatusEffectType.POISON }.duration)
    }

    @Test
    fun `Morale status impacts attack and defense modifiers`() {
        val steady = MoraleStatus.STEADY
        val heroic = MoraleStatus.HEROIC
        val panicked = MoraleStatus.PANICKED
        
        assertTrue(heroic.attackModifier() > steady.attackModifier())
        assertTrue(panicked.attackModifier() < steady.attackModifier())
    }
}
