package com.grimreich

import com.grimreich.core.PlayerState
import com.grimreich.systems.SaveLoadSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveLoadSystemTest {

    @Before
    fun reset() {
        SaveLoadSystem.clear()
    }

    @Test
    fun `save and load preserve player state`() {
        val state = PlayerState(currentCityId = "praha", gold = 222)
        SaveLoadSystem.save(state, "done")

        val loaded = SaveLoadSystem.load()
        assertTrue(loaded != null)
        assertEquals("praha", loaded!!.playerState.currentCityId)
        assertEquals(222, loaded.playerState.gold)
        assertEquals("done", loaded.lastResolutionSummary)
    }
}
