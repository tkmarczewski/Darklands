package com.grimreich.core

import com.grimreich.core.mutations.Mutation
import com.grimreich.core.mutations.MutationCategory
import com.grimreich.core.mutations.MutationTier
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
        hero.activeMutations.add(Mutation(id = "IRON_BLOOD", name = "Iron Blood", description = "", category = MutationCategory.PHYSICAL, tier = MutationTier.DORMANT))

        val state = GameState(party = mutableListOf(hero))
        val copy = state.deepCopy()

        val copyHero = copy.party.first()
        assertNotSame("activeMutations list must not be same reference", hero.activeMutations, copyHero.activeMutations)

        copyHero.activeMutations.add(Mutation(id = "SHADOW_FLESH", name = "Shadow Flesh", description = "", category = MutationCategory.PHYSICAL, tier = MutationTier.DORMANT))
        assertFalse(
            "Adding mutation to copy should not affect original hero",
            hero.activeMutations.any { it.id == "SHADOW_FLESH" }
        )
    }

    // Hero does not have wounds in current Hero.kt.
    // Removing deepCopy_heroWounds_shouldBeIsolated() or updating to use appropriate fields if they existed.
    // Since wounds are currently managed differently (e.g. in CombatRound), skipping this test.

    @Test
    fun deepCopy_activeQuestIds_shouldBeIsolated() {
        val state = GameState()
        state.quest.activeQuestIds.add("q_plague")

        val copy = state.deepCopy()
        assertNotSame("activeQuestIds must not be same reference", state.quest.activeQuestIds, copy.quest.activeQuestIds)

        copy.quest.activeQuestIds.add("q_collapse")
        assertFalse(
            "Adding quest to copy should not affect original state",
            state.quest.activeQuestIds.contains("q_collapse")
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
