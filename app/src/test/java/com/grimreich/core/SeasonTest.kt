package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Test

class SeasonTest {

    @Test
    fun `displayName returns correct Polish names`() {
        assertEquals("Wiosna", Season.SPRING.displayName())
        assertEquals("Lato", Season.SUMMER.displayName())
        assertEquals("Jesień", Season.AUTUMN.displayName())
        assertEquals("Zima", Season.WINTER.displayName())
    }

    @Test
    fun `travelModifier returns correct values`() {
        assertEquals(1.0f, Season.SPRING.travelModifier(), 0.001f)
        assertEquals(0.9f, Season.SUMMER.travelModifier(), 0.001f)
        assertEquals(1.1f, Season.AUTUMN.travelModifier(), 0.001f)
        assertEquals(1.4f, Season.WINTER.travelModifier(), 0.001f)
    }
}
