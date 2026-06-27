package com.grimreich.core

import com.grimreich.core.mutations.Mutation
import com.grimreich.core.mutations.MutationCategory
import com.grimreich.core.mutations.MutationTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DtoIntegrityTest {

    @Test
    fun `verify Hero and Career history integrity through DTO cycle`() {
        // 1. Arrange: Create a hero with complex history
        val originalHero = Hero(
            id = "test_hero",
            name = "Test Knight",
            age = 25,
            strength = 15,
            intelligence = 12
        ).apply {
            careerHistory.add(CareerEntry(Career.PAGE, 2))
            careerHistory.add(CareerEntry(Career.SQUIRE, 3))
            currentCareer = Career.KNIGHT
            
            // Add abilities
            abilities.add(AbilityRegistry.SOLARIAN_STRIKE)
            
            // Add mutations
            activeMutations.add(Mutation(
                id = "mut_iron_skin",
                name = "Żelazna Skóra",
                description = "...",
                category = MutationCategory.PHYSICAL,
                tier = MutationTier.MANIFESTED,
                attributeModifiers = mapOf("endurance" to 2),
                stabilityImpact = -10
            ))
        }

        // 2. Act: Map to DTO and back to Domain
        val dto = originalHero.toDto()
        val restoredHero = dto.toDomain()

        // 3. Assert: Verify critical data points
        assertEquals("ID must match", originalHero.id, restoredHero.id)
        assertEquals("Name must match", originalHero.name, restoredHero.name)
        assertEquals("Current career must match", originalHero.currentCareer, restoredHero.currentCareer)
        
        // Verify Career History (Crucial Phase 4 fix)
        assertEquals("History size must match", 2, restoredHero.careerHistory.size)
        assertEquals("First career in history must match", Career.PAGE, restoredHero.careerHistory[0].career)
        assertEquals("Years served must be preserved", 2, restoredHero.careerHistory[0].yearsServed)
        
        // Verify Abilities (Crucial Phase 5 fix)
        assertEquals("Abilities size must match", 1, restoredHero.abilities.size)
        assertEquals("Ability ID must match", "solarian_strike", restoredHero.abilities[0].id)
        assertEquals("Ability cost type must be restored", CostType.PRAYER, restoredHero.abilities[0].costType)
        
        // Verify Mutations (Phase 6 depth)
        assertEquals("Mutations size must match", 1, restoredHero.activeMutations.size)
        assertEquals("Mutation tier must match", MutationTier.MANIFESTED, restoredHero.activeMutations[0].tier)
    }

    @Test
    fun `verify full GameState integrity through DTO cycle`() {
        val originalState = GameState(
            playerName = "Admin",
            heroName = "Alpha",
            gold = 500,
            activeHeroId = "hero_1"
        ).apply {
            world.globalStability = 15
            world.season = Season.WINTER
            quest.activeQuestIds.add("q_verdict_1")
            reputation.globalFactions["KNIGHTS"] = 25
        }

        val restoredState = originalState.toDto().toDomain()

        assertEquals("Player name preserved", "Admin", restoredState.playerName)
        assertEquals("Stability preserved", 15, restoredState.world.globalStability)
        assertEquals("Season preserved", Season.WINTER, restoredState.world.season)
        assertEquals("Active quest preserved", 1, restoredState.quest.activeQuestIds.size)
        assertEquals("Faction rep preserved", 25, restoredState.reputation.globalFactions["KNIGHTS"])
    }
}
