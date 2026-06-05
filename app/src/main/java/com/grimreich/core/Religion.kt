package com.grimreich.core

// Typy kapliczek/przybytkow do mechaniki modlitwy w ReligionSystem.
// Trzymane tu, bo Religion.kt jest historycznym miejscem dla domeny religijnej w projekcie.
enum class ShrineType {
    CHAPEL, CATHEDRAL, ROADSIDE, MONASTERY, RUINS
}

// Prosty rekord przybytku — uzywany przez systemy/ekrany do opisu lokalizacji modlitwy.
data class Shrine(
    val id: String,
    val name: String,
    val type: ShrineType,
    val patronSaintId: String? = null,
    val faithBonus: Int = 1,
    val divineFavorBonus: Int = 1,
    val isDesecrated: Boolean = false,
    val locationId: String = ""
)

// Maly katalog przybytkow uzywany przez ekrany testowe (Saints/Prayer).
object ShrineCatalogue {
    private val shrines = listOf(
        Shrine("shrine_chapel_east", "Kaplica Wschodnia", ShrineType.CHAPEL, "s3"),
        Shrine("shrine_cathedral",   "Katedra sw. Michala", ShrineType.CATHEDRAL, "s1", faithBonus = 3, divineFavorBonus = 3),
        Shrine("shrine_wayside",     "Przydrozny krzyz", ShrineType.ROADSIDE),
        Shrine("shrine_monastery",   "Klasztor benedyktynow", ShrineType.MONASTERY, "s2", faithBonus = 2, divineFavorBonus = 2),
        Shrine("shrine_ruins",       "Zrujnowana kaplica", ShrineType.RUINS, isDesecrated = true)
    )

    fun all(): List<Shrine> = shrines
    fun get(id: String): Shrine? = shrines.firstOrNull { it.id == id }
}
