package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

enum class HeroSkill(val displayName: String, val group: SkillGroup) {
    W_EDG("Ostrza", SkillGroup.WEAPON),
    W_IMP("Obuchowe", SkillGroup.WEAPON),
    W_FLL("Korbacze", SkillGroup.WEAPON),
    W_POL("Drzewne", SkillGroup.WEAPON),
    W_THR("Miotane", SkillGroup.WEAPON),
    W_BOW("Łuki", SkillGroup.WEAPON),
    W_MSD("Kusze", SkillGroup.WEAPON),

    ALCH("Alchemia", SkillGroup.ACADEMIC),
    RELG("Religia", SkillGroup.ACADEMIC),
    VIRT("Cnota", SkillGroup.SPIRITUAL),
    SPK_C("Łacina", SkillGroup.ACADEMIC),
    SPK_L("Lokalny", SkillGroup.ACADEMIC),
    RW("Czytanie/Pisanie", SkillGroup.ACADEMIC),

    HEAL("Leczenie", SkillGroup.SURVIVAL),
    ARTF("Rzemiosło", SkillGroup.SURVIVAL),
    STL_H("Skradanie", SkillGroup.SURVIVAL),
    STR_W("Przetrwanie", SkillGroup.SURVIVAL),
    RIDE("Jeździectwo", SkillGroup.SURVIVAL),
    WD_WS("Leśnictwo", SkillGroup.SURVIVAL),

    PERS("Perswazja", SkillGroup.INTRIGUE),
    INTI("Zastraszanie", SkillGroup.INTRIGUE),
    DECP("Oszustwo", SkillGroup.INTRIGUE),
    SOCI("Etykieta", SkillGroup.INTRIGUE),
    INVN("Śledztwo", SkillGroup.INTRIGUE),

    PRAY("Modlitwa", SkillGroup.SPIRITUAL),
    MEDI("Medytacja", SkillGroup.SPIRITUAL),
    EXOR("Egzorcyzmy", SkillGroup.SPIRITUAL),

    L_ARM("Lekki Pancerz", SkillGroup.ARMOR),
    H_ARM("Ciężki Pancerz", SkillGroup.ARMOR),
    SHLD("Tarcza", SkillGroup.ARMOR)
}

enum class SkillGroup { WEAPON, ACADEMIC, SURVIVAL, INTRIGUE, SPIRITUAL, ARMOR }

enum class EncumbranceLevel(val label: String, val agilityMult: Float, val speedPenalty: Int) {
    LIGHT("Lekki", 1.0f, 0),
    NORMAL("Normalny", 0.9f, 1),
    HEAVY("Ciężki", 0.7f, 3),
    OVERLOAD("Przeciążenie", 0.4f, 6)
}

@Singleton
class SkillSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun defaultSkills(): MutableMap<String, Int> {
        return HeroSkill.values().associate { it.name to 5 }.toMutableMap()
    }

    fun practiceSkill(hero: Hero, skill: HeroSkill, success: Boolean): Boolean {
        val current = hero.skills[skill.name] ?: 5
        if (current >= 100) return false

        val chance = if (success) 5 else 15
        if (kotlin.random.Random.nextInt(100) < chance) {
            hero.skills[skill.name] = current + 1
            gameRepository.persistCurrentState()
            return true
        }
        return false
    }

    fun getSkill(hero: Hero, skill: HeroSkill): Int = hero.skills[skill.name] ?: 5

    fun encumbranceLevel(hero: Hero, currentWeight: Int): EncumbranceLevel {
        val capacity = hero.strength * 2
        return when {
            currentWeight <= capacity * 0.5 -> EncumbranceLevel.LIGHT
            currentWeight <= capacity -> EncumbranceLevel.NORMAL
            currentWeight <= capacity * 1.5 -> EncumbranceLevel.HEAVY
            else -> EncumbranceLevel.OVERLOAD
        }
    }

    fun effectiveAgility(hero: Hero, currentWeight: Int): Int {
        val enc = encumbranceLevel(hero, currentWeight)
        return (hero.agility * enc.agilityMult).toInt()
    }
}

