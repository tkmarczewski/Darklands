package com.grimreich.world

import javax.inject.Inject
import javax.inject.Singleton

data class CityData(
    val id: String,
    val name: String,
    val region: String,
    val phenomenon: String,
    val rulingFaction: String,
    val priceModifier: Float,
    val backgroundDrawable: String,
    val corruptedBackgroundDrawable: String? = null,
    val loreDescription: String,
    val primaryArtifact: String,
    val events: MutableList<String> = mutableListOf(),
    val prophet: String? = null,
    val marketStock: List<String> = emptyList(),
    val isQuestAccessLocked: Boolean = false // NOWE
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

    fun allIds(): Set<String> = cities.keys

    fun clear() {
        cities.clear()
    }

    fun seedCanonical() {
        if (cities.isNotEmpty()) return

        register(CityData(
            id = "wybrzeze_polnocne",
            name = "Wybrzeże Północne",
            region = "Boreas",
            phenomenon = "Mgła",
            rulingFaction = "church",
            priceModifier = 1.0f,
            backgroundDrawable = "bg_coast",
            loreDescription = "Miejsce, gdzie Mgła po raz pierwszy dotknęła paradygmatu.",
            primaryArtifact = "Kotwica Północy",
            prophet = "Aelion",
            marketStock = listOf("sword_short", "dagger_basic", "armor_leather_light", "pot_heal", "ing_herb")
        ))

        register(CityData(
            id = "twierdza_zelazna",
            name = "Twierdza Żelazna",
            region = "Północ",
            phenomenon = "Krew",
            rulingFaction = "military",
            priceModifier = 1.2f,
            backgroundDrawable = "bg_fortress",
            loreDescription = "Potężna forteca wykuta w skale, bastion przeciwko echa.",
            primaryArtifact = "Serce Ferrum",
            marketStock = listOf("sword_long", "mace_basic", "armor_chainmail", "pot_str", "ing_bone")
        ))

        register(CityData(
            id = "port_mglisty",
            name = "Port Mglisty",
            region = "Północ",
            phenomenon = "Mgła",
            rulingFaction = "merchants",
            priceModifier = 0.9f,
            backgroundDrawable = "bg_port",
            loreDescription = "Centrum handlu i przemytu esencji.",
            primaryArtifact = "Kompas Echa",
            marketStock = listOf("pot_agi", "ing_feather")
        ))

        register(CityData(
            id = "opactwo_ciszy",
            name = "Opactwa Ciszy",
            region = "Północ",
            phenomenon = "Odbicie",
            rulingFaction = "church",
            priceModifier = 1.1f,
            backgroundDrawable = "bg_abbey",
            loreDescription = "Miejsce medytacji i katalogowania echa przeszłości.",
            primaryArtifact = "Lustro Prawdy",
            marketStock = listOf("pot_sanity", "ing_blue_dust")
        ))

        register(CityData(
            id = "serce_krainy",
            name = "Serce Krainy",
            region = "Centrum",
            phenomenon = "Pęknięcie",
            rulingFaction = "scholars",
            priceModifier = 1.5f,
            backgroundDrawable = "bg_core",
            loreDescription = "Epicentrum kolapsu. Tutaj rzeczywistość jest najcieńsza.",
            primaryArtifact = "Czarna Kotwica",
            prophet = "Mira",
            marketStock = listOf("pot_mana", "ing_red_dust")
        ))
    }
}
