package com.grimreich.core

data class Hero(
    val id: String,
    val name: String,
    var age: Int,
    // Atrybuty pierwotne (7 atrybutów wg oryginału)
    var strength: Int     = 10,
    var agility: Int      = 10,
    var perception: Int   = 10,  // Postrzeganie — pułapki, skradanie, obserwacja
    var intelligence: Int = 10,
    var endurance: Int    = 10,
    var charisma: Int     = 10,
    var piety: Int        = 10,  // Pobożność
    
    // Status psychiczny i duchowy
    var virtue: Int       = 0,
    var divineFavor: Int  = 50,  // DF: 0-150, konieczne do modlitwy do świętych
    var sanity: Int       = 100, // 0-100, spada podczas ekspedycji na Drugą Stronę
    var corruption: Int   = 0,   // 0-100, rośnie przy kontaktach z mrokiem
    var morale: Int       = 70,  // 0-100, bieżący stan ducha
    
    // Progresja
    var level: Int        = 1,
    var xp: Int           = 0,
    var attributePoints: Int = 0,
    var portraitRes: String = "port_knight", // Default portrait

    // Punkty życia
    var hp: Int           = 30,
    var maxHp: Int        = 30,
    // Kariera
    var currentCareer: Career? = null,
    var careerHistory: List<CareerEntry> = emptyList(),
    // Cechy i zdolności
    var trait: Trait? = null,
    val abilities: MutableList<Ability> = mutableListOf(),
    // Umiejętności (mapa nazw HeroSkill -> wartość 0-100)
    // Initialized by factory or system, default to baseline
    val skills: MutableMap<String, Int> = mutableMapOf(),
    // Ekwipunek (slot -> id przedmiotu)
    val equipment: MutableMap<String, String?> = mutableMapOf(
        "weapon" to null, "armor" to null, "helmet" to null, "shield" to null
    )
)
