package com.grimreich.core

// 18 umiejetnosci z oryginalnego Grimreich (1992)
enum class HeroSkill(val displayName: String, val group: SkillGroup) {
    // BRON
    W_EDG("Bron sieczna",     SkillGroup.WEAPON),
    W_IMP("Bron obuchowa",    SkillGroup.WEAPON),
    W_FLL("Cep bojowy",     SkillGroup.WEAPON),
    W_POL("Drzewce",          SkillGroup.WEAPON),
    W_THR("Rzucanie",         SkillGroup.WEAPON),
    W_BOW("Luk",              SkillGroup.WEAPON),
    W_MSD("Urzadzenia",       SkillGroup.WEAPON),
    // AKADEMICKIE
    ALCH ("Alchemia",         SkillGroup.ACADEMIC),
    RELG ("Religia",          SkillGroup.ACADEMIC),
    VIRT ("Cnota",            SkillGroup.ACADEMIC),
    SPK_C("Jezyk potoczny",   SkillGroup.ACADEMIC),
    SPK_L("Lacina",           SkillGroup.ACADEMIC),
    RW   ("Czytanie/Pisanie", SkillGroup.ACADEMIC),
    // SURWIWAL
    HEAL ("Leczenie",         SkillGroup.SURVIVAL),
    ARTF ("Rzemioslo",        SkillGroup.SURVIVAL),
    STL_H("Skradanie",        SkillGroup.SURVIVAL),
    STR_W("Ulice",            SkillGroup.SURVIVAL),
    RIDE ("Jezdziectwo",      SkillGroup.SURVIVAL),
    WD_WS("Dzicz",            SkillGroup.SURVIVAL),
    // INTRYGA
    PERS ("Perswazja",        SkillGroup.INTRIGUE),
    INTI ("Zastraszanie",     SkillGroup.INTRIGUE),
    DECP ("Oszustwo",         SkillGroup.INTRIGUE),
    SOCI ("Etykieta",         SkillGroup.INTRIGUE),
    INVN ("Sledztwo",         SkillGroup.INTRIGUE),
    // DUCHOWOŚĆ
    PRAY ("Modlitwa",         SkillGroup.SPIRITUAL),
    MEDI ("Medytacja",        SkillGroup.SPIRITUAL),
    EXOR ("Egzorcyzmy",       SkillGroup.SPIRITUAL),
    // PANCERZ
    L_ARM("Lekki pancerz",    SkillGroup.ARMOR),
    H_ARM("Ciezki pancerz",   SkillGroup.ARMOR),
    SHLD ("Tarcza",           SkillGroup.ARMOR)
}

enum class SkillGroup { WEAPON, ACADEMIC, SURVIVAL, INTRIGUE, SPIRITUAL, ARMOR }

// Encumbrance (obciazenie) wg oryginalnych zasad
enum class EncumbranceLevel(val label: String, val agilityMult: Float, val speedPenalty: Int) {
    LIGHT   ("Lekki",        1.0f,  0),
    NORMAL  ("Normalny",     0.67f, 30),
    HEAVY   ("Ciezki",       0.33f, 120),
    OVERLOAD("Przeciazenie", 0.0f,  120)
}

object SkillSystem {
    // Inicjalizuje mape umiejetnosci dla nowego bohatera
    fun defaultSkills(): MutableMap<String, Int> =
        HeroSkill.values().associate { it.name to 5 }.toMutableMap()

    // Learn-by-doing: zwraca true jesli nastapil awans
    fun practiceSkill(hero: Hero, skill: HeroSkill, successRoll: Boolean): Boolean {
        val current = hero.skills[skill.name] ?: 0
        if (current >= 100) return false
        // Szansa awansu spada wraz z poziomem
        val chance = maxOf(1, 30 - (current / 5))
        if (successRoll && (1..100).random() <= chance) {
            hero.skills[skill.name] = current + 1
            return true
        }
        return false
    }

    fun getSkill(hero: Hero, skill: HeroSkill): Int =
        hero.skills[skill.name] ?: 0

    // Encumbrance wg Str+End
    fun encumbranceLevel(hero: Hero, carriedWeight: Int): EncumbranceLevel {
        val cap = hero.strength + hero.endurance
        val ratio = if (cap > 0) carriedWeight.toFloat() / cap else 1f
        return when {
            ratio <= 0.5f -> EncumbranceLevel.LIGHT
            ratio <= 1.0f -> EncumbranceLevel.NORMAL
            ratio <= 1.5f -> EncumbranceLevel.HEAVY
            else          -> EncumbranceLevel.OVERLOAD
        }
    }

    fun effectiveAgility(hero: Hero, carriedWeight: Int): Int {
        val level = encumbranceLevel(hero, carriedWeight)
        return (hero.agility * level.agilityMult).toInt().coerceAtLeast(1)
    }
}
