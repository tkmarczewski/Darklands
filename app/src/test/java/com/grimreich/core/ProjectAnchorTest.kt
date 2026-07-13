package com.grimreich.core

import com.grimreich.systems.WorldStabilitySystem
import com.grimreich.systems.CollapseEngine
import com.grimreich.systems.CollapseEvent
import com.grimreich.contracts.CollapseRandomProvider
import com.grimreich.grimreich.v1.OntologicalLevel
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

class ProjectAnchorTest {

    @Test
    fun testAnchorIdentityPersistence() {
        val state = GameState(playerName = "TheAnchor")
        state.normalizeState()
        
        assertEquals("TheAnchor", state.persistentMeta.anchorIdentity)
        
        // Reset game state but keep persistent meta (simulating new loop)
        val newState = GameState()
        newState.persistentMeta = state.persistentMeta
        newState.normalizeState()
        
        assertEquals("TheAnchor", newState.persistentMeta.anchorIdentity)
    }

    @Test
    fun testPhenomenaBloodTax() {
        val repository = mock(GameRepository::class.java)
        val system = WorldStabilitySystem(repository)
        val state = GameState()
        
        // In the Ledger, phenomenon "Krew" is in "twierdza_zelazna"
        state.world.locationId = "twierdza_zelazna"
        
        // Use default Hero which has endurance 10
        // maxHp = 10 * 2 (HP_PER_ENDURANCE) + 20 (HP_BASE_BONUS) = 40
        val hero = Hero(id = "h1", name = "Vessel")
        hero.hp = 10 
        hero.normalize() 
        // hero.hp should be 10, hero.maxHp should be 40
        
        state.party.add(hero)
        
        // Trigger a stability change to invoke the tax
        system.changeStabilityDirect(state, -1, "Any reason")
        
        // 10 HP - 1 HP Tax = 9 HP
        assertEquals(9, state.party[0].hp)
        assertTrue(state.logEntries.any { it.contains("Kotwica porusza się") })
    }
    
    @Test
    fun testTheBreathCondition() {
        val state = GameState()
        state.world.ontologicalLevel = OntologicalLevel.ABSOLUTE
        state.world.globalStability = 100
        
        val engine = CollapseEngine(
            mock(GameRepository::class.java),
            mock(WorldStabilitySystem::class.java),
            mock(CollapseRandomProvider::class.java)
        )
        
        // DayEnded event triggers processCollapseEventDirect
        engine.processCollapseEventDirect(state, CollapseEvent.DayEnded)
        
        assertTrue(state.logEntries.any { it.contains("TRIBUNAL_LOG: Nie ma już błędów") })
    }
}
