package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ProceduralNpcGenerator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MassiveCoverageTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
    }

    @Test
    fun `CalendarAuraSystem returns a valid aura for any day`() {
        val aura = CalendarAuraSystem.getCurrentAura()
        assertNotNull(aura.name)
        assertNotNull(aura.effect)
    }

    @Test
    fun `ProceduralNpcGenerator creates a variety of NPCs`() {
        val npcs = ProceduralNpcGenerator.generateForCity("serce_krainy", 456)
        assertTrue(npcs.size in 4..7)
        npcs.forEach { npc ->
            assertNotNull(npc.name)
            assertNotNull(npc.role)
            assertTrue(npc.startNodeId?.endsWith("_start") == true)
        }
    }

    @Test
    fun `QuestSystem seeds and activates quests`() {
        QuestSystem.clear()
        QuestSystem.seedIntegratedContent(123)
        val all = QuestSystem.all()
        assertTrue(all.isNotEmpty())
        
        val firstId = all[0].id
        QuestSystem.activate(firstId)
        val activated = QuestSystem.all().find { it.id == firstId }
        assertEquals(QuestStatus.AKTYWNE, activated?.status)
        
        QuestSystem.complete(firstId)
        val completed = QuestSystem.all().find { it.id == firstId }
        assertEquals(QuestStatus.UKONCZONE, completed?.status)
    }

    @Test
    fun `DialogueManager maps all career roles to portraits`() {
        val roles = listOf("Rycerz", "Alchemik", "Lowca", "Mag", "Aelion", "Xyrel")
        roles.forEach { role ->
            val port = DialogueManager.getPortrait(role)
            assertTrue(port.startsWith("port_"), "Role $role should have a port_ asset")
        }
    }
}
