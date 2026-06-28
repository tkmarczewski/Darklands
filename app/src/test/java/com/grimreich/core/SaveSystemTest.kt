package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SaveSystemTest {

    @Test
    fun saveAndLoad_shouldRoundTripSnapshot() {
        SaveSystem.clearAll()
        val state = GameState().apply { gold = 123 }
        SaveSystem.save(state, slotId = 1, label = "test")

        val loaded = SaveSystem.load(1)

        assertNotNull("Loaded snapshot should not be null", loaded)
        assertEquals("Gold value must survive round trip", 123, loaded!!.state.gold)
        assertEquals("Label must survive round trip", "test", loaded.label)
    }

    @Test
    fun deleteSlot_shouldRemoveSnapshot() {
        SaveSystem.clearAll()
        SaveSystem.save(GameState(), slotId = 2, label = "to-delete")
        SaveSystem.deleteSlot(2)
        assertNull("Deleted slot should be null", SaveSystem.load(2))
    }

    @Test
    fun load_shouldReturnCopyNotLiveReference() {
        SaveSystem.clearAll()
        val state = GameState().apply { gold = 50 }
        SaveSystem.save(state, slotId = 3, label = "copy-check")

        val loaded = SaveSystem.load(3)!!
        loaded.state.gold = 999

        val loadedAgain = SaveSystem.load(3)!!
        assertEquals("Original save should remain unchanged (was gold=50)", 50, loadedAgain.state.gold)
    }
}
