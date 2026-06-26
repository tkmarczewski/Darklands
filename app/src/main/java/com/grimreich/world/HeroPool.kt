package com.grimreich.world

import com.grimreich.core.Career
import com.grimreich.core.Hero
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * HeroPool — generator losowej puli bohaterów do rekrutacji.
 * Każde wywołanie [generatePool] zwraca 4 w pełni losowych najemników:
 * imię, wiek, profesja, 7 atrybutów, umiejętności startowe, wyposażenie.
 * HeroPool jest Singleton — generator jest jeden, ale pula jest zawsze świeża.
 */
@Singleton
class HeroPool @Inject constructor() {

    // ── Pule losowych imion ──────────────────────────────────────────────────

    private val maleFirstNames = listOf(
        "Klaus", "Rolf", "Erik", "Gunter", "Hans", "Wilhelm", "Dietrich", "Albrecht",
        "Conrad", "Rudolf", "Franz", "Otto", "Heinrich", "Gerhard", "Ulrich", "Siegfried",
        "Balthasar", "Kaspar", "Werner", "Hartmann", "Leberecht", "Nikolaus", "Benedikt"
    )

    private val femaleFirstNames = listOf(
        "Helga", "Ingrid", "Brunhilde", "Elsa", "Marta", "Gertruda", "Hildegard",
        "Adelheid", "Mechthild", "Kunigunde", "Walburga", "Hedwig", "Beatrix",
        "Lutgard", "Richenza", "Sophia", "Agnes", "Margarethe", "Euphemia"
    )

    private val lastNames = listOf(
        "von Stein", "zum Hammer", "der Krähe", "Rattenkopf", "Finstermut",
        "Grauwald", "Eisenhand", "Blutklinge", "Dunkelstern", "Nebelborn",
        "Schattenläufer", "Wulfsdorf", "Knochenbrecher", "Morgenrot", "Sturmvogel",
        "Galgenholz", "Schattenaxt", "Todesklinge", "Schwarzfels", "Narbenherz"
    )

    // ── Profesje dostępne w rekrutacji (podzbiór Career) ────────────────────

    private val recruitableCareers = listOf(
        Career.MERCENARY,
        Career.KNIGHT,
        Career.THIEF,
        Career.SCHOLAR,
        Career.MONK,
        Career.GUARD,
        Career.PHYSICIAN,
        Career.CRAFTSMAN,
        Career.ROGUE,
        Career.ALCHEMIST
    )

    // ── Portrety per profesja ────────────────────────────────────────────────

    private val portraitByCareer = mapOf(
        Career.KNIGHT     to "port_knight",
        Career.MERCENARY  to "port_mercenary",
        Career.THIEF      to "port_rogue",
        Career.ROGUE      to "port_rogue",
        Career.SCHOLAR    to "port_scholar",
        Career.MONK       to "port_monk",
        Career.GUARD      to "port_guard",
        Career.PHYSICIAN  to "port_physician",
        Career.CRAFTSMAN  to "port_craftsman",
        Career.ALCHEMIST  to "port_scholar"
    )

    // ── Ekwipunek startowy per profesja ─────────────────────────────────────
    // Wartości to String ID przedmiotów zgodne z ItemCatalogue

    private val weaponByCareer = mapOf(
        Career.KNIGHT     to listOf("sword_basic", "sword_long"),
        Career.MERCENARY  to listOf("sword_basic", "axe_hand", "spear_basic"),
        Career.THIEF      to listOf("dagger_basic", "dagger_stiletto"),
        Career.ROGUE      to listOf("dagger_basic", "sword_short"),
        Career.SCHOLAR    to listOf("staff_basic"),
        Career.MONK       to listOf("staff_basic", "mace_basic"),
        Career.GUARD      to listOf("spear_basic", "sword_short"),
        Career.PHYSICIAN  to listOf("dagger_basic"),
        Career.CRAFTSMAN  to listOf("axe_hand", "hammer_basic"),
        Career.ALCHEMIST  to listOf("staff_basic", "dagger_basic")
    )

    private val armorByCareer = mapOf(
        Career.KNIGHT     to listOf("armor_chainmail", "armor_plate_partial"),
        Career.MERCENARY  to listOf("armor_leather", "armor_chainmail"),
        Career.THIEF      to listOf("armor_leather_light"),
        Career.ROGUE      to listOf("armor_leather_light"),
        Career.SCHOLAR    to listOf("armor_cloth"),
        Career.MONK       to listOf("armor_cloth"),
        Career.GUARD      to listOf("armor_leather"),
        Career.PHYSICIAN  to listOf("armor_cloth"),
        Career.CRAFTSMAN  to listOf("armor_leather"),
        Career.ALCHEMIST  to listOf("armor_cloth")
    )

    // ── Umiejętności startowe per profesja ───────────────────────────────────

    private val skillsByCareer = mapOf(
        Career.KNIGHT    to mapOf("Sword" to 40, "Ride" to 30, "Dodge" to 25, "Piety" to 20),
        Career.MERCENARY to mapOf("Sword" to 35, "Axe" to 35, "Dodge" to 30, "Intimidate" to 20),
        Career.THIEF     to mapOf("Sneak" to 50, "Pick Lock" to 40, "Dagger" to 35, "Dodge" to 30),
        Career.ROGUE     to mapOf("Dagger" to 40, "Sneak" to 35, "Streetwise" to 30, "Dodge" to 25),
        Career.SCHOLAR   to mapOf("Read" to 60, "Alchemy" to 30, "Lore" to 50, "Heal" to 25),
        Career.MONK      to mapOf("Piety" to 60, "Heal" to 40, "Read" to 40, "Mace" to 25),
        Career.GUARD     to mapOf("Spear" to 40, "Shield" to 35, "Dodge" to 30, "Intimidate" to 25),
        Career.PHYSICIAN to mapOf("Heal" to 60, "Herb Lore" to 50, "Read" to 30, "Dagger" to 20),
        Career.CRAFTSMAN to mapOf("Craft" to 50, "Axe" to 35, "Appraise" to 30, "Strength" to 25),
        Career.ALCHEMIST to mapOf("Alchemy" to 60, "Read" to 50, "Herb Lore" to 40, "Lore" to 35)
    )

    // ── Zakres atrybutów per profesja ────────────────────────────────────────

    private data class StatRange(val base: Int, val spread: Int)

    private fun statRangesFor(career: Career): Map<String, StatRange> = when (career) {
        Career.KNIGHT    -> mapOf("str" to StatRange(13,4), "agi" to StatRange(10,3), "per" to StatRange(9,3),  "int" to StatRange(8,3),  "end" to StatRange(12,4), "cha" to StatRange(10,3), "pie" to StatRange(11,3))
        Career.MERCENARY -> mapOf("str" to StatRange(12,4), "agi" to StatRange(11,3), "per" to StatRange(10,3), "int" to StatRange(8,3),  "end" to StatRange(12,3), "cha" to StatRange(9,3),  "pie" to StatRange(8,3))
        Career.THIEF     -> mapOf("str" to StatRange(8,3),  "agi" to StatRange(14,4), "per" to StatRange(13,4), "int" to StatRange(10,3), "end" to StatRange(9,3),  "cha" to StatRange(11,3), "pie" to StatRange(7,3))
        Career.ROGUE     -> mapOf("str" to StatRange(9,3),  "agi" to StatRange(13,4), "per" to StatRange(12,3), "int" to StatRange(9,3),  "end" to StatRange(9,3),  "cha" to StatRange(12,3), "pie" to StatRange(7,3))
        Career.SCHOLAR   -> mapOf("str" to StatRange(7,3),  "agi" to StatRange(8,3),  "per" to StatRange(11,3), "int" to StatRange(14,4), "end" to StatRange(8,3),  "cha" to StatRange(10,3), "pie" to StatRange(10,3))
        Career.MONK      -> mapOf("str" to StatRange(8,3),  "agi" to StatRange(9,3),  "per" to StatRange(10,3), "int" to StatRange(12,3), "end" to StatRange(10,3), "cha" to StatRange(9,3),  "pie" to StatRange(14,4))
        Career.GUARD     -> mapOf("str" to StatRange(11,3), "agi" to StatRange(10,3), "per" to StatRange(10,3), "int" to StatRange(8,3),  "end" to StatRange(11,3), "cha" to StatRange(9,3),  "pie" to StatRange(8,3))
        Career.PHYSICIAN -> mapOf("str" to StatRange(8,3),  "agi" to StatRange(10,3), "per" to StatRange(12,3), "int" to StatRange(13,4), "end" to StatRange(9,3),  "cha" to StatRange(11,3), "pie" to StatRange(10,3))
        Career.CRAFTSMAN -> mapOf("str" to StatRange(12,3), "agi" to StatRange(10,3), "per" to StatRange(9,3),  "int" to StatRange(10,3), "end" to StatRange(11,3), "cha" to StatRange(8,3),  "pie" to StatRange(8,3))
        Career.ALCHEMIST -> mapOf("str" to StatRange(7,3),  "agi" to StatRange(9,3),  "per" to StatRange(11,3), "int" to StatRange(14,4), "end" to StatRange(8,3),  "cha" to StatRange(9,3),  "pie" to StatRange(10,3))
        else             -> mapOf("str" to StatRange(10,3), "agi" to StatRange(10,3), "per" to StatRange(10,3), "int" to StatRange(10,3), "end" to StatRange(10,3), "cha" to StatRange(10,3), "pie" to StatRange(10,3))
    }

    // ── Koszt rekrutacji per profesja ────────────────────────────────────────

    fun hireCostFor(career: Career): Int = when (career) {
        Career.KNIGHT    -> 120
        Career.MERCENARY -> 70
        Career.THIEF     -> 60
        Career.ROGUE     -> 55
        Career.SCHOLAR   -> 80
        Career.MONK      -> 50
        Career.GUARD     -> 45
        Career.PHYSICIAN -> 90
        Career.CRAFTSMAN -> 50
        Career.ALCHEMIST -> 100
        else             -> 50
    }

    // ── Główna metoda ────────────────────────────────────────────────────────

    /**
     * Generuje [count] losowych, unikalnych bohaterów.
     * Każde wywołanie zwraca inną pulę — UUID gwarantuje unikalność ID.
     */
    fun generatePool(count: Int = 4): List<Hero> {
        return (1..count).map { generateHero() }
    }

    private fun generateHero(): Hero {
        val rng = Random.Default
        val career = recruitableCareers.random(rng)
        val isFemale = rng.nextBoolean()
        val firstName = if (isFemale) femaleFirstNames.random(rng) else maleFirstNames.random(rng)
        val lastName = lastNames.random(rng)
        val name = "$firstName $lastName"
        val age = (career.minAge + 4).coerceAtLeast(16) + rng.nextInt(12)

        val ranges = statRangesFor(career)
        fun roll(key: String): Int = ranges[key]!!.let { it.base + rng.nextInt(it.spread + 1) }

        val str = roll("str")
        val agi = roll("agi")
        val per = roll("per")
        val int = roll("int")
        val end = roll("end")
        val cha = roll("cha")
        val pie = roll("pie")
        val maxHp = end * 2 + 18 + rng.nextInt(8)

        val weapon = weaponByCareer[career]?.random(rng)
        val armor  = armorByCareer[career]?.random(rng)

        val skills = skillsByCareer[career]
                        ?.mapValues { (_, base) -> (base + rng.nextInt(11) - 5).coerceAtLeast(1) }
            ?.toMutableMap()
            ?: mutableMapOf()

        val portrait = portraitByCareer[career] ?: "port_knight"

        return Hero(
            id           = UUID.randomUUID().toString(),
            name         = name,
            age          = age,
            strength     = str,
            agility      = agi,
            perception   = per,
            intelligence = int,
            endurance    = end,
            charisma     = cha,
            piety        = pie,
            hp           = maxHp,
            maxHp        = maxHp,
            currentCareer = career,
            portraitRes  = portrait,
            skills       = skills,
            equipment    = mutableMapOf(
                "weapon" to weapon,
                "armor"  to armor,
                "helmet" to null,
                "shield" to null
            )
        )
    }
}
