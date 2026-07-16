package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

enum class Career(
    val displayName: String,
    val minAge: Int,
    val maxAge: Int,
    val requiredVirtue: Int = 0,
    val requiredStrength: Int = 0,
    val requiredAgility: Int = 0,
    val requiredIntelligence: Int = 0,
    val strBonus: Int = 0,
    val agiBonus: Int = 0,
    val intBonus: Int = 0,
    val virtueBonus: Int = 0,
    val description: String = ""
) {
    PAGE("Paż", 7, 14, strBonus = 1, agiBonus = 1,
        description = "Młody sługa uczący się podstaw rycerskiego rzemiosła."),
    SQUIRE("Giermek", 14, 21, requiredStrength = 10, strBonus = 2, agiBonus = 1,
        description = "Pomocnik rycerza, szkolący się w walce i etykiecie."),
    KNIGHT("Rycerz", 21, 60, requiredVirtue = 30, requiredStrength = 12, strBonus = 3, virtueBonus = 5,
        description = "Zakonny wojownik, obrońca wiary i tradycji."),
    MERCENARY("Najemnik", 16, 60, requiredStrength = 11, strBonus = 2, agiBonus = 2,
        description = "Wojownik do wynajęcia, znający realia wojny."),
    SCHOLAR("Uczony", 14, 80, requiredIntelligence = 12, intBonus = 4,
        description = "Poszukiwacz wiedzy, badający starożytne pisma."),
    MONK("Mnich", 14, 80, requiredVirtue = 20, virtueBonus = 5, intBonus = 1,
        description = "Sługa kościoła, oddany modlitwie i kontemplacji."),
    // FIX BUG-11: Corrected typo 'Żodziej' -> 'Złodziej'
    THIEF("Złodziej", 12, 50, requiredAgility = 12, agiBonus = 3,
        description = "Cień miejskich zaułków, mistrz manipulacji."),
    ALCHEMIST("Alchemik", 18, 70, requiredIntelligence = 14, intBonus = 3,
        description = "Mistrz eliksirów i przemian materii."),
    CRAFTSMAN("Rzemieślnik", 14, 70, requiredStrength = 10, strBonus = 2,
        description = "Twórca przedmiotów, znający się na metalurgii."),
    MERCHANT("Kupiec", 16, 75, requiredIntelligence = 10,
        description = "Handlarz, znający wartość towarów i ludzi."),
    GUARD("Strażnik", 16, 60, requiredStrength = 10, strBonus = 1, agiBonus = 1,
        description = "Obrońca porządku miejskiego."),
    PRIEST("Kapłan", 21, 80, requiredVirtue = 40, virtueBonus = 10, intBonus = 2,
        description = "Ustanowiony sługa wiary, prowadzący wiernych."),
    PHYSICIAN("Cyrulik", 18, 70, requiredIntelligence = 11, intBonus = 2,
        description = "Uzdrowiciel, znający anatomię i zioła."),
    APPRENTICE("Uczeń", 7, 18, intBonus = 1,
        description = "Młody adept sztuki lub rzemiosła."),
    INQUISITOR("Inkwizytor", 25, 60, requiredVirtue = 50, requiredIntelligence = 12, virtueBonus = 15,
        description = "Łowca heretyków i tępiciel mroku."),
    ROGUE("Łotr", 14, 50, requiredAgility = 11, agiBonus = 2,
        description = "Awanturnik żyjący na krawędzi prawa.")
}

data class CareerEntry(
    val career: Career,
    val yearsServed: Float
)

@Singleton
class CareerChain @Inject constructor() {
    companion object {
        private const val STAT_CAP   = 99
        private const val VIRTUE_CAP = 100
    }

    fun isEligible(career: Career, hero: Hero): Boolean {
        return hero.age in career.minAge..career.maxAge &&
            hero.virtue       >= career.requiredVirtue &&
            hero.strength     >= career.requiredStrength &&
            hero.agility      >= career.requiredAgility &&
            hero.intelligence >= career.requiredIntelligence
    }

    fun availableCareers(hero: Hero): List<Career> = Career.values().filter { isEligible(it, hero) }

    fun applyCareer(career: Career, hero: Hero): Hero {
        hero.currentCareer  = career
        hero.strength       = (hero.strength    + career.strBonus).coerceIn(0, STAT_CAP)
        hero.agility        = (hero.agility     + career.agiBonus).coerceIn(0, STAT_CAP)
        hero.intelligence   = (hero.intelligence + career.intBonus).coerceIn(0, STAT_CAP)
        hero.virtue         = (hero.virtue      + career.virtueBonus).coerceIn(0, VIRTUE_CAP)
        
        // Ensure career exists in history, starting with 0 years if new
        if (hero.careerHistory.none { it.career == career }) {
            hero.careerHistory.add(CareerEntry(career = career, yearsServed = 0f))
        }
        return hero
    }
}

