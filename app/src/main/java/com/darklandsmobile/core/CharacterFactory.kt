package com.darklandsmobile.core

import java.util.UUID

object CharacterFactory {

    fun createHero(
        name: String,
        startingAge: Int = 14,
        startingCareer: Career = Career.PAGE
    ): Hero {
        val base = Hero(
            id = UUID.randomUUID().toString(),
            name = name,
            age = startingAge,
            strength = 8,
            agility = 8,
            dex = 8,
            intelligence = 8,
            endurance = 10,
            charisma = 8,
            piety = 5,
            virtue = 2,
            hp = 30,
            maxHp = 30
        )
        return CareerChain.applyCareer(startingCareer, base)
    }

    fun createKnight(name: String): Hero {
        var hero = createHero(name, startingAge = 7, startingCareer = Career.PAGE)
        hero = AgingSystem.applyAging(hero, 7).first
        hero = CareerChain.applyCareer(Career.SQUIRE, hero)
        hero = AgingSystem.applyAging(hero, 7).first
        if (CareerChain.isEligible(Career.KNIGHT, hero)) {
            hero = CareerChain.applyCareer(Career.KNIGHT, hero)
        }
        return hero
    }

    fun createScholar(name: String): Hero {
        var hero = createHero(name, startingAge = 14, startingCareer = Career.SCHOLAR)
        hero = AgingSystem.applyAging(hero, 6).first
        if (CareerChain.isEligible(Career.ALCHEMIST, hero)) {
            hero = CareerChain.applyCareer(Career.ALCHEMIST, hero)
        }
        return hero
    }

    fun createMercenary(name: String): Hero {
        var hero = createHero(name, startingAge = 16, startingCareer = Career.MERCENARY)
        hero = AgingSystem.applyAging(hero, 5).first
        return hero
    }

    fun createMonk(name: String): Hero {
        var hero = createHero(name, startingAge = 16, startingCareer = Career.MONK)
        hero = AgingSystem.applyAging(hero, 5).first
        return hero
    }

    fun availableTemplates(): List<String> = listOf(
        "Rycerz", "Uczony", "Najemnik", "Mnich"
    )

    fun createFromTemplate(name: String, template: String): Hero = when (template) {
        "Rycerz" -> createKnight(name)
        "Uczony" -> createScholar(name)
        "Najemnik" -> createMercenary(name)
        "Mnich" -> createMonk(name)
        else -> createHero(name)
    }
}
