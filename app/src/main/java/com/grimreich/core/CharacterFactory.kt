package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

@Singleton
class CharacterFactory @Inject constructor(
    private val careerChain: CareerChain,
    private val agingSystem: AgingSystem
) {

    fun createHero(name: String, age: Int, career: Career): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = age,
            currentCareer = career
        )
        careerChain.applyCareer(career, hero)
        agingSystem.applyAging(hero.id)
        return hero
    }

    fun createKnight(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 22,
            currentCareer = Career.KNIGHT,
            strength = 14,
            agility = 10,
            perception = 8,
            intelligence = 9,
            endurance = 12,
            charisma = 11,
            piety = 10,
            hp = 30,
            maxHp = 30
        )
        agingSystem.applyAging(hero.id)
        return hero
    }

    fun createScholar(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 30,
            currentCareer = Career.SCHOLAR,
            strength = 8,
            agility = 9,
            perception = 12,
            intelligence = 15,
            endurance = 8,
            charisma = 10,
            piety = 12,
            hp = 20,
            maxHp = 20
        )
        agingSystem.applyAging(hero.id)
        return hero
    }

    fun createMercenary(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 28,
            currentCareer = Career.MERCENARY,
            strength = 13,
            agility = 12,
            perception = 10,
            intelligence = 8,
            endurance = 11,
            charisma = 9,
            piety = 8,
            hp = 28,
            maxHp = 28
        )
        agingSystem.applyAging(hero.id)
        return hero
    }

    fun createMonk(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 25,
            currentCareer = Career.MONK,
            strength = 10,
            agility = 10,
            perception = 10,
            intelligence = 11,
            endurance = 10,
            charisma = 9,
            piety = 15,
            hp = 22,
            maxHp = 22
        )
        agingSystem.applyAging(hero.id)
        return hero
    }

    fun availableTemplates(): List<String> =
        listOf("Rycerz", "Uczony", "Najemnik", "Mnich")

    fun createFromTemplate(template: String, name: String): Hero {
        return when (template) {
            "Rycerz" -> createKnight(name)
            "Uczony" -> createScholar(name)
            "Najemnik" -> createMercenary(name)
            "Mnich" -> createMonk(name)
            else -> createKnight(name)
        }
    }
}
