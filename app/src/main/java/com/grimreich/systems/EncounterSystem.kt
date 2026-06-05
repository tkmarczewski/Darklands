package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.TimeOfDay
import kotlin.random.Random

enum class EncounterType {
    COMBAT, NARRATIVE, RESOURCE
}

data class Encounter(
    val id: String,
    val title: String,
    val description: String,
    val type: EncounterType
)

object EncounterSystem {
    private val encounters = listOf(
        Encounter("wolves", "Atak wilków", "Stado wygłodniałych wilków wyłania się z gęstwiny.", EncounterType.COMBAT),
        Encounter("bandits", "Zasadzka zbójców", "Zza krzaków wypadają uzbrojeni bandyci!", EncounterType.COMBAT),
        Encounter("pilgrims", "Pielgrzymi", "Spotykacie grupę zmęczonych podróżników dzielących się wieściami.", EncounterType.NARRATIVE),
        Encounter("shrine", "Zapomniana kapliczka", "Odnajdujecie miejsce kultu spowite mchem.", EncounterType.RESOURCE),
        Encounter("abandoned_cart", "Porzucony wóz", "W przydrożnym rowie leży rozbity wóz handlowy.", EncounterType.RESOURCE)
    )

    fun rollEncounter(random: Random): Encounter? {
        val chance = 0.4f // 40% chance during travel
        if (random.nextFloat() > chance) return null
        return encounters.random(random)
    }

    fun resolve(encounter: Encounter): String {
        val w = GameRepository.state.world
        w.lastEncounter = encounter.id
        
        return when (encounter.type) {
            EncounterType.COMBAT -> {
                // In a real app, this would switch to CombatActivity
                "Rozpoczyna się walka: ${encounter.title}!"
            }
            EncounterType.RESOURCE -> {
                val goldFound = Random.nextInt(10, 50)
                GameRepository.state.gold += goldFound
                "Znaleziono surowce: +$goldFound złota."
            }
            EncounterType.NARRATIVE -> {
                GameRepository.state.reputation.city.keys.randomOrNull()?.let { city ->
                    // Modify reputation
                }
                "Rozmowa z nieznajomymi przyniosła nowe informacje."
            }
        }
    }
}
