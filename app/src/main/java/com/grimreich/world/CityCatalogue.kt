package com.grimreich.world

import javax.inject.Inject
import javax.inject.Singleton

data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val phenomenon: String,
    val rulingFaction: String = "Neutralna",
    val priceModifier: Float = 1.0f,
    val backgroundDrawable: String = "bg_region_north_coast",
    val corruptedBackgroundDrawable: String? = null,
    val loreDescription: String = "",
    val primaryArtifact: String = "",
    val events: MutableList<String> = mutableListOf(),
    val prophet: String? = null
)

@Singleton
class CityCatalogue @Inject constructor() {
    val startingCityId = "wybrzeze_polnocne"
    private val cities = LinkedHashMap<String, CityData>()

    fun register(city: CityData) {
        cities[city.id] = city
    }

    fun get(id: String?): CityData? = cities[id]

    fun all(): List<CityData> = cities.values.toList()

    fun clear() {
        cities.clear()
    }

    fun seedCanonical() {
        if (cities.isNotEmpty()) return

        // 1. WYBRZEŻE PÓŁNOCNE
        register(CityData(
            id = "wybrzeze_polnocne",
            name = "Wybrzeże Północne",
            region = "North",
            phenomenon = "Mgła",
            rulingFaction = "Zakon Świtu",
            priceModifier = 1.0f,
            backgroundDrawable = "bg_region_north_coast",
            corruptedBackgroundDrawable = "bg_corrupted_coast",
            loreDescription = "Miejsce, gdzie rzeczywistość miesza się z sennym oparem.",
            prophet = "Aelion"
        ))

        // 2. RÓWNINY KORONNE
        register(CityData(
            id = "rowniny_koronne",
            name = "Równiny Koronne",
            region = "East",
            phenomenon = "Krew",
            rulingFaction = "Zakon Świtu",
            priceModifier = 0.9f,
            backgroundDrawable = "bg_region_crown_plains",
            corruptedBackgroundDrawable = "bg_corrupted_village",
            loreDescription = "Ziemia przesiąknięta szkarłatem.",
            prophet = "Xyrel"
        ))

        // 3. TWIERDZA ZAKONU (Tribunal Canonical)
        register(CityData(
            id = "twierdza_zakonu",
            name = "Twierdza Zakonu",
            region = "East",
            phenomenon = "Wyrok",
            rulingFaction = "Inkwizycja",
            priceModifier = 1.1f,
            backgroundDrawable = "bg_location_order_fortress",
            corruptedBackgroundDrawable = "bg_corrupted_graveyard",
            loreDescription = "Serce sprawiedliwości i Trybunału.",
            prophet = "Silentius"
        ))

        // 4. SERCE KRAINY
        register(CityData(
            id = "serce_krainy",
            name = "Serce Krainy",
            region = "Central",
            phenomenon = "Odbicie",
            rulingFaction = "Klasztor Milczenia",
            priceModifier = 1.2f,
            backgroundDrawable = "bg_region_heartland",
            corruptedBackgroundDrawable = "bg_corrupted_swamp",
            loreDescription = "Kraina luster i lśniących jezior.",
            prophet = "Mira"
        ))

        // 5. POŁUDNIOWE RUINY
        register(CityData(
            id = "poludniowe_ruiny",
            name = "Południowe Ruiny",
            region = "South",
            phenomenon = "Pełnia",
            rulingFaction = "Zakon Świtu",
            priceModifier = 1.05f,
            backgroundDrawable = "bg_region_south_ruins",
            loreDescription = "Wieczna jasność księżyca w pełni.",
            prophet = "Sereth"
        ))

        // 6. GÓRY POŁUDNIOWE
        register(CityData(
            id = "gory_poludniowe",
            name = "Góry Południowe",
            region = "Far South",
            phenomenon = "Głębia",
            rulingFaction = "Kopalnia Żelaza",
            priceModifier = 1.3f,
            backgroundDrawable = "bg_region_south_mountains",
            loreDescription = "Ciężkie góry, gdzie grawitacja wydaje się silniejsza.",
            prophet = "Ferrun"
        ))

        // 7. POGRANICZE STEPOWE
        register(CityData(
            id = "pogranicze_stepowe",
            name = "Pogranicze Stepowe",
            region = "West",
            phenomenon = "Pęknięcie",
            rulingFaction = "Ruiny Czarnej Paszczy",
            priceModifier = 1.1f,
            backgroundDrawable = "bg_region_steppe",
            loreDescription = "Przez te stepy przebiega Wielkie Pęknięcie.",
            prophet = "Noctyros"
        ))

        // 8. ZIEMIE DZIKIE
        register(CityData(
            id = "ziemie_dzikie",
            name = "Ziemie Dzikie",
            region = "Untamed",
            phenomenon = "Anomalia",
            rulingFaction = "Brak",
            priceModifier = 0.8f,
            backgroundDrawable = "bg_region_wild_lands",
            loreDescription = "Miejsce, gdzie natura oszalała."
        ))
    }
}
