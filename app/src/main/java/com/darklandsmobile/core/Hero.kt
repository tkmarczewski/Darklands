package com.darklandsmobile.core

data class Hero(
    val id: String,
    val name: String,
    var age: Int,
    var strength: Int = 10,
    var dex: Int = 10,
    var intelligence: Int = 10,
    var endurance: Int = 10,
    var charisma: Int = 10,
    var piety: Int = 10,
    var hp: Int = 30,
    var maxHp: Int = 30,
    val skills: MutableMap<String, Int> = mutableMapOf(),
    val equipment: MutableMap<String, String?> = mutableMapOf(
        "weapon" to null, "armor" to null, "helmet" to null
    )
)
