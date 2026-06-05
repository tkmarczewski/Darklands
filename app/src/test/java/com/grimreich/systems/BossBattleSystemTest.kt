package com.grimreich.systems

import com.grimreich.TestSupport
import com.grimreich.core.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BossBattleSystemTest {

    @Before
    fun setUp() {
        TestSupport.resetRepoSeeded()
    }

    @Test
    fun `startBoss creates fresh state in phase 1 with full hp`() {
        val state = BossBattleSystem.startBoss(GameRepository.state)
        assertEquals(1, state.phase)
        assertEquals(120, state.hp)
        assertEquals(100, state.morale)
        assertEquals(15, state.armor)
        assertTrue(state.statusEffects.isEmpty())
    }

    @Test
    fun `attackBoss without weapon does at least 1 damage`() {
        // hero_1 nie ma niczego zalozonego w seedzie
        val boss = BossBattleSystem.startBoss(GameRepository.state)
        val before = boss.hp
        BossBattleSystem.attackBoss(boss, GameRepository.state)
        assertTrue(boss.hp < before)
    }

    @Test
    fun `attackBoss with equipped sword scales damage with attack bonus`() {
        InventorySystem.equip("hero_1", "sword_01") // +8 atak
        val boss = BossBattleSystem.startBoss(GameRepository.state)

        // ataki: rawAtk = 10 + 8 = 18, armor=15 -> dmg = max(1, 18 - 7) = 11
        BossBattleSystem.attackBoss(boss, GameRepository.state)
        assertEquals(120 - 11, boss.hp)
        assertEquals(95, boss.morale)
    }

    @Test
    fun `attackBoss transitions to phase 2 once hp drops below 72`() {
        InventorySystem.equip("hero_1", "sword_01") // dmg 11 per cios
        val boss = BossBattleSystem.startBoss(GameRepository.state)

        // potrzeba 5 ciosow zeby zejsc z 120 do 65 (<72)
        repeat(5) { BossBattleSystem.attackBoss(boss, GameRepository.state) }

        assertEquals(2, boss.phase)
        assertTrue(boss.statusEffects.contains("enraged"))
        assertEquals(20, boss.armor) // armor +5 w fazie 2
    }

    @Test
    fun `bossTurn deals damage scaling per phase`() {
        val boss = BossBattleSystem.startBoss(GameRepository.state)
        val hero = GameRepository.state.party.first { it.id == "hero_1" }
        val hpBefore = hero.hp

        BossBattleSystem.bossTurn(boss, GameRepository.state) // faza 1: 8 dmg
        assertEquals(hpBefore - 8, hero.hp)
    }

    @Test
    fun `bossTurn damage reduced by hero defense items`() {
        InventorySystem.equip("hero_1", "armor_01") // +4 defense
        val boss = BossBattleSystem.startBoss(GameRepository.state)
        val hero = GameRepository.state.party.first { it.id == "hero_1" }
        val hpBefore = hero.hp

        // baseDmg=8 - defense/2 = 8 - 2 = 6
        BossBattleSystem.bossTurn(boss, GameRepository.state)
        assertEquals(hpBefore - 6, hero.hp)
    }

    @Test
    fun `bossTurn does not push hp below zero`() {
        val boss = BossBattleSystem.startBoss(GameRepository.state)
        val hero = GameRepository.state.party.first { it.id == "hero_1" }
        hero.hp = 2

        BossBattleSystem.bossTurn(boss, GameRepository.state)

        assertEquals(0, hero.hp)
        assertTrue(BossBattleSystem.isPlayerDefeated(GameRepository.state))
    }

    @Test
    fun `isDefeated reflects boss hp`() {
        val boss = BossBattleSystem.startBoss(GameRepository.state)
        assertFalse(BossBattleSystem.isDefeated(boss))
        boss.hp = 0
        assertTrue(BossBattleSystem.isDefeated(boss))
    }
}
