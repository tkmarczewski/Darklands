package com.grimreich.systems

import com.grimreich.TestSupport
import com.grimreich.core.GameRepository
import com.grimreich.core.ShrineType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReligionSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
    }

    @Test
    fun `pray with unknown saint returns guard message`() {
        val msg = ReligionSystem.pray("nonexistent", ShrineType.CATHEDRAL)
        assertTrue(msg.startsWith("Nieznany swiety"))
    }

    @Test
    fun `pray cathedral gives 10 favor and faith`() {
        val msg = ReligionSystem.pray("s1", ShrineType.CATHEDRAL)
        val p = GameRepository.state.prayer
        assertEquals(10, p.favor["s1"])
        // faith startuje na 50 z PrayerState
        assertEquals(60, p.faith)
        assertEquals(1, p.blessings)
        assertTrue(msg.contains("+10 laski"))
    }

    @Test
    fun `pray ruins gives only 1 favor`() {
        ReligionSystem.pray("s2", ShrineType.RUINS)
        assertEquals(1, ReligionSystem.favorFor("s2"))
    }

    @Test
    fun `pray accumulates favor across multiple prayers and is capped at 100`() {
        // 100 modlitw w katedrze powinno doprowadzic do limitu 100
        repeat(15) { ReligionSystem.pray("s1", ShrineType.CATHEDRAL) }
        val favor = ReligionSystem.favorFor("s1")
        assertEquals(100, favor)
    }

    @Test
    fun `pray increments blessings counter per call`() {
        ReligionSystem.pray("s1", ShrineType.CHAPEL)
        ReligionSystem.pray("s1", ShrineType.ROADSIDE)
        ReligionSystem.pray("s2", ShrineType.MONASTERY)
        assertEquals(3, GameRepository.state.prayer.blessings)
    }

    @Test
    fun `pray awards expected favor per shrine type`() {
        ReligionSystem.pray("s1", ShrineType.CHAPEL)
        assertEquals(5, ReligionSystem.favorFor("s1"))

        ReligionSystem.pray("s2", ShrineType.MONASTERY)
        assertEquals(4, ReligionSystem.favorFor("s2"))

        ReligionSystem.pray("s3", ShrineType.ROADSIDE)
        assertEquals(2, ReligionSystem.favorFor("s3"))
    }

    @Test
    fun `sin increases sins and decreases virtue by 2x`() {
        val p = GameRepository.state.prayer
        p.virtue = 10

        val msg = ReligionSystem.sin(3)

        assertEquals(3, p.sins)
        assertEquals(4, p.virtue)
        assertTrue(msg.contains("Grzechy: 3"))
        assertTrue(msg.contains("Cnota: 4"))
    }

    @Test
    fun `sin clamps virtue to zero`() {
        val p = GameRepository.state.prayer
        p.virtue = 1
        ReligionSystem.sin(5)
        assertEquals(0, p.virtue)
    }

    @Test
    fun `getSaint returns saint by id`() {
        val saint = ReligionSystem.getSaint("s1")
        assertNotNull(saint)
        assertEquals("Święty Jerzy", saint!!.name)
    }

    @Test
    fun `getSaint unknown returns null`() {
        assertNull(ReligionSystem.getSaint("ghost"))
    }

    @Test
    fun `allSaints returns whole catalogue`() {
        val all = ReligionSystem.allSaints()
        assertTrue(all.size >= 4)
        assertTrue(all.any { it.id == "s1" })
    }

    @Test
    fun `favorFor returns zero when no prayer history`() {
        assertEquals(0, ReligionSystem.favorFor("s4"))
    }
}
