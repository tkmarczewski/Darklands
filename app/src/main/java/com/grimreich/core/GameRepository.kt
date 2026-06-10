package com.grimreich.core

import com.grimreich.grimreich.v1.Item
import com.grimreich.world.CityCatalogue

object GameRepository {
    var state = GameState()

    fun seed() {
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
        val hero2 = Hero(
            id = "hero_2", name = "Hildegard", age = 22,
            strength = 9, agility = 13, intelligence = 16,
            endurance = 10, charisma = 14, piety = 18,
            hp = 25, maxHp = 25
        ).also {
            it.skills["alchemy"] = 55
            it.skills["prayer"]  = 60
        }
        state.party.addAll(listOf(hero1, hero2))
        state.activeHeroId = hero1.id
        
        CityCatalogue.seedCanonical()
        state.world.location = "wybrzeze_polnocne"
        
        // Initial Hireable Heroes
        state.hireableHeroes.addAll(listOf(
            Hero(id = "rec_borg", name = "Borg Ironfoot", age = 34, strength = 15, endurance = 14, hp = 30, maxHp = 30),
            Hero(id = "rec_elara", name = "Elara Shadow", age = 22, agility = 16, perception = 15, hp = 20, maxHp = 20),
            Hero(id = "rec_silas", name = "Father Silas", age = 50, piety = 18, intelligence = 14, hp = 25, maxHp = 25)
        ))

        state.inventory.addAll(listOf(
            Item("sword_01",  "Zelazny Miecz",    "weapon", "weapon", 50,  2.5, effects = mapOf("attack"  to 8)),
            Item("herb_01",   "Ziele Lecznicze",  "herb",   null,      5,  0.1, effects = mapOf("heal"    to 5)),
            Item("armor_01",  "Skorzana Zbroja",  "armor",  "armor",  80,  5.0, effects = mapOf("defense" to 4)),
            Item("potion_01", "Mikstura Zdrowia", "potion", null,     20,  0.3, effects = mapOf("heal"    to 15))
        ))
        state.logEntries.add("Druzyna rozpoczyna przygode w Grimholdu.")
    }

    fun log(msg: String) {
        state.logEntries.add(msg)
        if (state.logEntries.size > 100) state.logEntries.removeAt(0)
    }

    fun sync() {}
}
