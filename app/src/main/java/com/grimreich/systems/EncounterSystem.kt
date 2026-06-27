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
    private val lootSystem: LootSystem,
    private val chronicleSystem: dagger.Lazy<ChronicleSystem>
) {
    private val encounters = mutableListOf(
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
        ),
        // --- NARRATIVE ECHO EVENTS ---
        Encounter(
            "echo_frozen_archivist", "Zamarznięty Archiwista", "Na środku traktu stoi postać pokryta szronem, mimo upału. Trzyma w rękach księgę, której strony przewracają się same. 'Wszystko musi zostać skatalogowane, zanim zniknie', szepcze Archiwista.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("[Intelligence 14] Pomóż mu skatalogować otoczenie", "Pamięć jest kotwicą.", "intelligence", 14) { s ->
                    chronicleSystem.get().unlock("lore_fracture_origin")
                    s.world.globalStability += 5
                    "Twoja pomoc uspokaja Archiwistę. Wręcza Ci zapisany zwój."
                },
                EncounterChoice("Zabierz księgę siłą [Strength 15]", "Księga rozpada się w proch.", "strength", 15) { s ->
                    s.gold += 100
                    s.world.globalStability -= 5
                    "Znalazłeś 100 złota w pyłach księgi."
                }
            )
        ),
        Encounter(
            "echo_glitched_child", "Błąd w Obrazie", "Mała dziewczynka siedzi pod drzewem. Gdy mruga, jej postać przesuwa się o kilka centymetrów w bok, zostawiając za sobą powidok. 'Widzisz to?' pyta, wskazując na niebo. 'Piksele spadają jak śnieg.'",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("[Charisma 13] Uspokój dziecko", "Rzeczywistość odzyskuje ostrość.", "charisma", 13) { s ->
                    s.party.forEach { h -> h.hp = (h.hp + 10).coerceAtMost(h.maxHp) }
                    "Dziewczynka uśmiecha się. Odzyskaliście spokój ducha."
                },
                EncounterChoice("[Perception 15] Zbadaj niebo", "Widzisz błękitny kod.", "perception", 15) { s ->
                    chronicleSystem.get().unlock("lore_scribes")
                    s.world.echoIntensity += 0.1f
                    "Widzisz surowy kod rzeczywistości. Twoja Kotwica drży."
                }
            )
        )
    )

    var activeEncounter: Encounter? = null

    fun rollEncounter(random: Random, state: GameState): Encounter? {
        // --- FACTION RAIDS ---
        // Logic fix: Factions are indexed by their names in ReputationSystem
        val hostileFactions = state.reputation.globalFactions.filter { it.value <= -50 }.keys
        if (hostileFactions.isNotEmpty() && random.nextFloat() < 0.2f) {
            val factionId = hostileFactions.toList().random(random)
            return Encounter(
                id = "raid_${factionId}",
                title = "Zasadzka: ${factionId.uppercase()}",
                description = "Twoje działania przeciwko frakcji ${factionId.uppercase()} nie pozostały niezauważone. Grupa zabójców zastępuje Ci drogę!",
                type = EncounterType.COMBAT,
                choices = listOf(
                    EncounterChoice("Walcz o życie!", "Rozpoczyna się brutalne starcie.") { s ->
                        val (name, hp, atk) = when(factionId.uppercase()) {
                            "CHURCH", "INKWIZYCJA" -> Triple("Egzekutor Inkwizycji", 70, 15)
                            "KNIGHTS", "ZAKON" -> Triple("Mściciel Zakonu", 65, 14)
                            else -> Triple("Zabójca Frakcyjny", 60, 12)
                        }
                        "POJEDYNEK:$name:$hp:$atk" 
                    }
                )
            )
        }

        if (random.nextFloat() > 0.3f) return null
        return encounters.random(random)
    }

    fun selectEncounter(encounter: Encounter) {
        activeEncounter = encounter
    }
}
