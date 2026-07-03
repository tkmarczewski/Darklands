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
}
