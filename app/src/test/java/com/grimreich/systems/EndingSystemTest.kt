package com.grimreich.systems

import com.grimreich.TestSupport
import com.grimreich.core.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EndingSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
    }

    @Test
    fun `GOOD ending requires high faith virtue cityRep and low sins`() {
        val s = GameRepository.state
        s.prayer.faith = 70
        s.prayer.virtue = 60
        s.prayer.sins = 0
        s.reputation.city["grimhold"] = 25 // sum >= 20

        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.GOOD, ending.type)
        assertEquals("Oczyszczenie", ending.title)
    }

    @Test
    fun `PRAGMATIC ending fallback when faith and rep good but sins moderate`() {
        val s = GameRepository.state
        s.prayer.faith = 40
        s.prayer.virtue = 30
        s.prayer.sins = 4
        s.reputation.city["grimhold"] = 15

        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.PRAGMATIC, ending.type)
    }

    @Test
    fun `REDEMPTION ending requires many blessings and many sins`() {
        val s = GameRepository.state
        s.prayer.faith = 10
        s.prayer.virtue = 5
        s.prayer.sins = 8
        s.prayer.blessings = 6
        s.reputation.city["grimhold"] = -50

        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.REDEMPTION, ending.type)
    }

    @Test
    fun `CORRUPTED ending as ultimate fallback`() {
        val s = GameRepository.state
        s.prayer.faith = 0
        s.prayer.virtue = 0
        s.prayer.sins = 0
        s.prayer.blessings = 0
        s.reputation.city["grimhold"] = -50

        val ending = EndingSystem.resolveEnding(s)
        assertEquals(EndingType.CORRUPTED, ending.type)
    }

    @Test
    fun `finaleStatus reflects current faith and sins`() {
        val s = GameRepository.state
        s.prayer.faith = 60
        s.prayer.sins = 1
        val status = EndingSystem.finaleStatus()
        assertTrue(status.contains("Oczyszczenie"))
        assertTrue(status.contains("Wiara: 60"))
        assertTrue(status.contains("Grzechy: 1"))
    }

    @Test
    fun `finaleStatus shows Skazenie when sins dominate`() {
        val s = GameRepository.state
        s.prayer.faith = 5
        s.prayer.sins = 8
        val status = EndingSystem.finaleStatus()
        assertTrue(status.contains("Skazenie"))
    }

    @Test
    fun `finaleStatus shows Pielgrzymka when neutral`() {
        val s = GameRepository.state
        s.prayer.faith = 5
        s.prayer.sins = 1
        val status = EndingSystem.finaleStatus()
        assertTrue(status.contains("Pielgrzymka trwa"))
    }
}
