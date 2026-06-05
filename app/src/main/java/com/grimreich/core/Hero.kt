package com.grimreich.core

data class Hero(
    val id: String,
    val name: String,
    var age: Int,
    // Atrybuty pierwotne (7 atrybutow wg oryginalu)
    var strength: Int     = 10,
    var agility: Int      = 10,
    var perception: Int   = 10,  // Postrzeganie — pulapki, skradanie, obserwacja
    var intelligence: Int = 10,
    var endurance: Int    = 10,
    var charisma: Int     = 10,
    var piety: Int        = 10,  // Pobozhnosc
    
    // Status psychiczny i duchowy
    var virtue: Int       = 0,
    var divineFavor: Int  = 50,  // DF: 0-150, konieczne do modlitwy do swietych
    var sanity: Int       = 100, // 0-100, spada podczas ekspedycji na Druga Strone
    var corruption: Int   = 0,   // 0-100, rosnie przy kontaktach z mrokiem
    var morale: Int       = 70,  // 0-100, biezacy stan ducha

    // Punkty zycia
    var hp: Int           = 30,
    var maxHp: Int        = 30,
    // Kariera
    var currentCareer: Career? = null,
    var careerHistory: List<CareerEntry> = emptyList(),
    // Umiejetnosci (mapa nazw HeroSkill -> wartosc 0-100)
    val skills: MutableMap<String, Int> = SkillSystem.defaultSkills(),
    // Ekwipunek (slot -> id przedmiotu)
    val equipment: MutableMap<String, String?> = mutableMapOf(
        "weapon" to null, "armor" to null, "helmet" to null
    )
)
