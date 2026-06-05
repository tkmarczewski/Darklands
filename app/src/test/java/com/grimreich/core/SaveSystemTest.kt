package com.grimreich.core

import com.grimreich.TestSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoSeeded()
        (0 until 3).forEach { SaveSystem.deleteSlot(it) }
    }

    @Test
    fun `save stores snapshot under slot id`() {
        val snap = SaveSystem.save(GameRepository.state, slotId = 1, label = "Test")
        assertEquals(SAVE_VERSION, snap.version)
        assertEquals("Test", snap.label)
        assertNotNull(SaveSystem.load(1))
    }

    @Test
    fun `save defaults label when blank`() {
        val snap = SaveSystem.save(GameRepository.state, slotId = 2, label = "")
        assertEquals("Save 3", snap.label)
    }

    @Test
    fun `load empty slot returns null`() {
        assertNull(SaveSystem.load(0))
    }

    @Test
    fun `getSlot reports emptiness correctly`() {
        val empty = SaveSystem.getSlot(0)
        assertTrue(empty.isEmpty)

        SaveSystem.save(GameRepository.state, slotId = 0)
        val full = SaveSystem.getSlot(0)
        assertFalse(full.isEmpty)
    }

    @Test
    fun `getAllSlots returns N slot entries`() {
        val slots = SaveSystem.getAllSlots(count = 3)
        assertEquals(3, slots.size)
        assertEquals(listOf(0, 1, 2), slots.map { it.slotId })
    }

    @Test
    fun `deleteSlot removes snapshot`() {
        SaveSystem.save(GameRepository.state, slotId = 0)
        SaveSystem.deleteSlot(0)
        assertNull(SaveSystem.load(0))
    }

    @Test
    fun `validate flags incompatible version`() {
        val snap = SaveSnapshot(version = 999, timestamp = 0L, label = "x", state = GameRepository.state)
        val res = SaveSystem.validate(snap)
        assertFalse(res.isCompatible)
        assertTrue(res.message.contains("Niezgodna wersja"))
    }

    @Test
    fun `isCompatible recognizes supported version range`() {
        val ok = SaveSnapshot(version = SAVE_VERSION, timestamp = 0L, label = "ok", state = GameRepository.state)
        assertTrue(SaveSystem.isCompatible(ok))
        val older = SaveSnapshot(version = 1, timestamp = 0L, label = "older", state = GameRepository.state)
        assertTrue(SaveSystem.isCompatible(older))
        val future = SaveSnapshot(version = SAVE_VERSION + 5, timestamp = 0L, label = "future", state = GameRepository.state)
        assertFalse(SaveSystem.isCompatible(future))
    }

    @Test
    fun `migrateIfNeeded bumps old version to current`() {
        val old = SaveSnapshot(version = 1, timestamp = 0L, label = "old", state = GameRepository.state)
        val migrated = SaveSystem.migrateIfNeeded(old)
        assertEquals(SAVE_VERSION, migrated.version)
    }

    @Test
    fun `migrateIfNeeded keeps current version snapshots untouched`() {
        val cur = SaveSnapshot(version = SAVE_VERSION, timestamp = 7L, label = "cur", state = GameRepository.state)
        val migrated = SaveSystem.migrateIfNeeded(cur)
        assertEquals(7L, migrated.timestamp)
        assertEquals(SAVE_VERSION, migrated.version)
    }
}
