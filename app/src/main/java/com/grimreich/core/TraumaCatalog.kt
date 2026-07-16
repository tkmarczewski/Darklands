package com.grimreich.core

import kotlin.random.Random

/**
 * Katalog predefiniowanych traum dostępnych w grze.
 */
object TraumaCatalog {
    private val traumas = listOf(
        Trauma(
            id = "t_trembling_hands",
            name = "Trzęsące się dłonie",
            description = "Atrament pęka przy każdym ruchu. Celność spada.",
            statModifiers = mapOf("attack" to -3, "agility" to -2),
            severity = 1
        ),
        Trauma(
            id = "t_echo_vision",
            name = "Wizja Echa",
            description = "Widzisz rzeczy, których nie ma. Percepcja wzrasta, ale morale cierpi.",
            statModifiers = mapOf("perception" to 5, "morale" to -10),
            severity = 1
        ),
        Trauma(
            id = "t_hollow_voice",
            name = "Pusty Głos",
            description = "Twój głos brzmi jak szum statyczny. Charyzma spada.",
            statModifiers = mapOf("charisma" to -5),
            severity = 2
        ),
        Trauma(
            id = "t_brittle_bones",
            name = "Kruche Kości",
            description = "Twoja fizyczność staje się sugestią. Wytrzymałość spada.",
            statModifiers = mapOf("endurance" to -4),
            severity = 2
        ),
        Trauma(
            id = "t_shattered_soul",
            name = "Zdruzgotana Dusza",
            description = "Pęknięcie jest zbyt głębokie. Wszystkie atrybuty cierpią.",
            statModifiers = mapOf(
                "strength" to -2, 
                "agility" to -2, 
                "intelligence" to -2, 
                "perception" to -2
            ),
            severity = 3
        )
    )

    fun getRandomTrauma(): Trauma = traumas[Random.nextInt(traumas.size)]
    
    fun getById(id: String): Trauma? = traumas.find { it.id == id }
}

