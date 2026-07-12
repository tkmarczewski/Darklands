package com.grimreich.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SaveSystemTest {

    // AUDIT FIX: isolate global singleton state — clear before each test
    @Before
    fun setUp() = runBlocking {
        SaveSystem.clearAll()
    }

    @Test
    fun saveAndLoad_shouldRoundTripSnapshot() = runBlocking {
        val state = GameState().apply { gold = 123 }
        SaveSystem.save(state, slotId = 1, label = "test")

        val loaded = SaveSystem.load(1)

        assertNotNull("Loaded snapshot should not be null", loaded)
        assertEquals("Gold value must survive round trip", 123, loaded!!.state.gold)
        assertEquals("Label must survive round trip", "test", loaded.label)
    }

    @Test
    fun deleteSlot_shouldRemoveSnapshot() = runBlocking {
        SaveSystem.save(GameState(), slotId = 2, label = "to-delete")
        SaveSystem.deleteSlot(2)
        assertNull("Deleted slot should be null", SaveSystem.load(2))
    }

    @Test
    fun load_shouldReturnCopyNotLiveReference() = runBlocking {
        val state = GameState().apply { gold = 50 }
        SaveSystem.save(state, slotId = 3, label = "copy-check")

        val loaded = SaveSystem.load(3)!!
        loaded.state.gold = 999

        val loadedAgain = SaveSystem.load(3)!!
        assertEquals("Original save should remain unchanged (was gold=50)", 50, loadedAgain.state.gold)
    }

    // NEW: hash must differ when activeQuestIds change (plan naprawy — autosave sensitivity)
    @Test
    fun stateHash_shouldChangeWhenQuestIdsChange() = runBlocking {
        val state1 = GameState()
        val state2 = GameState().apply { activeQuestIds.add("q_plague") }

        val hash1 = SaveSystem.computeStateHash(state1)
        val hash2 = SaveSystem.computeStateHash(state2)

        assertNotEquals(
            "computeStateHash must differ when activeQuestIds differ",
            hash1,
            hash2
        )
    }

    // NEW: hash must differ when reputation changes
    @Test
    fun stateHash_shouldChangeWhenReputationChanges() = runBlocking {
        val state1 = GameState()
        val state2 = GameState().apply { reputation = 50 }

        val hash1 = SaveSystem.computeStateHash(state1)
        val hash2 = SaveSystem.computeStateHash(state2)

        assertNotEquals(
            "computeStateHash must differ when reputation differs",
            hash1,
            hash2
        )
    }
}
