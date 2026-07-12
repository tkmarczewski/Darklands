package com.grimreich.world

import com.grimreich.core.Career
import com.grimreich.core.Hero
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/*
 * HeroPool — generator losowej puli bohaterów do rekrutacji.
 */
@Singleton
class HeroPool @Inject constructor(
    private val random: com.grimreich.core.CombatRandomProvider
) {

    // ── Pule losowych imion ──────────────────────────────────────────────────
    private val maleFirstNames = listOf(
        "Klaus", "Rolf", "Erik", "Gunter", "Hans", "Wilhelm", "Dietrich", "Albrecht",
        "Conrad", "Rudolf", "Franz", "Otto", "Heinrich", "Gerhard", "Ulrich", "Siegfried",
        "Balthasar", "Kaspar", "Werner", "Hartmann", "Leberecht", "Nikolaus", "Benedikt"
    )

    private val femaleFirstNames = listOf(
        "Helga", "Ingrid", "Brunhilde", "Elsa", "Marta", "Gertruda", "Hildegard", "Adelheid",
        "Mechthild", "Kunigunde", "Walburga", "Hedwig", "Beatrix", "Lutgard", "Richenza",
        "Sophia", "Agnes", "Margarethe", "Euphemia"
    )

    private val lastNames = listOf(
        "von Stein", "zum Hammer", "der Krähe", "Rattenkopf", "Finstermut", "Grauwald",
        "Eisenhand", "Blutklinge", "Dunkelstern", "Nebelborn", "Schattenläufer", "Wulfsdorf",
        "Knochenbrecher", "Morgenrot", "Sturmvogel", "Galgenholz", "Schattenaxt",
        "Todesklinge", "Schwarzfels", "Narbenherz"
    )

    // ── Profesje dostępne w rekrutacji (podzbiór Career) ────────────────────
    private val recruitableCareers = listOf(
        Career.MERCENARY, Career.KNIGHT, Career.THIEF, Career.SCHOLAR, Career.MONK,
        Career.GUARD, Career.PHYSICIAN, Career.CRAFTSMAN, Career.ROGUE, Career.ALCHEMIST
    )

    // ── Portrety per profesja ────────────────────────────────────────────────
    private val portraitByCareer = mapOf(
        Career.KNIGHT to "port_knight",
        Career.MERCENARY to "port_mercenary",
        Career.THIEF to "port_rogue",
        Career.ROGUE to "port_rogue",
        Career.SCHOLAR to "port_scholar",
        Career.MONK to "port_monk",
        Career.GUARD to "port_guard",
        Career.PHYSICIAN to "port_physician",
        Career.CRAFTSMAN to "port_craftsman",
        Career.ALCHEMIST to "port_scholar"
    )

    // ── Ekwipunek startowy per profesja ─────────────────────────────────────
    // Wartości to String ID przedmiotów zgodne z ItemCatalogue
    private val weaponByCareer = mapOf(
        Career.KNIGHT to listOf("sword_basic", "sword_long"),
        Career.MERCENARY to listOf("sword_basic", "axe_hand", "spear_basic"),
        Career.THIEF to listOf("dagger_basic", "dagger_stiletto"),
        Career.ROGUE to listOf("dagger_basic", "sword_short"),
        Career.SCHOLAR to listOf("staff_basic"),
        Career.MONK to listOf("staff_basic", "mace_basic"),
        Career.GUARD to listOf("spear_basic", "sword_short"),
        Career.PHYSICIAN to listOf("dagger_basic"),
        Career.CRAFTSMAN to listOf("axe_hand", "hammer_basic"),
        Career.ALCHEMIST to listOf("staff_basic", "dagger_basic")
    )

    private val armorByCareer = mapOf(
        Career.KNIGHT to listOf("armor_chainmail", "armor_plate_partial"),
        Career.MERCENARY to listOf("armor_leather", "armor_chainmail"),
        Career.THIEF to listOf("armor_leather_light"),
        Career.ROGUE to listOf("armor_leather_light"),
        Career.SCHOLAR to listOf("armor_cloth"),
        Career.MONK to listOf("armor_cloth"),
        Career.GUARD to listOf("armor_leather"),
        Career.PHYSICIAN to listOf("armor_cloth"),
        Career.CRAFTSMAN to listOf("armor_leather"),
        Career.ALCHEMIST to listOf("armor_cloth")
    )

    // ── Zakres atrybutów per profesja ────────────────────────────────────────
    private data class StatRange(val base: Int, val spread: Int)

    private fun statRangesFor(career: Career): Map<String, StatRange> =
        when (career) {
            Career.KNIGHT ->
                mapOf("str" to StatRange(13,4), "agi" to StatRange(10,3), "per" to StatRange(9,3),
                      "int" to StatRange(8,3), "end" to StatRange(12,4), "cha" to StatRange(10,3), "pie" to StatRange(11,3))
            Career.MERCENARY ->
                mapOf("str" to StatRange(12,4), "agi" to StatRange(11,3), "per" to StatRange(10,3),
                      "int" to StatRange(8,3), "end" to StatRange(12,3), "cha" to StatRange(9,3), "pie" to StatRange(8,3))
            Career.THIEF ->
                mapOf("str" to StatRange(8,3), "agi" to StatRange(14,4), "per" to StatRange(13,4),
                      "int" to StatRange(10,3), "end" to StatRange(9,3), "cha" to StatRange(9,3), "pie" to StatRange(9,3))
            Career.SCHOLAR ->
                mapOf("str" to StatRange(8,3), "agi" to StatRange(10,3), "per" to StatRange(10,3),
                      "int" to StatRange(10,3), "end" to StatRange(9,3), "cha" to StatRange(9,3), "pie" to StatRange(9,3))
            else ->
                mapOf("str" to StatRange(10,3), "agi" to StatRange(10,3), "per" to StatRange(10,3),
                      "int" to StatRange(10,3), "end" to StatRange(10,3), "cha" to StatRange(10,3), "pie" to StatRange(10,3))
        }

    // ── Główna metoda ────────────────────────────────────────────────────────
    /**
     * Generuje [count] losowych, unikalnych bohaterów.
     * Każde wywołanie zwraca inną pulę — UUID gwarantuje unikalność ID.
     */
    fun generatePool(count: Int = 4): List<Hero> {
        return (1..count).map { generateHero() }
    }

    fun generateHero(): Hero {
        val career = recruitableCareers.random()
        val isFemale = random.nextFloat() < 0.5f
        val firstName = if (isFemale) femaleFirstNames.random() else maleFirstNames.random()
        val lastName = lastNames.random()
        val name = "$firstName $lastName"

        val age = (career.minAge + 4).coerceAtLeast(16) + random.nextInt(12)
        val ranges = statRangesFor(career)
        fun roll(key: String): Int =
            (ranges[key] ?: StatRange(10, 3)).let { it.base + random.nextInt(it.spread + 1) }

        val str = roll("str")
        val agi = roll("agi")
        val per = roll("per")
        val int = roll("int")
        val end = roll("end")
        val cha = roll("cha")
        val pie = roll("pie")

        val weapon = weaponByCareer[career]?.random()
        val armor = armorByCareer[career]?.random()

        val skills = skillsByCareer[career]
            ?.mapValues { (_, base) -> (base + random.nextInt(11) - 5).coerceAtLeast(1) }
            ?.toMutableMap() ?: mutableMapOf()

        val portrait = portraitByCareer[career] ?: "port_knight"

        return Hero(
            id = UUID.randomUUID().toString(),
            name = name,
            age = age,
            strength = str,
            agility = agi,
            perception = per,
            intelligence = int,
            endurance = end,
            charisma = cha,
            piety = pie,
            currentCareer = career,
            portraitRes = portrait,
            skills = skills,
            equipment = mutableMapOf(
                "weapon" to weapon,
                "armor" to armor,
                "helmet" to null,
                "shield" to null,
                "accessory" to null
            )
        ).apply { 
            // Ensures correct HP calculation based on Endurance
            normalize() 
        }
    }

    // ── Skills map ──────────────────────────────────────────────────────────
    private val skillsByCareer = mapOf(
        Career.KNIGHT to mapOf("Cięcie" to 12, "Taran" to 15, "Tarczownik" to 35, "Zastraszanie" to 25),
        Career.MERCENARY to mapOf("Miecze" to 10, "Topory" to 9, "Obrona" to 15, "Wojenne rzemiosło" to 12),
        Career.THIEF to mapOf("Skradanie" to 12, "Otwieranie zamków" to 15, "Zwinne dłonie" to 10),
        Career.ROGUE to mapOf("Sztylety" to 15, "Skradanie" to 10, "Uliczny spryt" to 12),
        Career.SCHOLAR to mapOf("Czytanie i Pisanie" to 15, "Starożytne języki" to 12, "Historia" to 10),
        Career.MONK to mapOf("Pobożność" to 18, "Leczenie" to 12, "Modlitwa" to 15),
        Career.GUARD to mapOf("Drzewce" to 12, "Warta" to 15, "Prawo i Porządek" to 10),
        Career.PHYSICIAN to mapOf("Anatomia" to 15, "Pierwsza pomoc" to 18, "Ziołolecznictwo" to 12),
        Career.CRAFTSMAN to mapOf("Kowalstwo" to 15, "Naprawa" to 12, "Wycena" to 10),
        Career.ALCHEMIST to mapOf("Alchemia" to 15, "Identyfikacja" to 12, "Chemia" to 10)
    )

    // ── Koszt rekrutacji per profesja ────────────────────────────────────────
    fun hireCostFor(career: Career): Int =
        when (career) {
            Career.KNIGHT -> 120
            Career.MERCENARY -> 70
            Career.THIEF -> 60
            Career.ROGUE -> 55
            Career.SCHOLAR -> 80
            Career.MONK -> 50
            Career.GUARD -> 45
            Career.PHYSICIAN -> 90
            Career.CRAFTSMAN -> 50
            Career.ALCHEMIST -> 100
            else -> 50
        }
}
