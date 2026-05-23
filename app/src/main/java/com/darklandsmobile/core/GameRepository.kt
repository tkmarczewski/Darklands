package com.darklandsmobile.core

object GameRepository {
    var state = GameState()

    fun seed() {
        state = GameState()
        val hero1 = Hero(
            id = "hero_1", name = "Friedrich", age = 25,
            strength = 14, dex = 10, intelligence = 12,
            endurance = 13, charisma = 9, piety = 8,
            hp = 35, maxHp = 35
        ).also {
            it.skills["sword"]  = 40
            it.skills["riding"] = 20
        }
        val hero2 = Hero(
            id = "hero_2", name = "Hildegard", age = 22,
            strength = 9, dex = 13, intelligence = 16,
            endurance = 10, charisma = 14, piety = 18,
            hp = 25, maxHp = 25
        ).also {
            it.skills["alchemy"] = 55
            it.skills["prayer"]  = 60
        }
        state.party.addAll(listOf(hero1, hero2))
        state.activeHeroId = hero1.id
        state.inventory.addAll(listOf(
            Item("sword_01",  "Zelazny Miecz",    "weapon", "weapon", 50,  2.5, mapOf("attack"  to 8)),
            Item("herb_01",   "Ziele Lecznicze",  "herb",   null,      5,  0.1, mapOf("heal"    to 5)),
            Item("armor_01",  "Skorzana Zbroja",  "armor",  "armor",  80,  5.0, mapOf("defense" to 4)),
            Item("potion_01", "Mikstura Zdrowia", "potion", null,     20,  0.3, mapOf("heal"    to 15))
        ))
        state.logEntries.add("Druzyna rozpoczyna przygode w Magdeburgu.")
    }

    fun log(msg: String) {
        state.logEntries.add(msg)
        if (state.logEntries.size > 100) state.logEntries.removeAt(0)
    }

    fun sync() {}
}
