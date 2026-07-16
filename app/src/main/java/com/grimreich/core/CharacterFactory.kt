package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

@Singleton
class CharacterFactory @Inject constructor(
    private val gameRepository: GameRepository,
    private val careerChain: CareerChain,
    private val agingSystem: AgingSystem,
    private val echoSystem: EchoSystem
) {

    fun createHero(name: String, age: Int, career: Career, trainingCycles: Int = 0, echoId: String? = null): Hero {
        val state = gameRepository.currentState()
        
        // --- AGE PROTECTION FIX ---
        // Ensure starting age is at least 18 (Life Path Anchor)
        val correctedAge = if (age < 18) 18 else age
        
        val hero = Hero(
            id = "hero_${UUID.randomUUID()}",
            name = name,
            age = correctedAge,
            currentCareer = career
        )
        careerChain.applyCareer(career, hero)
        
        // --- LIFE PATH CYCLES (Darklands style) ---
        repeat(trainingCycles) {
            // Each cycle adds 5 years of service and increases stats/skills
            val entry = hero.careerHistory.find { it.career == career }
            if (entry != null) {
                val updated = entry.copy(yearsServed = entry.yearsServed + 5f)
                val index = hero.careerHistory.indexOf(entry)
                hero.careerHistory[index] = updated
            }
            
            // Skill bonus for training
            hero.skills.entries.forEach { (skill, value) ->
                hero.skills[skill] = value + 10
            }
            
            hero.age += 5
            agingSystem.applyAgingToHero(hero, state)
        }
        
        // --- ECHO INHERITANCE ---
        echoId?.let { id ->
            echoSystem.linkToEcho(hero, id)
        }

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
        agingSystem.applyAgingToHero(hero, gameRepository.currentState())
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
        agingSystem.applyAgingToHero(hero, gameRepository.currentState())
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
        agingSystem.applyAgingToHero(hero, gameRepository.currentState())
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
        agingSystem.applyAgingToHero(hero, gameRepository.currentState())
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
