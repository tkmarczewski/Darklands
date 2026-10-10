package com.grimreich.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SaveIntegrityTest {

    @Test
    fun `generateChecksum produces consistent output`() = runBlocking {
        val json = "{\"gold\": 100, \"day\": 1}"
        val checksum1 = SaveIntegrity.generateChecksum(json)
        val checksum2 = SaveIntegrity.generateChecksum(json)
        
        assertEquals("Checksums should be identical for same input", checksum1, checksum2)
        assertNotNull(checksum1)
        assertTrue(checksum1.length > 10)
    }

    @Test
    fun `verify returns true for matching checksum`() = runBlocking {
        val json = "{\"gold\": 100}"
        val checksum = SaveIntegrity.generateChecksum(json)
        
        assertTrue("Verification should succeed", SaveIntegrity.verify(json, checksum))
    }

    @Test
    fun `verify returns false for tampered data`() = runBlocking {
        val json = "{\"gold\": 100}"
        val tampered = "{\"gold\": 999999}"
        val checksum = SaveIntegrity.generateChecksum(json)
        
        assertFalse("Verification should fail for tampered data", SaveIntegrity.verify(tampered, checksum))
    }

    @Test
    fun `checksum handles large session strings`() = runBlocking {
        val largeJson = "A".repeat(10000)
        val checksum = SaveIntegrity.generateChecksum(largeJson)
        assertTrue(SaveIntegrity.verify(largeJson, checksum))
    }

    @Test
    fun `computeStateHash changes on quest ID or reputation change`() {
        val state1 = GameState()
        state1.quest.activeQuestIds.add("quest_a")
        state1.reputation.globalFactions["faction_x"] = 10
        state1.reputation.globalFactions["faction_y"] = -10

        val state2 = GameState()
        state2.quest.activeQuestIds.add("quest_b")
        state2.reputation.globalFactions["faction_x"] = -10
        state2.reputation.globalFactions["faction_y"] = 10

        assertNotEquals(SaveIntegrity.computeStateHash(state1), SaveIntegrity.computeStateHash(state2))
    }
}
