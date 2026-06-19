package com.grimreich.core

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterFactory @Inject constructor(
    private val careerChain: CareerChain,
    private val agingSystem: AgingSystem
) {

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
            perception = 8,
            intelligence = 8,
            endurance = 10,
            charisma = 8,
            piety = 5,
            virtue = 2,
            hp = 30,
            maxHp = 30
        )
        return careerChain.applyCareer(startingCareer, base)
    }

    fun createKnight(name: String): Hero {
        var hero = createHero(name, startingAge = 7, startingCareer = Career.PAGE)
        // Manual aging logic as placeholder or refactored call
        hero.age += 7
        agingSystem.applyAging(hero)
        hero = careerChain.applyCareer(Career.SQUIRE, hero)
        hero.age += 7
        agingSystem.applyAging(hero)
        if (careerChain.isEligible(Career.KNIGHT, hero)) {
            hero = careerChain.applyCareer(Career.KNIGHT, hero)
        }
        return hero
    }

    fun createScholar(name: String): Hero {
        var hero = createHero(name, startingAge = 14, startingCareer = Career.SCHOLAR)
        hero.age += 6
        agingSystem.applyAging(hero)
        if (careerChain.isEligible(Career.ALCHEMIST, hero)) {
            hero = careerChain.applyCareer(Career.ALCHEMIST, hero)
        }
        return hero
    }

    fun createMercenary(name: String): Hero {
        var hero = createHero(name, startingAge = 16, startingCareer = Career.MERCENARY)
        hero.age += 5
        agingSystem.applyAging(hero)
        return hero
    }

    fun createMonk(name: String): Hero {
        var hero = createHero(name, startingAge = 16, startingCareer = Career.MONK)
        hero.age += 5
        agingSystem.applyAging(hero)
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
