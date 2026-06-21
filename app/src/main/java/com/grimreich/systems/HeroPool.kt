package com.grimreich.systems

import com.grimreich.core.Hero
import java.util.UUID

object HeroPool {
    private val firstNames = listOf("Klaus", "Hans", "Helga", "Friedrich", "Gisela", "Otto", "Bruno", "Lotte", "Wilhelm", "Sigrid")
    private val lastNames = listOf("Wagner", "Müller", "Becker", "Schmidt", "Weber", "Richter", "Krüger", "Hoffmann", "Schulz", "Koch")
    private val careers = listOf("Wojownik", "Łowca", "Kapłan", "Łotrzyk", "Uczony", "Najemnik")

    fun generatePool(cityId: String, size: Int): List<Hero> {
        return List(size) {
            val name = "${firstNames.random()} ${lastNames.random()}"
            val careerName = careers.random()
            val hp = (15..40).random()
            Hero(
                id = "rec_${UUID.randomUUID()}",
                name = name,
                age = (18..50).random(),
                strength = (8..15).random(),
                agility = (8..15).random(),
                endurance = (8..15).random(),
                perception = (8..15).random(),
                intelligence = (8..15).random(),
                charisma = (8..15).random(),
                piety = (8..15).random(),
                hp = hp,
                maxHp = hp
            )
        }
    }
}
