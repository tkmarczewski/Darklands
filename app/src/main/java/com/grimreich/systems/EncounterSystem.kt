package com.grimreich.systems

import com.grimreich.core.GameConstants
import com.grimreich.core.GameState
import com.grimreich.core.GameRepository
import com.grimreich.core.Bestiary
import com.grimreich.core.CombatRandomProvider
import com.grimreich.core.EnemyType
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

enum class EncounterType { COMBAT, INTERACTIVE, RESOURCE }

data class EncounterChoice(
    val label: String,
    val description: String,
    val requiredAttribute: String? = null,
    val requiredValue: Int = 0,
    val effect: (GameState) -> String,
    val combatEnemyType: EnemyType? = null
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
    private val chronicleSystem: Lazy<ChronicleSystem>
) {
    // FIX: mutableListOf dla możliwości addEncounter/removeEncounter w testach
    private val encounters = mutableListOf(
        Encounter(
            "enc_01", "Cienie w zaułku", "Widzisz migoczące światło w głębi uliczki.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("Sprawdź", "Znalazłeś porzuconą torbę.", effect = { state ->
                    lootSystem.awardLootDirect(state, 1.0f)
                }),
                EncounterChoice("Ignoruj", "Przeszedłeś obok.", effect = { "Bezpieczeństwo przede wszystkim." })
            )
        ),
        Encounter(
            "enc_per_01", "Ukryta Skrytka",
            "Twoje zmysły podpowiadają, że pod luźnym kamieniem coś się znajduje.",
            EncounterType.RESOURCE,
            listOf(
                EncounterChoice("Przeszukaj skrytkę", "Znalazłeś stare monety!",
                    "perception", 12, effect = { state ->
                        state.gold += 50
                        "Znalazłeś 50 złota!"
                    }),
                EncounterChoice("Zostaw to", "Może to pułapka.", effect = { "Lepiej nie ryzykować." })
            )
        ),
        Encounter(
            "enc_int_01", "Dziwny Mechanizm",
            "Na środku drogi stoi dziwna, pulsująca maszyna echa.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("Rozszyfruj działanie",
                    "Ustabilizowałeś fragment rzeczywistości!", "intelligence", 14,
                    effect = { state ->
                        state.world.globalStability += 10
                        "Stabilność świata wzrosła!"
                    }),
                EncounterChoice("Omiń", "Wygląda niebezpiecznie.", effect = { "Przyspieszyłeś kroku." })
            )
        ),
        // --- NARRATIVE ECHO EVENTS ---
        Encounter(
            "echo_frozen_archivist", "Zamarznięty Archiwista",
            "Na środku traktu stoi postać pokryta szronem, mimo upału. Trzyma w rękach księgę, " +
            "której strony przewracają się same. 'Wszystko musi zostać skatalogowane, zanim zniknie', szepcze Archiwista.",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice(
                    "Pomóż mu skatalogować otoczenie",
                    "Pamięć jest kotwicą.", "intelligence", 14,
                    effect = { s ->
                        chronicleSystem.get().unlock("lore_fracture_origin")
                        s.world.globalStability += 5
                        "Twoja pomoc uspokaja Archiwistę. Wręcza Ci zapisany zwój."
                    }),
                EncounterChoice(
                    "Zabierz księgę siłą",
                    "Księga rozpada się w proch.", "strength", 15,
                    effect = { s ->
                        s.gold += 100
                        s.world.globalStability -= 5
                        "Znalazłeś 100 złota w pyłach księgi."
                    })
            )
        ),
        Encounter(
            "echo_glitched_child", "Błąd w Obrazie",
            "Mała dziewczynka siedzi pod drzewem. Gdy mruga, jej postać przesuwa się o kilka " +
            "centymetrów w bok, zostawiając za sobą powidok. 'Widzisz to?' pyta, wskazując na niebo. " +
            "'Piksele spadają jak śnieg.'",
            EncounterType.INTERACTIVE,
            listOf(
                EncounterChoice("Uspokój dziecko",
                    "Rzeczywistość odzyskuje ostrość.", "charisma", 13,
                    effect = { s ->
                        s.party.forEach { h -> h.hp = (h.hp + 10).coerceAtMost(h.maxHp) }
                        "Dziewczynka uśmiecha się. Odzyskaliście spokój ducha."
                    }),
                EncounterChoice("Zbadaj niebo",
                    "Widzisz błękitny kod.", "perception", 15,
                    effect = { s ->
                        chronicleSystem.get().unlock("lore_scribes")
                        s.world.echoIntensity += 0.1f
                        "Widzisz surowy kod rzeczywistości. Twoja Kotwica drży."
                    })
            )
        )
    )

    var activeEncounter: Encounter? = null

    /** Resets active encounter to null. */
    fun clearActiveEncounter() {
        activeEncounter = null
    }

    // FIX: Usunięto kotlin.random.Random — parametr to CombatRandomProvider
    fun rollEncounter(random: CombatRandomProvider, state: GameState): Encounter? {
        // --- FACTION RAIDS ---
        val hostileFactions = state.reputation.globalFactions.filter { it.value <= GameConstants.HOSTILE_REPUTATION_THRESHOLD }.keys
        if (hostileFactions.isNotEmpty() && random.nextFloat() < GameConstants.FACTION_RAID_CHANCE) {
            val factionId = hostileFactions.toList()[random.nextInt(hostileFactions.size)]
            val enemyType = when (factionId.uppercase()) {
                "CHURCH", "INKWIZYCJA" -> EnemyType.CITY_GUARD
                "KNIGHTS", "ZAKON"     -> EnemyType.RAUBRITTER_KNIGHT
                else                   -> EnemyType.BANDIT
            }
            return Encounter(
                id = "raid_${factionId}",
                title = "Zasadzka: ${factionId.uppercase()}",
                description = "Twoje działania przeciwko frakcji ${factionId.uppercase()} " +
                              "nie pozostały niezauważone. Grupa zabójców zastępuje Ci drogę!",
                type = EncounterType.COMBAT,
                choices = listOf(
                    EncounterChoice(
                        "Walcz o życie!", "Rozpoczyna się brutalne starcie.",
                        effect = { "POJEDYNEK" },
                        combatEnemyType = enemyType
                    )
                )
            )
        }

        if (random.nextFloat() > GameConstants.ENCOUNTER_CHANCE) return null
        if (encounters.isEmpty()) return null
        return encounters[random.nextInt(encounters.size)]
    }

    fun selectEncounter(encounter: Encounter) {
        activeEncounter = encounter
    }

    /** Dla testów: wstrzyknięcie własnego encountera. */
    fun addEncounter(encounter: Encounter) { encounters.add(encounter) }

    /** Dla testów: usunięcie encountera po id. */
    fun removeEncounter(id: String) { encounters.removeAll { it.id == id } }
}
