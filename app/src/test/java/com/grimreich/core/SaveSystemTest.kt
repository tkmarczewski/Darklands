package com.grimreich.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SaveSystemTest {

    private lateinit var saveSystem: SaveSystem

    @Before
    fun setUp() = runBlocking {
        saveSystem = SaveSystem()
    }

    @Test
    fun saveAndLoad_shouldRoundTripSnapshot() = runBlocking {
        val state = GameState().apply { gold = 123 }
        saveSystem.save(state, slotId = 1, label = "test")

        val loaded = saveSystem.load(1)

        assertNotNull("Loaded snapshot should not be null", loaded)
        assertEquals("Gold value must survive round trip", 123, loaded!!.state.gold)
        assertEquals("Label must survive round trip", "test", loaded.label)
    }

    @Test
    fun deleteSlot_shouldRemoveSnapshot() = runBlocking {
        saveSystem.save(GameState(), slotId = 2, label = "to-delete")
        saveSystem.deleteSlot(2)
        assertNull("Deleted slot should be null", saveSystem.load(2))
    }

    @Test
    fun load_shouldReturnCopyNotLiveReference() = runBlocking {
        val state = GameState().apply { gold = 50 }
        saveSystem.save(state, slotId = 3, label = "copy-check")

        val loaded = saveSystem.load(3)!!
        loaded.state.gold = 999

        val loadedAgain = saveSystem.load(3)!!
        assertEquals("Original save should remain unchanged (was gold=50)", 50, loadedAgain.state.gold)
    }

    @Test
    fun stateHash_shouldChangeWhenQuestIdsChange() = runBlocking {
        val state1 = GameState()
        val state2 = GameState().apply { quest.activeQuestIds.add("q_plague") }

        val hash1 = saveSystem.computeStateHash(state1)
        val hash2 = saveSystem.computeStateHash(state2)

        assertNotEquals(
            "computeStateHash must differ when activeQuestIds differ",
            hash1,
            hash2
        )
    }

    @Test
    fun stateHash_shouldChangeWhenReputationChanges() = runBlocking {
        val state1 = GameState()
        val state2 = GameState().apply { reputation.globalFactions["test"] = 50 }

        val hash1 = saveSystem.computeStateHash(state1)
        val hash2 = saveSystem.computeStateHash(state2)

        assertNotEquals(
            "computeStateHash must differ when reputation differs",
            hash1,
            hash2
        )
    }
}
