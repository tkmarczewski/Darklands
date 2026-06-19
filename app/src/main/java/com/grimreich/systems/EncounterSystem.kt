package com.grimreich.systems

import com.grimreich.core.GameState
import javax.inject.Inject
import javax.inject.Singleton
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
    val choices: List<EncounterChoice>
)

@Singleton
class EncounterSystem @Inject constructor(
    private val lootSystem: LootSystem
) {
    private val encounters = listOf(
        Encounter(
            "enc_01", "Cienie w zaułku", "Widzisz migoczące światło w głębi uliczki.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("Sprawdź", "Znalazłeś porzuconą torbę.") { state ->
                    lootSystem.awardLoot(1.0f)
                },
                EncounterChoice("Ignoruj", "Przeszedłeś obok.") { "Bezpieczeństwo przede wszystkim." }
            )
        )
    )

    var activeEncounter: Encounter? = null

    fun rollEncounter(random: Random): Encounter? {
        if (random.nextFloat() > 0.3f) return null
        return encounters.random()
    }

    fun selectEncounter(encounter: Encounter) {
        activeEncounter = encounter
    }
}
