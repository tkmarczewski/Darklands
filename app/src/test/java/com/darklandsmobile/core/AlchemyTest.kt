package com.darklandsmobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlchemyTest {

    @Test
    fun `brewChance clamps in 5 to 95 range`() {
        assertEquals(5, AlchemyCore.brewChance(alchSkill = -100, intelligence = 0))
        assertEquals(95, AlchemyCore.brewChance(alchSkill = 200, intelligence = 200))
    }

    @Test
    fun `brewChance reflects skill and int`() {
        val low = AlchemyCore.brewChance(alchSkill = 20, intelligence = 10)
        val high = AlchemyCore.brewChance(alchSkill = 60, intelligence = 20)
        assertTrue(high > low)
    }

    @Test
    fun `ingredientCost scales with batch and quality`() {
        val potion = PotionType.NOXIOUS_AROMA
        val low = AlchemyCore.ingredientCost(potion, PotionQuality.LOW, 1)
        val high = AlchemyCore.ingredientCost(potion, PotionQuality.HIGH, 3)
        assertTrue(high > low)
    }

    @Test
    fun `saleValue scales with quality`() {
        val potion = PotionType.GREATPOWER
        val low = AlchemyCore.saleValue(potion, PotionQuality.LOW)
        val high = AlchemyCore.saleValue(potion, PotionQuality.HIGH)
        assertTrue(high > low)
    }
}
