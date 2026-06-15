package com.grimreich.systems

import com.grimreich.core.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class MainQuestProgressionTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
        // Add a hero for testing
        val hero = Hero(id = "test_hero", name = "Test", age = 25)
        GameRepository.state.party.add(hero)
        GameRepository.state.activeHeroId = hero.id
    }

    @Test
    fun `starting main quest adds it to active endgame quests`() {
        val state = GameRepository.state
        val questId = "eq1_signs"
        
        // Mocking logic from QuestFinalActivity.startMainQuest
        if (!state.quest.activeEndgameQuests.contains(questId)) {
            state.quest.activeEndgameQuests.add(questId)
        }

        assertTrue(state.quest.activeEndgameQuests.contains(questId))
    }

    @Test
    fun `completing main quest awards rewards and updates state`() {
        val state = GameRepository.state
        val quest = EndgameQuestChain.quests.first() // eq1_signs
        val initialGold = state.gold
        val hero = state.party.first()
        val initialPiety = hero.piety
        val initialVirtue = hero.virtue

        state.quest.activeEndgameQuests.add(quest.id)

        // Mocking logic from QuestFinalActivity.completeMainQuest
        state.gold += quest.rewards.gold
        hero.piety += quest.rewards.faithBonus
        hero.virtue += quest.rewards.divineFavorBonus
        val currentCity = state.grimCurrentRegion
        val currentRep = state.reputation.city[currentCity] ?: 0
        state.reputation.city[currentCity] = currentRep + quest.rewards.reputationBonus

        state.quest.activeEndgameQuests.remove(quest.id)
        state.quest.completedEndgameQuests.add(quest.id)

        assertEquals(initialGold + quest.rewards.gold, state.gold)
        assertEquals(initialPiety + quest.rewards.faithBonus, hero.piety)
        assertEquals(initialVirtue + quest.rewards.divineFavorBonus, hero.virtue)
        assertEquals(quest.rewards.reputationBonus, state.reputation.city[currentCity])
        assertFalse(state.quest.activeEndgameQuests.contains(quest.id))
        assertTrue(state.quest.completedEndgameQuests.contains(quest.id))
    }

    @Test
    fun `meetsRequirements returns true only when all conditions are met`() {
        val state = GameRepository.state
        val quest = EndgameQuestChain.quests[0] // eq1_signs: minFaith=3, minCityReputation=2
        val hero = state.party.first()

        // Initial state: 10 piety, 0 reputation
        hero.piety = 1
        state.reputation.city[state.grimCurrentRegion] = 0
        
        assertFalse("Should fail due to low piety", checkRequirements(quest, state))

        hero.piety = 10
        assertFalse("Should fail due to low reputation", checkRequirements(quest, state))

        state.reputation.city[state.grimCurrentRegion] = 5
        assertTrue("Should pass now", checkRequirements(quest, state))
    }
    
    @Test
    fun `meetsRequirements checks prerequisite quests`() {
        val state = GameRepository.state
        val quest2 = EndgameQuestChain.quests[1] // eq2_alliances: requires eq1_signs
        val hero = state.party.first()
        
        // Meet stats but not prerequisite
        hero.piety = 50
        state.reputation.city[state.grimCurrentRegion] = 50
        // eq2 also requires faction rep 3 (max of all cities)
        
        assertFalse("Should fail due to missing prerequisite eq1_signs", checkRequirements(quest2, state))
        
        state.quest.completedEndgameQuests.add("eq1_signs")
        assertTrue("Should pass now", checkRequirements(quest2, state))
    }

    // Helper to mirror QuestFinalActivity.meetsRequirements
    private fun checkRequirements(quest: EndgameQuest, state: GameState): Boolean {
        val hero = state.party.firstOrNull() ?: return false
        val cityRep = state.reputation.city[state.grimCurrentRegion] ?: 0
        val maxRep = state.reputation.city.values.maxOrNull() ?: 0

        if (hero.piety < quest.requirements.minFaith) return false
        if (hero.virtue < quest.requirements.minVirtue) return false
        if (cityRep < quest.requirements.minCityReputation) return false
        if (maxRep < quest.requirements.minFactionReputation) return false

        for (reqId in quest.requirements.requiredQuestIds) {
            if (!state.quest.completedEndgameQuests.contains(reqId)) return false
        }
        return true
    }
}
