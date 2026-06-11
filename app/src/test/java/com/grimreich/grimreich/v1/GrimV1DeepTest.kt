package com.grimreich.grimreich.v1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GrimV1DeepTest {

    @Test
    fun `GrimBuilders can construct a basic npc`() {
        val npc = GrimBuilders.npc(
            id = "Heinrich",
            name = "Warrior",
            role = "Guardian"
        )
        assertEquals("Warrior", npc.name)
        assertEquals("Guardian", npc.role)
    }

    @Test
    fun `GrimRegionCatalogue contains regions`() {
        val regions = GrimRegionCatalogue.all
        assertTrue(regions.isNotEmpty())
        assertTrue(GrimRegionCatalogue.allRegions.contains("Wybrzeże Północne"))
    }

    @Test
    fun `GrimBuilders constructs default components`() {
        assertNotNull(GrimBuilders.northCoastConsciousness())
        assertNotNull(GrimBuilders.northCoastTime())
    }
}
