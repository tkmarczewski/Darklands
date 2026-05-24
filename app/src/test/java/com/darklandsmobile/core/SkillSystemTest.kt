package com.darklandsmobile.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillSystemTest {

    private fun newHero(str: Int = 10, end_: Int = 10) = Hero(
        id = "h", name = "T", age = 25,
        strength = str, agility = 10, intelligence = 10,
        endurance = end_, charisma = 10, piety = 10
    )

    @Test
    fun `defaultSkills covers all HeroSkill names`() {
        val map = SkillSystem.defaultSkills()
        HeroSkill.values().forEach {
            assertTrue("missing default for ${it.name}", map.containsKey(it.name))
            assertEquals(5, map[it.name])
        }
    }

    @Test
    fun `encumbranceLevel reflects weight vs capacity`() {
        val hero = newHero(str = 10, end_ = 10) // cap 20
        assertEquals(EncumbranceLevel.LIGHT, SkillSystem.encumbranceLevel(hero, 5))
        assertEquals(EncumbranceLevel.NORMAL, SkillSystem.encumbranceLevel(hero, 15))
        assertEquals(EncumbranceLevel.HEAVY, SkillSystem.encumbranceLevel(hero, 25))
        assertEquals(EncumbranceLevel.OVERLOAD, SkillSystem.encumbranceLevel(hero, 50))
    }

    @Test
    fun `effectiveAgility drops with heavier load`() {
        val hero = newHero(str = 10, end_ = 10)
        val light = SkillSystem.effectiveAgility(hero, 5)
        val heavy = SkillSystem.effectiveAgility(hero, 25)
        val overload = SkillSystem.effectiveAgility(hero, 60)
        assertTrue(light > heavy)
        // overload daje 0% agi -> coerce do 1
        assertEquals(1, overload)
    }

    @Test
    fun `practiceSkill returns false at 100`() {
        val hero = newHero()
        hero.skills[HeroSkill.ALCH.name] = 100
        assertEquals(false, SkillSystem.practiceSkill(hero, HeroSkill.ALCH, true))
    }
}
