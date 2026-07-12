package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusSynergyTest {

    @Test
    fun wet_shouldNeutralizeFire() {
        val combatant = CombatantState(
            name = "Test", hp = 50, maxHp = 50, endurance = 20, activeEffects = mutableListOf(
                StatusEffect(StatusEffectType.FIRE, 3, 5)
            )
        )
        val log = mutableListOf<String>()

        combatant.applyStatus(StatusEffectType.WET, 2, 3, log)

        assertFalse("FIRE should be removed", combatant.activeEffects.any { it.type == StatusEffectType.FIRE })
        assertTrue("Log should mention neutralization", log.any { it.contains("Woda gasi płomienie") })
    }

    @Test
    fun fire_shouldNeutralizeWet() {
        val combatant = CombatantState(
            name = "Test", hp = 50, maxHp = 50, endurance = 20, activeEffects = mutableListOf(
                StatusEffect(StatusEffectType.WET, 3, 5)
            )
        )
        val log = mutableListOf<String>()

        combatant.applyStatus(StatusEffectType.FIRE, 2, 3, log)

        assertFalse("WET should be removed", combatant.activeEffects.any { it.type == StatusEffectType.WET })
        assertTrue("Log should mention neutralization", log.any { it.contains("Ogień odparowuje wodę") })
    }

    @Test
    fun freezeOnWet_shouldDealShatterDamage() {
        val combatant = CombatantState(
            name = "Test", hp = 50, maxHp = 50, endurance = 20, activeEffects = mutableListOf(
                StatusEffect(StatusEffectType.WET, 3, 5)
            )
        )
        val log = mutableListOf<String>()

        combatant.applyStatus(StatusEffectType.FREEZE, 2, 3, log)

        assertEquals("HP should drop by 10 (shatter damage)", 40, combatant.hp)
        assertTrue("FREEZE should be applied", combatant.activeEffects.any { it.type == StatusEffectType.FREEZE })
        assertTrue("Log should mention shatter", log.any { it.contains("NAGŁE PĘKNIĘCIE") })
    }
}
