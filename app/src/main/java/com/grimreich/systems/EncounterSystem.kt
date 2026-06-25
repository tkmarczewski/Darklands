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
    val requiredAttribute: String? = null,
    val requiredValue: Int = 0,
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
        ),
        Encounter(
            "enc_per_01", "Ukryta Skrytka", "Twoje zmysły podpowiadają, że pod luźnym kamieniem coś się znajduje.",
            EncounterType.RESOURCE,
            listOf(
                EncounterChoice("[Perception 12] Przeszukaj skrytkę", "Znalazłeś stare monety!", "perception", 12) { state ->
                    state.gold += 50
                    "Znalazłeś 50 złota!"
                },
                EncounterChoice("Zostaw to", "Może to pułapka.") { "Lepiej nie ryzykować." }
            )
        ),
        Encounter(
            "enc_int_01", "Dziwny Mechanizm", "Na środku drogi stoi dziwna, pulsująca maszyna echa.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("[Intelligence 14] Rozszyfruj działanie", "Ustabilizowałeś fragment rzeczywistości!", "intelligence", 14) { state ->
                    state.world.globalStability += 10
                    "Stabilność świata wzrosła!"
                },
                EncounterChoice("Omiń", "Wygląda niebezpiecznie.") { "Przyspieszyłeś kroku." }
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
