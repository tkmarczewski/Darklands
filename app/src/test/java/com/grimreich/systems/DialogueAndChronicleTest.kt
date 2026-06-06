package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DialogueAndChronicleTest {

    @Before
    fun setup() {
        GameRepository.state = GameState()
    }

    @Test
    fun `ConversationManager handles choices and selects new node`() {
        val startNode = DialogueNode(
            id = "node_1",
            npcId = "npc_1",
            text = "Hello",
            choices = listOf(DialogueChoice("Hi", "node_2"))
        )
        val nextNode = DialogueNode(id = "node_2", npcId = "npc_1", text = "Welcome")
        
        ConversationManager.registerDialogue(startNode)
        ConversationManager.registerDialogue(nextNode)
        
        val choice = startNode.choices.first()
        val resultNode = ConversationManager.makeChoice(choice)
        
        assertNotNull(resultNode)
        assertEquals("node_2", resultNode?.id)
    }

    @Test
    fun `ChronicleSystem records and summarizes deeds`() {
        ChronicleSystem.record("Defeated the Shadow Knight", importance = 3)
        ChronicleSystem.record("Saved the village", importance = 2)
        
        val summary = ChronicleSystem.getSummary()
        assertTrue(summary.contains("Defeated the Shadow Knight"))
        assertTrue(summary.contains("Saved the village"))
        assertEquals(2, ChronicleSystem.getAll().size)
    }

    @Test
    fun `EndingSystem calculates ending based on final stats`() {
        val s = GameRepository.state
        s.prayer.faith = 70
        s.prayer.virtue = 60
        s.world.globalStability = 90
        s.prayer.sins = 0
        
        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.GOOD, ending.type)
        assertTrue(ending.title.contains("Święte"))
    }
}
