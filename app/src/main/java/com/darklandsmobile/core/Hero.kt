package com.darklandsmobile.core

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
    // Cnota i wiara
    var virtue: Int       = 0,
    var divineFavor: Int  = 50,  // DF: 0-150, konieczne do modlitwy do swietych
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
