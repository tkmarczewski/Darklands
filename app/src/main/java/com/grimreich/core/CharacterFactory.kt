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
            age = age - 1, // age will be incremented by agingSystem
            currentCareer = career
        )
        careerChain.applyCareer(career, hero)
        
        // FIX: Aging must be applied manually here as hero is not yet in GameRepository.
        // We use a dummy GameState just for logging/state tracking if needed.
        agingSystem.applyAgingToHero(hero, GameState())
        hero.normalize()
        return hero
    }

    fun createKnight(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 21,
            currentCareer = Career.KNIGHT,
            strength = 14,
            agility = 10,
            perception = 8,
            intelligence = 9,
            endurance = 12,
            charisma = 11,
            piety = 10
        )
        agingSystem.applyAgingToHero(hero, GameState())
        hero.normalize()
        return hero
    }

    fun createScholar(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 29,
            currentCareer = Career.SCHOLAR,
            strength = 8,
            agility = 9,
            perception = 12,
            intelligence = 15,
            endurance = 8,
            charisma = 10,
            piety = 12
        )
        agingSystem.applyAgingToHero(hero, GameState())
        hero.normalize()
        return hero
    }

    fun createMercenary(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 27,
            currentCareer = Career.MERCENARY,
            strength = 13,
            agility = 12,
            perception = 10,
            intelligence = 8,
            endurance = 11,
            charisma = 9,
            piety = 8
        )
        agingSystem.applyAgingToHero(hero, GameState())
        hero.normalize()
        return hero
    }

    fun createMonk(name: String): Hero {
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = 24,
            currentCareer = Career.MONK,
            strength = 10,
            agility = 10,
            perception = 10,
            intelligence = 11,
            endurance = 10,
            charisma = 9,
            piety = 15
        )
        agingSystem.applyAgingToHero(hero, GameState())
        hero.normalize()
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
