package com.grimreich.world

/**
 * Central city and region catalogue for GrimReich.
 * Synchronized with canonical ontological lore (Chapters I-VIII).
 */
data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val phenomenon: String,
    val rulingFaction: String,
    val priceModifier: Float,
    val events: MutableList<String> = mutableListOf(),
    val prophet: String? = null
)

object CityCatalogue {
    const val startingCityId = "wybrzeze_polnocne"
    private val cities = linkedMapOf<String, CityData>()

    fun register(city: CityData) {
        cities[city.id] = city
    }

    fun get(id: String): CityData? = cities[id]

    fun all(): List<CityData> = cities.values.toList()

    fun clear() = cities.clear()

    fun seedCanonical() {
        if (cities.isNotEmpty()) return

        // 1. WYBRZEŻE PÓŁNOCNE (Prorok Aelion | Pamięć i Mgła)
        register(CityData(
            id = "wybrzeze_polnocne",
            name = "Wybrzeże Północne",
            region = "North",
            phenomenon = "Mgła",
            rulingFaction = "Zakon Świtu",
            priceModifier = 1.0f,
            prophet = "Aelion",
            events = mutableListOf("port_mrozny", "zatoka_piracka", "latarnia_switu")
        ))

        // 2. RÓWNINY KORONNE (Herold Xyrel | Krew i Wojna)
        register(CityData(
            id = "rowniny_koronne",
            name = "Równiny Koronne",
            region = "East",
            phenomenon = "Krew",
            rulingFaction = "Twierdza Zakonu",
            priceModifier = 0.9f,
            prophet = "Xyrel",
            events = mutableListOf("miasto_korony", "cmentarzysko_krolow")
        ))

        // 3. SERCE KRAINY (Sędzia Mira | Prawda i Odbicia)
        register(CityData(
            id = "serce_krainy",
            name = "Serce Krainy",
            region = "Central",
            phenomenon = "Odbicie",
            rulingFaction = "Klasztor Milczenia",
            priceModifier = 1.2f,
            prophet = "Mira",
            events = mutableListOf("dwor_zloty")
        ))

        // 4. POŁUDNIOWE RUINY (Strażnik Sereth | Pełnia i Światło)
        register(CityData(
            id = "poludniowe_ruiny",
            name = "Południowe Ruiny",
            region = "South",
            phenomenon = "Pełnia",
            rulingFaction = "Zakon Świtu",
            priceModifier = 1.05f,
            prophet = "Sereth",
            events = mutableListOf("zlote_ruiny", "spalona_wies")
        ))

        // 5. GÓRY POŁUDNIOWE (Ferrun | Głębia i Metal)
        register(CityData(
            id = "gory_poludniowe",
            name = "Góry Południowe",
            region = "Far South",
            phenomenon = "Głębia",
            rulingFaction = "Kopalnia Żelaza",
            priceModifier = 1.3f,
            prophet = "Ferrun",
            events = mutableListOf("czerwona_przelecz")
        ))

        // 6. POGRANICZE STEPOWE (Noctyros | Pęknięcie i Cień)
        register(CityData(
            id = "pogranicze_stepowe",
            name = "Pogranicze Stepowe",
            region = "West",
            phenomenon = "Pęknięcie",
            rulingFaction = "Ruiny Czarnej Paszczy",
            priceModifier = 1.1f,
            prophet = "Noctyros",
            events = mutableListOf("kamienna_przystan")
        ))

        // 7. ZIEMIE DZIKIE (Naturalne)
        register(CityData(
            id = "ziemie_dzikie",
            name = "Ziemie Dzikie",
            region = "Untamed",
            phenomenon = "Anomalia",
            rulingFaction = "Brak",
            priceModifier = 0.8f,
            events = mutableListOf("las_cieni", "bagna_szeptow")
        ))
    }
}
