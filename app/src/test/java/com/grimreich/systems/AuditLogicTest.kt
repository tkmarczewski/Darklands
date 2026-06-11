package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.world.CityCatalogue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditLogicTest {

    @BeforeEach
    fun setUp() {
        GameRepository.state = GameState()
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
    }

    @Test
    fun `CityCatalogue contains all 7 canonical regions with backgrounds`() {
        val all = CityCatalogue.all()
        assertEquals(7, all.size)
        
        val regions = listOf(
            "wybrzeze_polnocne", "rowniny_koronne", "serce_krainy",
            "poludniowe_ruiny", "gory_poludniowe", "pogranicze_stepowe", "ziemie_dzikie"
        )
        
        regions.forEach { id ->
            val city = CityCatalogue.get(id)
            assertNotNull(city, "City $id should exist")
            assertTrue(city?.backgroundDrawable?.startsWith("bg_") == true, "City $id should have a bg assigned")
        }
    }

    @Test
    fun `DialogueManager maps roles to canonical portraits correctly`() {
        assertEquals("port_orc", DialogueManager.getPortrait("Ork"))
        assertEquals("port_alchemist", DialogueManager.getPortrait("Alchemik"))
        assertEquals("port_priest", DialogueManager.getPortrait("Kaplan"))
        assertEquals("port_rogue", DialogueManager.getPortrait("UnknownRole"))
    }

    @Test
    fun `DialogueManager applies world stability effects to text`() {
        DialogueManager.seedBasicDialogues()
        GameRepository.state.world.globalStability = 20
        
        val node = DialogueManager.getNode("merchant_start")
        assertNotNull(node)
        assertTrue(node!!.text.contains("GŁOSY"), "Text should contain voices at low stability")
    }

    @Test
    fun `SocialEventSystem returns descriptive audience messages for each city`() {
        val msg = SocialEventSystem.cityAudience("wybrzeze_polnocne", "Aelion")
        assertTrue(msg.contains("Aelion"), "Prophet name should be present")
        assertTrue(msg.contains("Mgła"), "Phenomenon should be present")
    }

    @Test
    fun `EconomySystem calculates prices based on city modifier and reputation`() {
        // Heartland (serce_krainy) has priceModifier = 1.2
        val basePrice = 100
        val finalPrice = EconomySystem.priceInCity("serce_krainy", basePrice)
        assertEquals(120, finalPrice)
    }

    @Test
    fun `EconomySystem caps price at 1 if base price is positive`() {
        val price = EconomySystem.priceInCity("ziemie_dzikie", 1)
        assertTrue(price >= 1)
    }
}
