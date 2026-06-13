package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TotalProjectTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
        DialogueManager.seedBasicDialogues()
    }

    @Test
    fun `WorldState initialized with defaults`() {
        val w = GameRepository.state.world
        assertNotNull(w.location)
    }

    @Test
    fun `Career bonuses apply correctly`() {
        val hero = Hero(id = "1", name = "Test", age = 20, strength = 10, intelligence = 10)
        val upgraded = CareerChain.applyCareer(Career.KNIGHT, hero)
        assertTrue(upgraded.strength > 10)
    }

    @Test
    fun `Procedural names are valid`() {
        val npcs = ProceduralNpcGenerator.generateForCity("serce_krainy", 123)
        assertTrue(npcs.first().name.isNotEmpty())
    }

    @Test
    fun `Dialogue nodes for all procedural roles exist`() {
        val roles = listOf("chronicler", "zealot", "merchant", "fugitive", "mystic", "gravedigger", "inquisitor", "orphan", "blacksmith", "amnesiac")
        roles.forEach { role ->
            val node = DialogueManager.getNode("${role}_start")
            assertNotNull(node, "Missing start node for $role")
        }
    }

    @Test
    fun `Quest status transitions correctly`() {
        val q = QuestEntry("q1", "T", "D", "c1", QuestOriginType.ZDARZENIE_MIEJSKIE, "ref", 10, QuestStatus.DOSTEPNE)
        QuestSystem.register(q)
        QuestSystem.activate("q1")
        assertEquals(QuestStatus.AKTYWNE, QuestSystem.all().find { it.id == "q1" }?.status)
    }

    @Test
    fun `Save system logic works`() {
        val state = GameRepository.state
        state.gold = 999
        val snapshot = com.grimreich.core.SaveSystem.save(state, 1, "Test")
        assertEquals(999, snapshot.state.gold)
    }
}
