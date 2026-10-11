package com.grimreich.core

import com.grimreich.grimreich.v1.Item
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CoreCoverageTest {

    @Test
    fun testHeroStatsAndNormalization() {
        val hero = Hero(
            id = "h1",
            name = "Gideon",
            age = 25,
            strength = 10,
            agility = 12,
            perception = 8,
            intelligence = 14,
            endurance = 15,
            charisma = 10,
            piety = 10,
            hp = 120, // Exceeds maxHp (50)
            maxHp = 50,
            morale = 150 // Exceeds MAX_MORALE
        )

        hero.normalize()
        assertEquals(50, hero.hp)
        assertEquals(GameConstants.Combat.MAX_MORALE, hero.morale)

        hero.hp = -10
        hero.normalize()
        assertEquals(0, hero.hp)
        assertTrue(hero.isDead)

        hero.hp = 25
        hero.normalize()
        assertFalse(hero.isDead)
    }

    @Test
    fun testHeroEffectiveStatsWithEquipment() {
        val hero = Hero(id = "h1", name = "GearedHero", strength = 10, agility = 10)
        hero.equipment["weapon"] = "item_sword"
        hero.equipment["armor"] = "item_armor"

        val inventory = mutableListOf(
            Item("i1", "item_sword", "Miecz", "weapon", "weapon", 20, 2.0, "Common", "", emptyMap(), mapOf("attack" to 5)),
            Item("i2", "item_armor", "Pancerz", "armor", "armor", 50, 5.0, "Common", "", emptyMap(), mapOf("armor" to 8))
        )

        val atk = hero.effectiveAttack(inventory)
        val def = hero.effectiveArmor(inventory)

        // 5 (base) + 5 (strength / 2) + 5 (weapon) = 15
        assertEquals(15, atk)
        assertEquals(8, def)
    }

    @Test
    fun testAgingSystemStatPenalties() {
        val mockRepo = mock<GameRepository>()
        val aging = AgingSystem(mockRepo)
        val state = GameState()

        val youngHero = Hero(id = "h1", name = "Young", age = 20, strength = 10, agility = 10)
        aging.applyAgingToHero(youngHero, state)
        assertEquals(21, youngHero.age)
        assertEquals(10, youngHero.strength)

        val middleAgedHero = Hero(id = "h2", name = "Middle", age = 45, strength = 10, agility = 10)
        aging.applyAgingToHero(middleAgedHero, state)
        assertEquals(46, middleAgedHero.age)
        assertEquals(9, middleAgedHero.agility)

        val elderHero = Hero(id = "h3", name = "Elder", age = 65, strength = 10, agility = 10)
        aging.applyAgingToHero(elderHero, state)
        assertEquals(66, elderHero.age)
        assertEquals(9, elderHero.strength)
    }

    @Test
    fun testMoraleSystemCalculations() {
        val morale = MoraleSystem()

        val afterKill = morale.moraleAfterKill(80)
        assertTrue(afterKill > 80)

        val afterHit = morale.moraleAfterHit(80, 20)
        assertTrue(afterHit < 80)

        val statusHigh = morale.computeStatus(90)
        assertEquals(MoraleStatus.heroic, statusHigh)

        val statusLow = morale.computeStatus(10)
        assertEquals(MoraleStatus.routed, statusLow)
    }

    @Test
    fun testTraitMatchingAndUpperCasing() {
        val ironHeart = Trait.valueOf("IRON_HEART")
        assertEquals(Trait.IRON_HEART, ironHeart)

        val hero = Hero(id = "h1", name = "TraitHero", maxHp = 50, trait = Trait.IRON_HEART)
        applyTraitModifiers(hero)
        assertEquals(55, hero.maxHp)
    }

    @Test
    fun testCombatantStateAndPostCombatRecovery() {
        val combatant = CombatantState(
            name = "HeroCombatant",
            hp = 20,
            maxHp = 50,
            endurance = 10,
            morale = 70
        )

        val mockRepo = mock<GameRepository>()
        whenever(mockRepo.currentState()).thenReturn(GameState())

        val round = CombatRound(mockRepo, MoraleSystem(), FixedRandomProvider())
        val recMsg = round.postCombatRecovery(combatant)

        assertTrue(recMsg.contains("Leczenie"))
        assertTrue(combatant.hp > 20)
    }
}
