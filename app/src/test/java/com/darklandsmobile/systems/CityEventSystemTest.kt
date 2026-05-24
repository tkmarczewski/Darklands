package com.darklandsmobile.systems

import com.darklandsmobile.TestSupport
import com.darklandsmobile.core.GameBootstrap
import com.darklandsmobile.world.CityCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CityEventSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoEmpty()
        GameBootstrap.init()
    }

    @Test
    fun `runCityEvent rejects unknown city`() {
        val msg = CityEventSystem.runCityEvent("ghost_city")
        assertTrue(msg.startsWith("Nieznane miasto"))
    }

    @Test
    fun `neutral reputation produces neutral message`() {
        // ReputationSystem klucz miasta = nazwa, ale CityEventSystem korzysta z city.name jako klucza,
        // wiec rep ustawiamy pod kluczem nazwy.
        val msg = CityEventSystem.runCityEvent("magdeburg")
        assertTrue("expected neutral day message, got: $msg", msg.contains("zwyczajny"))
    }

    @Test
    fun `friendly reputation produces friendly message`() {
        ReputationSystem.changeCity("Magdeburg", 60)
        val msg = CityEventSystem.runCityEvent("magdeburg")
        assertTrue(msg.contains("z zaufaniem"))
    }

    @Test
    fun `low reputation produces guarded message`() {
        ReputationSystem.changeCity("Magdeburg", -25)
        val msg = CityEventSystem.runCityEvent("magdeburg")
        assertTrue(msg.contains("podejrzliwie"))
    }

    @Test
    fun `runCityEvent appends to city events list`() {
        val city = CityCatalogue.get("magdeburg")!!
        val before = city.events.size
        CityEventSystem.runCityEvent("magdeburg")
        assertEquals(before + 1, city.events.size)
    }
}
