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
            description = "Symbol sprawiedliwości. Modlitwa wzmacnia cnotę."
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
