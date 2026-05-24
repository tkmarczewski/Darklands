package com.darklandsmobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerChainTest {

    private fun newHero(
        age: Int = 25,
        str: Int = 10,
        agi: Int = 10,
        int_: Int = 10,
        virtue: Int = 5,
    ): Hero = Hero(
        id = "h", name = "Tester", age = age,
        strength = str, agility = agi, intelligence = int_,
        endurance = 10, charisma = 10, piety = 10,
        virtue = virtue
    )

    @Test
    fun `KNIGHT requires age and stats above thresholds`() {
        val ok = newHero(age = 25, str = 8, agi = 6, int_ = 4, virtue = 6)
        assertTrue(CareerChain.isEligible(Career.KNIGHT, ok))

        val tooYoung = newHero(age = 18, str = 8, agi = 6, int_ = 4, virtue = 6)
        assertFalse(CareerChain.isEligible(Career.KNIGHT, tooYoung))

        val tooWeak = newHero(age = 25, str = 4, agi = 6, int_ = 4, virtue = 6)
        assertFalse(CareerChain.isEligible(Career.KNIGHT, tooWeak))
    }

    @Test
    fun `availableCareers filters by hero stats`() {
        val child = newHero(age = 10, str = 1, agi = 1, int_ = 1, virtue = 0)
        val pageEligible = CareerChain.availableCareers(child)
        assertTrue(pageEligible.contains(Career.PAGE))
        assertFalse(pageEligible.contains(Career.KNIGHT))

        val scholar = newHero(age = 30, str = 0, agi = 1, int_ = 6, virtue = 2)
        val list = CareerChain.availableCareers(scholar)
        assertTrue(list.contains(Career.SCHOLAR))
        assertTrue(list.contains(Career.ALCHEMIST))
    }

    @Test
    fun `applyCareer adds stat bonuses and appends history entry`() {
        val hero = newHero(age = 25, str = 8, agi = 6, int_ = 4, virtue = 6)
        val after = CareerChain.applyCareer(Career.KNIGHT, hero)

        assertEquals(Career.KNIGHT, after.currentCareer)
        assertEquals(hero.strength + Career.KNIGHT.strBonus, after.strength)
        assertEquals(hero.agility + Career.KNIGHT.agiBonus, after.agility)
        assertEquals(hero.virtue + Career.KNIGHT.virtueBonus, after.virtue)
        assertEquals(1, after.careerHistory.size)
        assertEquals(Career.KNIGHT, after.careerHistory.first().career)
    }
}
