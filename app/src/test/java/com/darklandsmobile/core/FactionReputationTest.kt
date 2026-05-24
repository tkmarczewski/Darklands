package com.darklandsmobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FactionReputationTest {

    @Test
    fun `catalogue contains expected factions`() {
        assertNotNull(FactionCatalogue.findById("church"))
        assertNotNull(FactionCatalogue.findById("nobility"))
        assertNotNull(FactionCatalogue.findById("merchants"))
        assertNull(FactionCatalogue.findById("ghost"))
    }

    @Test
    fun `system initializes all factions at 0`() {
        val sys = FactionReputationSystem()
        FactionCatalogue.factions.forEach {
            assertEquals(0, sys.getReputation(it.id))
        }
    }

    @Test
    fun `changeReputation clamps to plus minus 20`() {
        val sys = FactionReputationSystem()
        sys.changeReputation("church", 100)
        assertEquals(20, sys.getReputation("church"))
        sys.changeReputation("church", -100)
        assertEquals(-20, sys.getReputation("church"))
    }

    @Test
    fun `changeReputation reports diff arrow`() {
        val sys = FactionReputationSystem()
        val out = sys.changeReputation("church", 5)
        assertTrue(out.contains("0 → 5"))
        assertTrue(out.contains("+5"))
    }

    @Test
    fun `reputationLabel buckets are stable`() {
        val sys = FactionReputationSystem()
        assertEquals("Wielki Sojusznik", sys.reputationLabel(20))
        assertEquals("Przyjaciel", sys.reputationLabel(10))
        assertEquals("Znany", sys.reputationLabel(4))
        assertEquals("Neutralny", sys.reputationLabel(0))
        assertEquals("Podejrzany", sys.reputationLabel(-3))
        assertEquals("Wróg", sys.reputationLabel(-9))
        assertEquals("Klątwa Frakcji", sys.reputationLabel(-15))
    }

    @Test
    fun `tradeModifier decreases price as rep rises`() {
        val sys = FactionReputationSystem()
        sys.changeReputation("merchants", 10)
        // 1.0 - 0.02 * 10 = 0.8
        assertEquals(0.8f, sys.tradeModifier("merchants"), 0.0001f)
    }

    @Test
    fun `summary lists every faction line`() {
        val sys = FactionReputationSystem()
        val text = sys.summary()
        FactionCatalogue.factions.forEach {
            assertTrue("expected ${it.name} in summary", text.contains(it.name))
        }
    }
}
