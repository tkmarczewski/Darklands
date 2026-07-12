package com.grimreich.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * NEW — DeepCopyTest
 * Verifies GameState.deepCopy() produces fully isolated copies.
 * Critical for GameRepository.updateState() correctness — no shallow references.
 */
class DeepCopyTest {

    @Test
    fun deepCopy_heroMutations_shouldBeIsolated() {
        val hero = Hero(id = "h1", name = "Test", age = 25)
        hero.activeMutations.add("IRON_BLOOD")

        val state = GameState(party = mutableListOf(hero))
        val copy = state.deepCopy()

        val copyHero = copy.party.first()
        assertNotSame("activeMutations list must not be same reference", hero.activeMutations, copyHero.activeMutations)

        copyHero.activeMutations.add("SHADOW_FLESH")
        assertFalse(
            "Adding mutation to copy should not affect original hero",
            hero.activeMutations.contains("SHADOW_FLESH")
        )
    }

    @Test
    fun deepCopy_heroWounds_shouldBeIsolated() {
        val hero = Hero(id = "h1", name = "Test", age = 25)
        hero.wounds.add(WoundType.LIGHT)

        val state = GameState(party = mutableListOf(hero))
        val copy = state.deepCopy()

        val copyHero = copy.party.first()
        assertNotSame("wounds list must not be same reference", hero.wounds, copyHero.wounds)

        copyHero.wounds.add(WoundType.HEAVY)
        assertFalse(
            "Adding wound to copy should not affect original hero",
            hero.wounds.contains(WoundType.HEAVY)
        )
    }

    @Test
    fun deepCopy_activeQuestIds_shouldBeIsolated() {
        val state = GameState()
        state.activeQuestIds.add("q_plague")

        val copy = state.deepCopy()
        assertNotSame("activeQuestIds must not be same reference", state.activeQuestIds, copy.activeQuestIds)

        copy.activeQuestIds.add("q_collapse")
        assertFalse(
            "Adding quest to copy should not affect original state",
            state.activeQuestIds.contains("q_collapse")
        )
    }

    @Test
    fun deepCopy_partyList_shouldBeIsolated() {
        val hero = Hero(id = "h1", name = "Test", age = 25)
        val state = GameState(party = mutableListOf(hero))
        val copy = state.deepCopy()

        assertNotSame("party list must not be same reference", state.party, copy.party)

        copy.party.removeAll { it.id == "h1" }
        assertTrue(
            "Removing hero from copy should not affect original party",
            state.party.any { it.id == "h1" }
        )
    }
}
