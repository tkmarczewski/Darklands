package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import kotlin.random.Random

enum class EncounterType {
    COMBAT, INTERACTIVE, RESOURCE
}

data class EncounterChoice(
    val label: String,
    val description: String,
    val effect: (GameState) -> String
)

data class Encounter(
    val id: String,
    val title: String,
    val description: String,
    val type: EncounterType,
    val choices: List<EncounterChoice> = emptyList()
)

object EncounterSystem {
    private val encounters = listOf(
        Encounter(
            "abandoned_cart", 
            "Porzucony wóz", 
            "W przydrożnym rowie leży rozbity wóz handlowy. Nie widać żywej duszy.", 
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("Przeszukaj", "Szukasz cennych przedmiotów.") { state ->
                    val gold = Random.nextInt(20, 60)
                    state.gold += gold
                    state.party.forEach { it.corruption += 1 }
                    val lootMsg = LootSystem.awardLoot(0.3f)
                    "Znalazłeś $gold złota, ale sumienie cię gryzie (+1 Korupcja).$lootMsg"
                },
                EncounterChoice("Módl się", "Odmawiasz modlitwę za właścicieli.") { state ->
                    state.prayer.virtue += 2
                    state.party.forEach { it.sanity += 5 }
                    "Poczuliście spokój (+2 Cnota, +5 Poczytalność)."
                },
                EncounterChoice("Zignoruj", "Omijasz wóz szerokim łukiem.") { "Zostawiliście to miejsce w mroku." }
            )
        ),
        Encounter(
            "mysterious_shrine",
            "Mroczna kapliczka",
            "Na rozstajach dróg stoi kapliczka spowita czarną mgłą.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("Złóż ofiarę", "Poświęcasz odrobinę krwi.") { state ->
                    state.party.forEach { 
                        it.hp -= 5
                        it.corruption += 5
                    }
                    "Mrok cię zauważył (+5 Korupcja, -5 HP)."
                },
                EncounterChoice("Oczyść ją", "Używasz świętej wody i modlitwy.") { state ->
                    state.prayer.faith += 5
                    state.prayer.virtue += 5
                    "Mgła nieco rzednie (+5 Wiara, +5 Cnota)."
                }
            )
        )
    )

    fun rollEncounter(random: Random): Encounter? {
        val chance = 0.5f 
        if (random.nextFloat() > chance) return null
        return encounters.random(random)
    }

    var activeEncounter: Encounter? = null

    fun selectEncounter(encounter: Encounter) {
        activeEncounter = encounter
    }
}
