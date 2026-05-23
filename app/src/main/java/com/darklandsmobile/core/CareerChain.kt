package com.darklandsmobile.core

enum class Career(
    val displayName: String,
    val minAge: Int,
    val maxAge: Int,
    val requiredVirtue: Int,
    val requiredStrength: Int,
    val requiredAgility: Int,
    val requiredIntelligence: Int,
    val strBonus: Int = 0,
    val agiBonus: Int = 0,
    val intBonus: Int = 0,
    val virtueBonus: Int = 0,
    val description: String = ""
) {
    PAGE(
        displayName = "Paź",
        minAge = 7, maxAge = 14,
        requiredVirtue = 0,
        requiredStrength = 0, requiredAgility = 0, requiredIntelligence = 0,
        agiBonus = 1, virtueBonus = 1,
        description = "Służba na dworze. Podstawy etykiety i walki."
    ),
    SQUIRE(
        displayName = "Giermek",
        minAge = 14, maxAge = 21,
        requiredVirtue = 2,
        requiredStrength = 3, requiredAgility = 2, requiredIntelligence = 1,
        strBonus = 2, agiBonus = 1,
        description = "Szkolenie rycerskie. Walka, jazda konna, honor."
    ),
    KNIGHT(
        displayName = "Rycerz",
        minAge = 21, maxAge = 50,
        requiredVirtue = 5,
        requiredStrength = 6, requiredAgility = 4, requiredIntelligence = 2,
        strBonus = 3, agiBonus = 1, virtueBonus = 2,
        description = "Pełny rycerz. Kodeks honoru, walka w zbroi."
    ),
    MERCENARY(
        displayName = "Najemnik",
        minAge = 16, maxAge = 45,
        requiredVirtue = 0,
        requiredStrength = 4, requiredAgility = 3, requiredIntelligence = 0,
        strBonus = 2, agiBonus = 2,
        description = "Walka za pieniądze. Bez honoru, z doświadczeniem."
    ),
    SCHOLAR(
        displayName = "Uczony",
        minAge = 14, maxAge = 60,
        requiredVirtue = 1,
        requiredStrength = 0, requiredAgility = 0, requiredIntelligence = 4,
        intBonus = 3, virtueBonus = 1,
        description = "Nauka, alchemia, teologia. Słaby fizycznie, silny umysłem."
    ),
    MONK(
        displayName = "Mnich",
        minAge = 16, maxAge = 60,
        requiredVirtue = 4,
        requiredStrength = 0, requiredAgility = 0, requiredIntelligence = 3,
        intBonus = 2, virtueBonus = 3,
        description = "Życie klasztorne. Modlitwa, wiedza, asceza."
    ),
    THIEF(
        displayName = "Złodziej",
        minAge = 12, maxAge = 40,
        requiredVirtue = 0,
        requiredStrength = 1, requiredAgility = 5, requiredIntelligence = 2,
        agiBonus = 3, intBonus = 1,
        description = "Życie poza prawem. Zwinność i spryt kosztem honoru."
    ),
    ALCHEMIST(
        displayName = "Alchemik",
        minAge = 20, maxAge = 60,
        requiredVirtue = 1,
        requiredStrength = 0, requiredAgility = 1, requiredIntelligence = 5,
        intBonus = 3,
        description = "Nauka tajemna. Mikstury, transmutacje, eksperymenty."
    )
}

data class CareerEntry(
    val career: Career,
    val yearsServed: Int
)

object CareerChain {
    fun isEligible(career: Career, hero: Hero): Boolean {
        return hero.age >= career.minAge &&
            hero.age <= career.maxAge &&
            hero.virtue >= career.requiredVirtue &&
            hero.strength >= career.requiredStrength &&
            hero.agility >= career.requiredAgility &&
            hero.intelligence >= career.requiredIntelligence
    }

    fun availableCareers(hero: Hero): List<Career> =
        Career.values().filter { isEligible(it, hero) }

    fun applyCareer(career: Career, hero: Hero): Hero {
        return hero.copy(
            currentCareer = career,
            strength = hero.strength + career.strBonus,
            agility = hero.agility + career.agiBonus,
            intelligence = hero.intelligence + career.intBonus,
            virtue = hero.virtue + career.virtueBonus,
            careerHistory = hero.careerHistory + CareerEntry(career, 0)
        )
    }
}
