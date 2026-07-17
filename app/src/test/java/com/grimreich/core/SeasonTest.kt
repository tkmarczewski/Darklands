package com.grimreich.core

import org.junit.Assert.assertEquals
import org.junit.Test

class SeasonTest {

    @Test
    fun `displayName returns correct Polish names`() {
        assertEquals("Wiosna", Season.spring.displayName())
        assertEquals("Lato", Season.summer.displayName())
        assertEquals("Jesień", Season.autumn.displayName())
        assertEquals("Zima", Season.winter.displayName())
    }

    @Test
    fun `travelModifier returns correct values`() {
        assertEquals(1.0f, Season.spring.travelModifier(), 0.001f)
        assertEquals(0.9f, Season.summer.travelModifier(), 0.001f)
        assertEquals(1.1f, Season.autumn.travelModifier(), 0.001f)
        assertEquals(1.4f, Season.winter.travelModifier(), 0.001f)
    }
}
