package com.darklandsmobile.core

// ==================== SAINT ====================

enum class SaintDomain {
    HEALING, WAR, WISDOM, MERCY, JUSTICE, NATURE, DEATH
}

data class Saint(
    val id: String,
    val name: String,
    val domain: SaintDomain,
    val prayerBonus: Int = 1,
    val description: String = ""
)

object SaintCatalogue {
    val saints = listOf(
        Saint(
            id = "st_michael",
            name = "Archanioł Michał",
            domain = SaintDomain.WAR,
            prayerBonus = 2,
            description = "Opiekun wojowników. Modlitwa do niego wzmacnia w walce."
        ),
        Saint(
            id = "st_raphael",
            name = "Archanioł Rafał",
            domain = SaintDomain.HEALING,
            prayerBonus = 2,
            description = "Uzdrowiciel. Modlitwa przyspiesza leczenie ran."
        ),
        Saint(
            id = "st_thomas",
            name = "Santo Tomasz",
            domain = SaintDomain.WISDOM,
            prayerBonus = 1,
            description = "Mędrzec i teolog. Modlitwa zwiększa zrozumienie świata."
        ),
        Saint(
            id = "st_mary",
            name = "Najświętsza Maria Panna",
            domain = SaintDomain.MERCY,
            prayerBonus = 3,
            description = "Orędowniczka. Modlitwa daje szczególne wstawiennictwo."
        ),
        Saint(
            id = "st_george",
            name = "Jerzy Męczennik",
            domain = SaintDomain.JUSTICE,
            prayerBonus = 1,
            description = "Symbol sprawiedliwości. Modlitwa wzmacnia cnotę.",
        Saint(
            id = "st_agnes",
            name = "Święta Agnieszka",
            domain = SaintDomain.MERCY,
            prayerBonus = 1,
            description = "Patronka czystości i dziewic."
        ),
        Saint(
            id = "st_agatha",
            name = "Święta Agata",
            domain = SaintDomain.HEALING,
            prayerBonus = 1,
            description = "Uzdrowicielka chorych."
        ),
        Saint(
            id = "st_anthony",
            name = "Święty Antoni",
            domain = SaintDomain.WISDOM,
            prayerBonus = 2,
            description = "Nauczyciel i patron rzeczy zaginionych."
        ),
        Saint(
            id = "st_barbara",
            name = "Święta Barbara",
            domain = SaintDomain.JUSTICE,
            prayerBonus = 1,
            description = "Patronka górników i ochrony przed nagłą śmiercią."
        ),
        Saint(
            id = "st_benedict",
            name = "Święty Benedykt",
            domain = SaintDomain.WISDOM,
            prayerBonus = 2,
            description = "Założyciel zakonu benedyktynów."
        ),
        Saint(
            id = "st_bernard",
            name = "Święty Bernard",
            domain = SaintDomain.HEALING,
            prayerBonus = 1,
            description = "Uzdrowiciel i opiekun podróżnych."
        ),
        Saint(
            id = "st_catherine",
            name = "Święta Katarzyna",
            domain = SaintDomain.WISDOM,
            prayerBonus = 2,
            description = "Patronka filozofów i uczonych."
        ),
        Saint(
            id = "st_christopher",
            name = "Święty Krzysztof",
            domain = SaintDomain.NATURE,
            prayerBonus = 1,
            description = "Patron podróżnych i przewoźników."
        ),
        Saint(
            id = "st_clare",
            name = "Święta Klara",
            domain = SaintDomain.MERCY,
            prayerBonus = 1,
            description = "Założycielka zakonu klarysek."
        ),
        Saint(
            id = "st_cosmas",
            name = "Święty Kosma",
            domain = SaintDomain.HEALING,
            prayerBonus = 2,
            description = "Lekarz i uzdrowiciel."
        ),
        Saint(
            id = "st_damian",
            name = "Święty Damian",
            domain = SaintDomain.HEALING,
            prayerBonus = 2,
            description = "Lekarz i uzdrowiciel, brat Kosmy."
        ),
        Saint(
            id = "st_dominic",
            name = "Święty Dominik",
            domain = SaintDomain.JUSTICE,
            prayerBonus = 1,
            description = "Założyciel zakonu dominikanów."
        ),
        Saint(
            id = "st_dorothy",
            name = "Święta Dorota",
            domain = SaintDomain.NATURE,
            prayerBonus = 1,
            description = "Patronka ogrodników."
        ),
        Saint(
            id = "st_elizabeth",
            name = "Święta Elżbieta",
            domain = SaintDomain.MERCY,
            prayerBonus = 2,
            description = "Opiekunka ubogich i chorych."
        ),
        Saint(
            id = "st_francis",
            name = "Święty Franciszek",
            domain = SaintDomain.NATURE,
            prayerBonus = 2,
            description = "Patron zwierząt i natury."
        ),
        Saint(
            id = "st_helena",
            name = "Święta Helena",
            domain = SaintDomain.JUSTICE,
            prayerBonus = 1,
            description = "Odkrywczyni Krzyża Świętego."
        ),
        Saint(
            id = "st_john",
            name = "Święty Jan",
            domain = SaintDomain.WISDOM,
            prayerBonus = 2,
            description = "Ewangelista i apostoł."
        ),
        Saint(
            id = "st_lawrence",
            name = "Święty Wawrzyniec",
            domain = SaintDomain.JUSTICE,
            prayerBonus = 1,
            description = "Męczennik i diakon."
        ),
        Saint(
            id = "st_leonard",
            name = "Święty Leonard",
            domain = SaintDomain.MERCY,
            prayerBonus = 1,
            description = "Patron więźniów."
        ),
        Saint(
            id = "st_lucy",
            name = "Święta Łucja",
            domain = SaintDomain.HEALING,
            prayerBonus = 1,
            description = "Patronka wzroku."
        ),
        Saint(
            id = "st_margaret",
            name = "Święta Małgorzata",
            domain = SaintDomain.MERCY,
            prayerBonus = 1,
            description = "Patronka kobiet w ciąży."
        ),
        Saint(
            id = "st_martin",
            name = "Święty Marcin",
            domain = SaintDomain.WAR,
            prayerBonus = 1,
            description = "Żołnierz i biskup."
        ),
        Saint(
            id = "st_nicholas",
            name = "Święty Mikołaj",
            domain = SaintDomain.MERCY,
            prayerBonus = 2,
            description = "Patron dzieci i obdarowujący."
        ),
        Saint(
            id = "st_paul",
            name = "Święty Paweł",
            domain = SaintDomain.WISDOM,
            prayerBonus = 2,
            description = "Apostoł narodów."
        ),
        Saint(
            id = "st_sebastian",
            name = "Święty Sebastian",
            domain = SaintDomain.WAR,
            prayerBonus = 1,
            description = "Żołnierz i męczennik."
        )
        )
    )

    fun findById(id: String) = saints.firstOrNull { it.id == id }
    fun findByDomain(domain: SaintDomain) = saints.filter { it.domain == domain }
}

// ==================== SHRINE ====================

enum class ShrineType {
    CHAPEL, CATHEDRAL, WAYSIDE_CROSS, MONASTERY, RUINS
}

data class Shrine(
    val id: String,
    val name: String,
    val type: ShrineType,
    val patronSaintId: String?,
    val faithBonus: Int = 1,
    val divineFavorBonus: Int = 1,
    val isDesecrated: Boolean = false,
    val locationId: String = ""
)

object ShrineCatalogue {
    val shrines = listOf(
        Shrine(
            id = "shrine_chapel_east",
            name = "Kaplica Wschodnia",
            type = ShrineType.CHAPEL,
            patronSaintId = "st_mary",
            faithBonus = 1,
            divineFavorBonus = 2
        ),
        Shrine(
            id = "shrine_cathedral",
            name = "Katedra św. Michała",
            type = ShrineType.CATHEDRAL,
            patronSaintId = "st_michael",
            faithBonus = 3,
            divineFavorBonus = 3
        ),
        Shrine(
            id = "shrine_wayside",
            name = "Przydrożny Krzyż",
            type = ShrineType.WAYSIDE_CROSS,
            patronSaintId = null,
            faithBonus = 1,
            divineFavorBonus = 1
        ),
        Shrine(
            id = "shrine_monastery",
            name = "Klasztor Bernardynów",
            type = ShrineType.MONASTERY,
            patronSaintId = "st_thomas",
            faithBonus = 2,
            divineFavorBonus = 2
        ),
        Shrine(
            id = "shrine_ruins",
            name = "Zrujnowana Kaplica",
            type = ShrineType.RUINS,
            patronSaintId = null,
            faithBonus = 0,
            divineFavorBonus = 1,
            isDesecrated = true
        )
    )

    fun findById(id: String) = shrines.firstOrNull { it.id == id }
    fun findByLocation(locationId: String) = shrines.filter { it.locationId == locationId }
}

// ==================== DIVINE FAVOR ====================

data class DivineFavorState(
    var favor: Int = 0,
    var patronSaintId: String? = null,
    var blessings: MutableList<String> = mutableListOf(),
    var curses: MutableList<String> = mutableListOf()
)

object DivineFavorSystem {

    fun pray(state: DivineFavorState, saint: Saint, faith: Int): String {
        val bonus = saint.prayerBonus + (faith / 3)
        state.favor += bonus
        state.favor = state.favor.coerceIn(-20, 20)

        if (state.patronSaintId == null) {
            state.patronSaintId = saint.id
        }

        return when (saint.domain) {
            SaintDomain.HEALING -> {
                state.blessings.add("healing_prayer")
                "${saint.name} usłyszał. +$bonus łaski. Rany goją się szybciej."
            }
            SaintDomain.WAR -> {
                state.blessings.add("war_prayer")
                "${saint.name} wzmocnił twoje ramię. +$bonus łaski."
            }
            SaintDomain.WISDOM -> {
                state.blessings.add("wisdom_prayer")
                "${saint.name} oświetlił twój umysł. +$bonus łaski."
            }
            SaintDomain.MERCY -> {
                state.blessings.add("mercy_prayer")
                "${saint.name} wstawiła się za tobą. +$bonus łaski."
            }
            SaintDomain.JUSTICE -> {
                state.blessings.add("justice_prayer")
                "${saint.name} umocnił twoją cnotę. +$bonus łaski."
            }
            else -> "${saint.name} usłyszał twoją modlitwę. +$bonus łaski."
        }
    }

    fun visitShrine(state: DivineFavorState, shrine: Shrine, faith: Int): String {
        if (shrine.isDesecrated) {
            state.favor -= 1
            return "${shrine.name} jest znieważona. Tracisz 1 łaskę."
        }
        state.favor += shrine.divineFavorBonus
        state.favor = state.favor.coerceIn(-20, 20)

        val patron = shrine.patronSaintId?.let { SaintCatalogue.findById(it) }
        return if (patron != null) {
            "Odwiedziłeś ${shrine.name} (opiekun: ${patron.name}). +${shrine.divineFavorBonus} łaski."
        } else {
            "Odwiedziłeś ${shrine.name}. +${shrine.divineFavorBonus} łaski."
        }
    }

    fun hasBlessing(state: DivineFavorState, blessing: String) =
        state.blessings.contains(blessing)

    fun favorDescription(favor: Int): String = when {
        favor >= 15 -> "Łaska Boża"
        favor >= 8 -> "Zbłogosławiony"
        favor >= 3 -> "Pobożny"
        favor >= 0 -> "Neutralny"
        favor >= -5 -> "Podejrzany"
        favor >= -10 -> "Grzesznik"
        else -> "Klątwa"
    }
}


// ==================== VIRTUE SYSTEM ====================

object VirtueSystem {

    // Virtue score (0-100) influences DivineFavor passively each day
    // High virtue boosts favor with JUSTICE/MERCY saints
    // Low virtue (sin) reduces favor and adds curses

    fun applyVirtueToDivineFavor(virtue: Int, state: DivineFavorState): String {
        val delta = when {
            virtue >= 80 -> 2   // Very virtuous: passive favor gain
            virtue >= 60 -> 1   // Virtuous
            virtue >= 40 -> 0   // Neutral
            virtue >= 20 -> -1  // Sinful: passive favor loss
            else         -> -2  // Very sinful: strong favor penalty
        }
        if (delta != 0) {
            state.favor = (state.favor + delta).coerceIn(-20, 20)
        }
        return when {
            delta > 0  -> "Twoja cnota przynosi lask\u0119 Boza. (+$delta)"
            delta < 0  -> "Twoje grzechy oddalaja cie od Boga. ($delta)"
            else       -> "Twoja cnota jest neutralna."
        }
    }

    fun virtueDescription(virtue: Int): String = when {
        virtue >= 90 -> "Swiety"
        virtue >= 70 -> "Prawy"
        virtue >= 50 -> "Uczciwy"
        virtue >= 30 -> "Watpliwy"
        virtue >= 10 -> "Grzesznik"
        else         -> "Potepieniec"
    }

    // Called when hero prays to a JUSTICE or MERCY saint
    // virtue score improves on successful prayer
    fun prayerBoostVirtue(currentVirtue: Int, saintDomain: SaintDomain): Int {
        return if (saintDomain == SaintDomain.JUSTICE || saintDomain == SaintDomain.MERCY) {
            (currentVirtue + 3).coerceAtMost(100)
        } else {
            currentVirtue
        }
    }

    // Called when hero commits a sinful act (theft, murder, desecration)
    fun sinPenalty(currentVirtue: Int, severity: Int = 5): Int =
        (currentVirtue - severity).coerceAtLeast(0)
}
