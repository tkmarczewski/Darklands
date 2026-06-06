package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.DialogueNode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GrimReichFinalTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
    }

    @Test
    fun `StabilitySystem calculates echo intensity at low stability`() {
        val s = GameRepository.state
        s.party.add(Hero("h", "H", 20).apply { corruption = 60 })
        
        StabilitySystem.updateStability()
        
        assertTrue(s.world.globalStability < 50)
        assertTrue(s.world.echoIntensity > 0f)
    }

    @Test
    fun `ConversationManager applies echo effect to dialogue`() {
        val node = DialogueNode("n_start", "npc_1", "Oto długa wiadomość do testowania echa.")
        ConversationManager.registerDialogue(node)
        
        GameRepository.state.world.echoIntensity = 0.9f
        
        val result = ConversationManager.start("npc_1")
        assertNotNull(result)
    }

    @Test
    fun `CollapseEngine decides scenario based on corruption`() {
        val s = GameRepository.state
        s.world.collapseProgress = 0.5f
        s.party.add(Hero("h", "H", 20).apply { corruption = 90 })
        
        CollapseEngine.tick()
        
        assertEquals(CollapseScenario.BLOOD_RUIN, CollapseEngine.activeScenario)
    }

    @Test
    fun `MutationEngine applies ontological mutations`() {
        val hero = Hero("h", "H", 20)
        val initialStr = hero.strength
        
        val msg = MutationEngine.applyMutation(hero, OntologicalMutationType.PHYSICAL)
        
        assertTrue(msg.contains("Ciało"))
        assertEquals(initialStr + 2, hero.strength)
    }
}
