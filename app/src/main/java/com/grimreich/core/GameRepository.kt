package com.grimreich.core

import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.DialogueManager

object GameRepository {
    var state = GameState()

    fun seed() {
        // FORCED RESET: Ensure no stale data persists across sessions
        state = GameState()
        
        val hero1 = Hero(
            id = "hero_1", name = "Friedrich", age = 25,
            strength = 14, agility = 10, intelligence = 12,
            endurance = 13, charisma = 9, piety = 8,
            hp = 35, maxHp = 35
        ).also {
            it.skills["sword"]  = 40
            it.skills["riding"] = 20
        }
        
        state.party.add(hero1)
        state.activeHeroId = hero1.id
        state.gold = 100
        
        // ENSURE LORE DATA
        CityCatalogue.clear()
        CityCatalogue.seedCanonical()
        
        // NORMALIZE STARTING LOCATION (ID MATCHING)
        state.grimCurrentRegion = "wybrzeze_polnocne"
        state.world.location = "wybrzeze_polnocne"
        
        // INITIALIZE SYSTEMS
        QuestSystem.clear()
        QuestSystem.seedIntegratedContent(seed = 1)
        DialogueManager.seedBasicDialogues()
        
        // Initial Hireable Heroes
        state.hireableHeroes.addAll(listOf(
            Hero(id = "rec_borg", name = "Borg Ironfoot", age = 34, strength = 15, endurance = 14, hp = 30, maxHp = 30),
            Hero(id = "rec_elara", name = "Elara Shadow", age = 22, agility = 16, perception = 15, hp = 20, maxHp = 20)
        ))

        state.inventory.add(Item("sword_01", "Żelazny Miecz", "weapon", "weapon", 50, 2.5, effects = mapOf("attack" to 8)))
        state.logEntries.add("Początek nowej ery w Grimreich.")
    }

    fun log(msg: String) {
        state.logEntries.add(msg)
        if (state.logEntries.size > 100) state.logEntries.removeAt(0)
    }

    fun sync() {}
}
