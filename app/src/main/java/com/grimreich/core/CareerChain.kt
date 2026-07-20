package com.grimreich.core

import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
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
    page("Paż", 18, 22, strBonus = 1, agiBonus = 1,
        description = "Młody sługa uczący się podstaw rycerskiego rzemiosła."),
    squire("Giermek", 18, 25, requiredStrength = 10, strBonus = 2, agiBonus = 1,
        description = "Pomocnik rycerza, szkolący się w walce i etykiecie."),
    knight("Rycerz", 21, 60, requiredVirtue = 30, requiredStrength = 12, strBonus = 3, virtueBonus = 5,
        description = "Zakonny wojownik, obrońca wiary i tradycji."),
    mercenary("Najemnik", 18, 60, requiredStrength = 11, strBonus = 2, agiBonus = 2,
        description = "Wojownik do wynajęcia, znający realia wojny."),
    scholar("Uczony", 18, 80, requiredIntelligence = 12, intBonus = 4,
        description = "Poszukiwacz wiedzy, badający starożytne pisma."),
    monk("Mnich", 18, 80, requiredVirtue = 20, virtueBonus = 5, intBonus = 1,
        description = "Sługa kościoła, oddany modlitwie i kontemplacji."),
    thief("Złodziej", 18, 50, requiredAgility = 12, agiBonus = 3,
        description = "Cień miejskich zaułków, mistrz manipulacji."),
    alchemist("Alchemik", 18, 70, requiredIntelligence = 14, intBonus = 3,
        description = "Mistrz eliksirów i przemian materii."),
    craftsman("Rzemieślnik", 18, 70, requiredStrength = 10, strBonus = 2,
        description = "Twórca przedmiotów, znający się na metalurgii."),
    merchant("Kupiec", 18, 75, requiredIntelligence = 10,
        description = "Handlarz, znający wartość towarów i ludzi."),
    guard("Strażnik", 18, 60, requiredStrength = 10, strBonus = 1, agiBonus = 1,
        description = "Obrońca porządku miejskiego."),
    priest("Kapłan", 21, 80, requiredVirtue = 40, virtueBonus = 10, intBonus = 2,
        description = "Ustanowiony sługa wiary, prowadzący wiernych."),
    physician("Cyrulik", 18, 70, requiredIntelligence = 11, intBonus = 2,
        description = "Uzdrowiciel, znający anatomię i zioła."),
    apprentice("Uczeń", 18, 25, intBonus = 1,
        description = "Młody adept sztuki lub rzemiosła."),
    inquisitor("Inkwizytor", 25, 60, requiredVirtue = 50, requiredIntelligence = 12, virtueBonus = 15,
        description = "Łowca heretyków i tępiciel mroku."),
    rogue("Łotr", 18, 50, requiredAgility = 11, agiBonus = 2,
        description = "Awanturnik żyjący na krawędzi prawa.");

    companion object {
        @JvmField val PAGE = page; @JvmField val SQUIRE = squire; @JvmField val KNIGHT = knight
        @JvmField val MERCENARY = mercenary; @JvmField val SCHOLAR = scholar; @JvmField val MONK = monk
        @JvmField val THIEF = thief; @JvmField val ALCHEMIST = alchemist; @JvmField val CRAFTSMAN = craftsman
        @JvmField val MERCHANT = merchant; @JvmField val GUARD = guard; @JvmField val PRIEST = priest
        @JvmField val PHYSICIAN = physician; @JvmField val APPRENTICE = apprentice; @JvmField val INQUISITOR = inquisitor
        @JvmField val ROGUE = rogue
        
        // Added for UI matching
        @JvmField val ALCH = alchemist
    }
}

@Serializable
data class CareerEntry(
    val career: Career,
    val daysServed: Int = 0,
    val levelReached: Int = 1,
    val dateReached: Long = 0L
) {
    val yearsServed: Float get() = daysServed / 365f
}

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
        
        if (hero.careerHistory.none { it.career == career }) {
            hero.careerHistory.add(CareerEntry(career = career, daysServed = 0))
        }
        return hero
    }
}
