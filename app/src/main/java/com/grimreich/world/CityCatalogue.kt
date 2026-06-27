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
    val prophet: String? = null,
    val marketStock: List<String> = emptyList()
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
            loreDescription = """
                Kraina wiecznego poranka, gdzie granica między morzem a niebem rozmywa się w gęstej, mlecznej mgle. 
                Mieszkańcy Wybrzeża to potomkowie tych, którzy pierwsi ujrzeli Pęknięcie na horyzoncie. 
                Mówi się, że pod wodami zatoki wciąż biją dzwony zatopionych katedr, a ich dźwięk słychać tylko w snach. 
                Zakon Świtu pilnuje tutaj latarni, które nie świecą światłem, lecz Nadzieją.
            """.trimIndent(),
            prophet = "Aelion",
            marketStock = listOf("ing_herb", "pot_heal", "pot_sanity")
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
            loreDescription = """
                Niegdyś spichlerz Imperium, dziś stepy przesiąknięte rdzawym szkarłatem. 
                Ziemia na Równinach wydaje się pulsować w rytm niewidocznego serca. 
                Opowieści głoszą, że każde ziarno zboża, które tu wyrośnie, kosztowało kroplę krwi dawnego króla. 
                Wiatr niesie tu szepty poległych armii, a Inkwizycja patroluje trakty, szukając tych, których krew 'pamięta za dużo'.
            """.trimIndent(),
            prophet = "Xyrel",
            marketStock = listOf("ing_bone", "ing_red_dust", "pot_str")
        ))

        // 3. TWIERDZA ZAKONU
        register(CityData(
            id = "twierdza_zakonu",
            name = "Twierdza Zakonu",
            region = "East",
            phenomenon = "Wyrok",
            rulingFaction = "Inkwizycja",
            priceModifier = 1.1f,
            backgroundDrawable = "bg_location_order_fortress",
            corruptedBackgroundDrawable = "bg_corrupted_graveyard",
            loreDescription = """
                Monolityczna konstrukcja z czarnego bazaltu, która zdaje się wyrastać wprost z gniewu fundamentów świata. 
                To tutaj zapadają Wyroki, które decydują o tym, co w GrimReich jest jeszcze rzeczywiste, a co już jest błędem. 
                W murach Twierdzy cisza jest głośniejsza od krzyku. 
                Każdy kamień został tu pobłogosławiony przez Pierwszego Sędziego, by opierał się naporowi Drugiej Strony.
            """.trimIndent(),
            prophet = "Silentius",
            marketStock = listOf("pot_heal", "pot_mana")
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
            loreDescription = """
                Geograficzne i mistyczne centrum świata, gdzie jeziora są tak czyste, że pokazują nie to, co stoi nad brzegiem, lecz to, co kryje się w duszy. 
                W Sercu Krainy rzeczywistość jest elastyczna – odbicia w lustrach mogą wyjść na zewnątrz, jeśli nikt ich nie pilnuje. 
                Klasztor Milczenia strzeże Wielkiego Zwierciadła, w którym podobno widać projektantów tego świata, piszących nasze losy piórami z echa.
            """.trimIndent(),
            prophet = "Mira",
            marketStock = listOf("ing_blue_dust", "pot_sanity")
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
            loreDescription = """
                Cmentarzysko cywilizacji, która rzuciła wyzwanie Absolutowi. 
                Nad ruinami wiecznie wisi księżyc w pełni, rzucając trupioblade światło na zniszczone kolumnady. 
                Tutaj czas stanął w miejscu w momencie największej katastrofy. 
                Wędrowcy twierdzą, że cienie w Ruinach mają własne plany i potrafią kraść wspomnienia tym, którzy zbyt długo patrzą w blask Luny.
            """.trimIndent(),
            prophet = "Sereth",
            marketStock = listOf("ing_bone", "pot_heal")
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
            loreDescription = """
                Szczyty tak wysokie, że przebijają sklepienie rzeczywistości, i kopalnie tak głębokie, że dotykają fundamentów nicości. 
                W Górach grawitacja wydaje się mieć inne zasady – krew płynie wolniej, a grzechy ważą więcej. 
                Górnicy wydobywają tu nie tylko kruszec, lecz 'Gęstą Ciemność', z której wykuwa się broń zdolną ranić byty z Drugiej Strony.
            """.trimIndent(),
            prophet = "Ferrun",
            marketStock = listOf("ing_red_dust", "ing_yellow_dust")
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
            loreDescription = """
                Nieskończone morze traw, przecięte gigantyczną szczeliną, z której wydobywa się błękitny blask czystej informacji. 
                Na Pograniczu granice paradygmatu są najcieńsze. 
                Wioski są tu rzadkością, a ludzie, którzy tu zostali, często mówią w językach, których nikt nie rozumie – językach kodu i echa. 
                Noctyros, strażnik Pęknięcia, twierdzi, że stepy to tylko margines w wielkiej księdze.
            """.trimIndent(),
            prophet = "Noctyros",
            marketStock = listOf("ing_feather", "ing_blue_dust")
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
            loreDescription = """
                Terytorium, gdzie natura uległa całkowitej mutacji pod wpływem GrimReich. 
                Drzewa krwawią, zwierzęta mają ludzkie oczy, a deszcz pada w górę. 
                To strefa zero każdej nowej anomalii. 
                Prawo tu nie sięga, a jedyną walutą jest przetrwanie. 
                Mówi się, że w samym sercu Dzikich Ziem rośnie Drzewo Echa, które karmi się historiami tych, którzy nigdy z nich nie wrócili.
            """.trimIndent(),
            marketStock = listOf("ing_herb", "ing_feather", "ing_bone")
        ))
    }
}
