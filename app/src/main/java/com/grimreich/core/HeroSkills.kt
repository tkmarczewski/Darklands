package com.grimreich.core

import com.grimreich.core.*

enum class HeroSkill(val displayName: String, val group: SkillGroup) {
    alchemy("Alchemia", SkillGroup.academic),
    read_write("Czytanie i Pisanie", SkillGroup.academic),
    lore_history("Historia", SkillGroup.academic),
    lore_heraldry("Heraldyka", SkillGroup.academic),
    lore_law("Prawo", SkillGroup.academic),
    
    religion("Religia", SkillGroup.spiritual),
    meditation("Medytacja", SkillGroup.spiritual),
    exorcism("Egzorcyzmy", SkillGroup.spiritual),

    survival("Sztuka Przetrwania", SkillGroup.survival),
    hunting("Łowiectwo", SkillGroup.survival),
    tracking("Tropienie", SkillGroup.survival),
    healing("Leczenie", SkillGroup.survival),

    bluff("Bluff", SkillGroup.intrigue),
    stealth("Skradanie", SkillGroup.intrigue),
    pickpocket("Kradzież Kieszonkowa", SkillGroup.intrigue),
    bribery("Przekupstwo", SkillGroup.intrigue),

    melee_basic("Broń Biała (Podstawowa)", SkillGroup.weapon),
    melee_heavy("Broń Biała (Ciężka)", SkillGroup.weapon),
    ranged_basic("Łuk i Kusza", SkillGroup.weapon),
    dodge("Unik", SkillGroup.armor),
    parry("Parowanie", SkillGroup.weapon);

    companion object {
        @JvmField val ALCHEMY = alchemy; @JvmField val READ_WRITE = read_write
        @JvmField val LORE_HISTORY = lore_history; @JvmField val LORE_HERALDRY = lore_heraldry
        @JvmField val LORE_LAW = lore_law; @JvmField val RELIGION = religion
        @JvmField val MEDITATION = meditation; @JvmField val EXORCISM = exorcism
        @JvmField val SURVIVAL = survival; @JvmField val HUNTING = hunting
        @JvmField val TRACKING = tracking; @JvmField val HEALING = healing
        @JvmField val BLUFF = bluff; @JvmField val STEALTH = stealth
        @JvmField val PICKPOCKET = pickpocket; @JvmField val BRIBERY = bribery
        @JvmField val MELEE_BASIC = melee_basic; @JvmField val MELEE_HEAVY = melee_heavy
        @JvmField val RANGED_BASIC = ranged_basic; @JvmField val DODGE = dodge
        @JvmField val PARRY = parry
        
        // Added for UI/Logic matching
        @JvmField val ALCH = alchemy
    }
}

enum class SkillGroup { 
    weapon, academic, survival, intrigue, spiritual, armor;

    companion object {
        @JvmField val WEAPON = weapon; @JvmField val ACADEMIC = academic
        @JvmField val SURVIVAL = survival; @JvmField val INTRIGUE = intrigue
        @JvmField val SPIRITUAL = spiritual; @JvmField val ARMOR = armor
    }
}

enum class EncumbranceLevel(val label: String, val agilityMult: Float, val speedPenalty: Int) {
    light("Lekkie", 1.0f, 0),
    medium("Średnie", 0.8f, 1),
    heavy("Ciężkie", 0.5f, 3),
    overloaded("Przeciążenie", 0.2f, 5);

    companion object {
        @JvmField val LIGHT = light; @JvmField val MEDIUM = medium
        @JvmField val HEAVY = heavy; @JvmField val OVERLOADED = overloaded
    }
}
