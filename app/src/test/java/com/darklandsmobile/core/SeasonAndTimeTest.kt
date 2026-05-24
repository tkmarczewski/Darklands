package com.darklandsmobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeasonAndTimeTest {

    @Test
    fun `season displayName translations`() {
        assertEquals("Wiosna", Season.SPRING.displayName())
        assertEquals("Lato", Season.SUMMER.displayName())
        assertEquals("Jesień", Season.AUTUMN.displayName())
        assertEquals("Zima", Season.WINTER.displayName())
    }

    @Test
    fun `winter has highest travel modifier`() {
        val ordered = Season.values().sortedBy { it.travelModifier() }
        // SUMMER 0.9, SPRING 1.0, AUTUMN 1.1, WINTER 1.4
        assertEquals(Season.SUMMER, ordered.first())
        assertEquals(Season.WINTER, ordered.last())
    }

    @Test
    fun `TimeOfDay isNight and isDusk`() {
        assertTrue(TimeOfDay.MIDNIGHT.isNight())
        assertTrue(TimeOfDay.EVENING.isNight())
        assertTrue(TimeOfDay.DEEP_NIGHT.isNight())
        assertTrue(TimeOfDay.DUSK.isDusk())
    }

    @Test
    fun `advanceHours wraps around the clock and counts days`() {
        val state = DayNightState(hour = 22, daysPassed = 0)
        DayNightSystem.advanceHours(state, 5) // 22 + 5 = 27 % 24 = 3
        assertEquals(3, state.hour)

        DayNightSystem.advanceHours(state, 48)
        assertEquals(2, state.daysPassed)
    }

    @Test
    fun `encounterChanceModifier is higher at night`() {
        val day = DayNightSystem.encounterChanceModifier(TimeOfDay.MIDDAY)
        val night = DayNightSystem.encounterChanceModifier(TimeOfDay.MIDNIGHT)
        assertTrue(night > day)
        assertEquals(1.0f, day, 0.0001f)
        assertEquals(1.8f, night, 0.0001f)
    }

    @Test
    fun `fatigueMod is higher at night`() {
        assertTrue(DayNightSystem.fatigueMod(TimeOfDay.MIDNIGHT) > DayNightSystem.fatigueMod(TimeOfDay.MIDDAY))
    }
}
