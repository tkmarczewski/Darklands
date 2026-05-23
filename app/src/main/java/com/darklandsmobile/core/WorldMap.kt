package com.darklandsmobile.core

// ==================== CITY DISTRICT ====================

enum class DistrictType {
    MARKET, CHURCH, INN, BLACKSMITH, ALCHEMIST, GUILDHALL, CASTLE, SLUMS, UNIVERSITY, PORT
}

data class CityDistrict(
    val id: String,
    val name: String,
    val type: DistrictType,
    val description: String = "",
    val isAvailable: Boolean = true
)

// ==================== WORLD MAP ====================

object WorldMap {
    data class Node(
        val id: String,
        val name: String,
        val region: String,
        val neighbors: List<String>,
        val districts: List<CityDistrict> = emptyList()
    )

    private val nodes = listOf(
        // ========== MAGDEBURG (start) ==========
        Node(
            "magdeburg", "Magdeburg", "town",
            listOf("road_mag_koln", "road_mag_frank", "road_mag_berlin"),
            listOf(
                CityDistrict("mag_market", "Targ", DistrictType.MARKET, "Kupcy i handlarze"),
                CityDistrict("mag_church", "Katedra", DistrictType.CHURCH, "Dom modlitwy"),
                CityDistrict("mag_inn", "Gospoda", DistrictType.INN, "Odpoczynek i plotki"),
                CityDistrict("mag_smith", "Kuźnia", DistrictType.BLACKSMITH, "Broń i zbroja"),
                CityDistrict("mag_alch", "Apteka", DistrictType.ALCHEMIST, "Sklep alchemiczny"),
                CityDistrict("mag_guild", "Gildia", DistrictType.GUILDHALL, "Kontrakty i zlecenia"),
                CityDistrict("mag_castle", "Zamek", DistrictType.CASTLE, "Siedziba władzy"),
                CityDistrict("mag_slums", "Przedmieścia", DistrictType.SLUMS, "Ciemne uliczki")
            )
        ),
        
        // ========== KÖLN (KOLONIA) ==========
        Node(
            "koln", "Köln", "town",
            listOf("road_mag_koln", "road_koln_frank", "road_koln_aachen"),
            listOf(
                CityDistrict("kol_market", "Wielki Targ", DistrictType.MARKET, "Najbogatszy targ Nadrenii"),
                CityDistrict("kol_cathedral", "Katedra", DistrictType.CHURCH, "Wspaniała katedra gotycka"),
                CityDistrict("kol_inn", "U Trzech Korali", DistrictType.INN, "Tłoczna gospoda"),
                CityDistrict("kol_smith", "Zbrojownia", DistrictType.BLACKSMITH, "Miecze kolońskie"),
                CityDistrict("kol_alch", "Dom Alchemika", DistrictType.ALCHEMIST, "Rzadkie składniki"),
                CityDistrict("kol_guild", "Gildia Kupiecka", DistrictType.GUILDHALL, "Potężna gildia"),
                CityDistrict("kol_univ", "Uniwersytet", DistrictType.UNIVERSITY, "Uczelnia teologiczna"),
                CityDistrict("kol_castle", "Ratusz", DistrictType.CASTLE, "Rada miejska"),
                CityDistrict("kol_slums", "Brudne Dzielnice", DistrictType.SLUMS, "Niebezpieczne zaułki")
            )
        ),
        
        // ========== NÜRNBERG (NORYMBERGA) ==========
        Node(
            "nurnberg", "Nürnberg", "town",
            listOf("road_nurn_frank", "road_nurn_praha", "road_nurn_augsburg"),
            listOf(
                CityDistrict("nur_market", "Hauptmarkt", DistrictType.MARKET, "Centrum handlu"),
                CityDistrict("nur_church", "Kościół św. Wawrzyńca", DistrictType.CHURCH, "Piękny kościół"),
                CityDistrict("nur_inn", "Bratwurst Glockl", DistrictType.INN, "Słynne kiełbaski"),
                CityDistrict("nur_smith", "Kuźnia Cesarza", DistrictType.BLACKSMITH, "Najlepsza zbroja"),
                CityDistrict("nur_alch", "Pracownia Mistrza", DistrictType.ALCHEMIST, "Wysokiej klasy mikstury"),
                CityDistrict("nur_guild", "Gildia Rzemieślników", DistrictType.GUILDHALL, "Cenione rzemiosło"),
                CityDistrict("nur_univ", "Akademia", DistrictType.UNIVERSITY, "Szkoła cesarska"),
                CityDistrict("nur_castle", "Zamek Cesarski", DistrictType.CASTLE, "Regalia Cesarskie"),
                CityDistrict("nur_slums", "Przedmieścia", DistrictType.SLUMS, "Dzielnica ubogich")
            )
        ),
        
        // ========== FRANKFURT ==========
        Node(
            "frankfurt", "Frankfurt am Main", "town",
            listOf("road_mag_frank", "road_koln_frank", "road_nurn_frank"),
            listOf(
                CityDistrict("fra_market", "Römerberg", DistrictType.MARKET, "Wielkie targi"),
                CityDistrict("fra_church", "Katedra Św. Bartłomieja", DistrictType.CHURCH, "Miejsce koronacji"),
                CityDistrict("fra_inn", "Złoty Orzeł", DistrictType.INN, "Elitarna gospoda"),
                CityDistrict("fra_smith", "Zbrojownia Miejska", DistrictType.BLACKSMITH, "Doskonała broń"),
                CityDistrict("fra_alch", "Apteka przy Rynku", DistrictType.ALCHEMIST, "Mikstury handlowe"),
                CityDistrict("fra_guild", "Gildia Bankierów", DistrictType.GUILDHALL, "Finansiści"),
                CityDistrict("fra_castle", "Römer", DistrictType.CASTLE, "Ratusz i sejm"),
                CityDistrict("fra_slums", "Port Rzeczny", DistrictType.SLUMS, "Złodzieje")
            )
        ),
        
        // ========== PRAHA (PRAGA) ==========
        Node(
            "praha", "Praha", "town",
            listOf("road_nurn_praha", "road_praha_wien", "road_praha_breslau"),
            listOf(
                CityDistrict("pra_market", "Staromestske Namesti", DistrictType.MARKET, "Staromiejski plac"),
                CityDistrict("pra_church", "Katedra Św. Wita", DistrictType.CHURCH, "Gotycki klejnot"),
                CityDistrict("pra_inn", "U Fleku", DistrictType.INN, "Słynne piwo"),
                CityDistrict("pra_smith", "Złota Uliczka", DistrictType.BLACKSMITH, "Rzemieślnicy"),
                CityDistrict("pra_alch", "Pracownia Alchemiczna", DistrictType.ALCHEMIST, "Tajemne formuły"),
                CityDistrict("pra_guild", "Gildia", DistrictType.GUILDHALL, "Kupcy"),
                CityDistrict("pra_univ", "Uniwersytet Karola", DistrictType.UNIVERSITY, "Najstarsza uczelnia"),
                CityDistrict("pra_castle", "Hrad", DistrictType.CASTLE, "Zamek królewski"),
                CityDistrict("pra_slums", "Żydowskie Getto", DistrictType.SLUMS, "Golem i kabała")
            )
        ),
        
        // ========== LÜBECK (LUBEKA) ==========
        Node(
            "lubeck", "Lübeck", "town",
            listOf("road_lubeck_hamburg", "road_lubeck_bremen"),
            listOf(
                CityDistrict("lub_market", "Targ Hanzy", DistrictType.MARKET, "Kupcy hanzeatyccy"),
                CityDistrict("lub_church", "Marienkirche", DistrictType.CHURCH, "Kościół ceglany"),
                CityDistrict("lub_inn", "Schiffergesellschaft", DistrictType.INN, "Żeglarze"),
                CityDistrict("lub_smith", "Zbrojownia", DistrictType.BLACKSMITH, "Broń żeglarska"),
                CityDistrict("lub_alch", "Apteka Morska", DistrictType.ALCHEMIST, "Zamorskie składniki"),
                CityDistrict("lub_guild", "Dom Hanzy", DistrictType.GUILDHALL, "Potęga Bałtyku"),
                CityDistrict("lub_port", "Port", DistrictType.PORT, "Doki handlowe"),
                CityDistrict("lub_castle", "Ratusz", DistrictType.CASTLE, "Władza kupiecka"),
                CityDistrict("lub_slums", "Dzielnica Portowa", DistrictType.SLUMS, "Szmuglerzy")
            )
        ),
        
        // ========== HAMBURG ==========
        Node(
            "hamburg", "Hamburg", "town",
            listOf("road_lubeck_hamburg", "road_hamburg_bremen"),
            listOf(
                CityDistrict("ham_market", "Rynek Rybny", DistrictType.MARKET, "Handel morski"),
                CityDistrict("ham_church", "Kościół Św. Michała", DistrictType.CHURCH, "Patron żeglarzy"),
                CityDistrict("ham_inn", "Pod Kotwicą", DistrictType.INN, "Opowieści marynarzy"),
                CityDistrict("ham_smith", "Stoczniowa Kuźnia", DistrictType.BLACKSMITH, "Ciężkie zbrojenia"),
                CityDistrict("ham_alch", "Pracownia Portowa", DistrictType.ALCHEMIST, "Egzotyki"),
                CityDistrict("ham_guild", "Gildia Stoczniowców", DistrictType.GUILDHALL, "Okręty"),
                CityDistrict("ham_port", "Port Hanzy", DistrictType.PORT, "Główny port"),
                CityDistrict("ham_castle", "Twierdza", DistrictType.CASTLE, "Fortyfikacje"),
                CityDistrict("ham_slums", "Nadbrzeże", DistrictType.SLUMS, "Biedota")
            )
        ),
        
        // ========== WIEN (WIEDEŃ) ==========
        Node(
            "wien", "Wien", "town",
            listOf("road_praha_wien", "road_wien_augsburg"),
            listOf(
                CityDistrict("wie_market", "Graben", DistrictType.MARKET, "Elegancki plac"),
                CityDistrict("wie_church", "Stephansdom", DistrictType.CHURCH, "Majestatyczna katedra"),
                CityDistrict("wie_inn", "Gospoda Cesarska", DistrictType.INN, "Luksus i intryga"),
                CityDistrict("wie_smith", "Cesarska Zbrojownia", DistrictType.BLACKSMITH, "Płatnerze Habsburgów"),
                CityDistrict("wie_alch", "Dwór Alchemika", DistrictType.ALCHEMIST, "Tajemne nauki"),
                CityDistrict("wie_guild", "Gildia Artystów", DistrictType.GUILDHALL, "Sztuka"),
                CityDistrict("wie_univ", "Uniwersytet", DistrictType.UNIVERSITY, "Uczelnia cesarska"),
                CityDistrict("wie_castle", "Hofburg", DistrictType.CASTLE, "Pałac cesarski"),
                CityDistrict("wie_slums", "Spittelberg", DistrictType.SLUMS, "Nielegalne obroty")
            )
        ),
        
        // ========== BRESLAU (WROCŁAW) ==========
        Node(
            "breslau", "Breslau", "town",
            listOf("road_praha_breslau", "road_breslau_berlin"),
            listOf(
                CityDistrict("wro_market", "Rynek", DistrictType.MARKET, "Śląski handel"),
                CityDistrict("wro_church", "Katedra", DistrictType.CHURCH, "Ostrow Tumski"),
                CityDistrict("wro_inn", "Karczma Piwna", DistrictType.INN, "Śląskie piwo"),
                CityDistrict("wro_smith", "Kuźnia", DistrictType.BLACKSMITH, "Ciężkie ostrza"),
                CityDistrict("wro_alch", "Pracownia Górnicza", DistrictType.ALCHEMIST, "Górskie minerały"),
                CityDistrict("wro_guild", "Gildia Górników", DistrictType.GUILDHALL, "Srebro"),
                CityDistrict("wro_univ", "Szkoła Katedralna", DistrictType.UNIVERSITY, "Nauki"),
                CityDistrict("wro_castle", "Zamek", DistrictType.CASTLE, "Władza książęca"),
                CityDistrict("wro_slums", "Nadodrze", DistrictType.SLUMS, "Biedna dzielnica")
            )
        ),
        
        // ========== AUGSBURG ==========
        Node(
            "augsburg", "Augsburg", "town",
            listOf("road_nurn_augsburg", "road_wien_augsburg"),
            listOf(
                CityDistrict("aug_market", "Rynek", DistrictType.MARKET, "Fuggerowie"),
                CityDistrict("aug_church", "Katedra", DistrictType.CHURCH, "Starożytna świątynia"),
                CityDistrict("aug_inn", "Gospoda", DistrictType.INN, "Bankierzy i kupcy"),
                CityDistrict("aug_smith", "Kuźnia", DistrictType.BLACKSMITH, "Zbroje szwabskie"),
                CityDistrict("aug_alch", "Apteka", DistrictType.ALCHEMIST, "Formuły"),
                CityDistrict("aug_guild", "Dom Fuggerów", DistrictType.GUILDHALL, "Imperium handlowe"),
                CityDistrict("aug_univ", "Szkoła", DistrictType.UNIVERSITY, "Nauki"),
                CityDistrict("aug_castle", "Ratusz", DistrictType.CASTLE, "Rada"),
                CityDistrict("aug_slums", "Przedmieścia", DistrictType.SLUMS, "Bieda")
            )
        ),
        
        // ========== DRESDEN (DREZNO) ==========
        Node(
            "dresden", "Dresden", "town",
            listOf("road_dresden_praha", "road_dresden_leipzig"),
            listOf(
                CityDistrict("dre_market", "Altmarkt", DistrictType.MARKET, "Targ saksoński"),
                CityDistrict("dre_church", "Frauenkirche", DistrictType.CHURCH, "Kościół Marii"),
                CityDistrict("dre_inn", "Gospoda", DistrictType.INN, "Saksońskie piwo"),
                CityDistrict("dre_smith", "Zbrojownia", DistrictType.BLACKSMITH, "Broń saksońska"),
                CityDistrict("dre_alch", "Apteka", DistrictType.ALCHEMIST, "Mikstury"),
                CityDistrict("dre_guild", "Gildia", DistrictType.GUILDHALL, "Rzemiosło"),
                CityDistrict("dre_castle", "Zamek", DistrictType.CASTLE, "Rezydencja książęca"),
                CityDistrict("dre_slums", "Dzielnica", DistrictType.SLUMS, "Ubodzy")
            )
        ),
        
        // ========== DROGI / ROADS ==========
        Node("road_mag_koln", "Trakt Magdeburg-Köln", "road", listOf("magdeburg", "koln")),
        Node("road_mag_frank", "Trakt Magdeburg-Frankfurt", "road", listOf("magdeburg", "frankfurt")),
        Node("road_mag_berlin", "Trakt Magdeburg-Berlin", "road", listOf("magdeburg")),
        Node("road_koln_frank", "Trakt Köln-Frankfurt", "road", listOf("koln", "frankfurt")),
        Node("road_koln_aachen", "Trakt Köln-Aachen", "road", listOf("koln")),
        Node("road_nurn_frank", "Trakt Nürnberg-Frankfurt", "road", listOf("nurnberg", "frankfurt")),
        Node("road_nurn_praha", "Trakt Nürnberg-Praha", "road", listOf("nurnberg", "praha")),
        Node("road_nurn_augsburg", "Trakt Nürnberg-Augsburg", "road", listOf("nurnberg", "augsburg")),
        Node("road_praha_wien", "Trakt Praha-Wien", "road", listOf("praha", "wien")),
        Node("road_praha_breslau", "Trakt Praha-Breslau", "road", listOf("praha", "breslau")),
        Node("road_wien_augsburg", "Trakt Wien-Augsburg", "road", listOf("wien", "augsburg")),
        Node("road_breslau_berlin", "Trakt Breslau-Berlin", "road", listOf("breslau")),
        Node("road_lubeck_hamburg", "Trakt Lübeck-Hamburg", "road", listOf("lubeck", "hamburg")),
        Node("road_lubeck_bremen", "Trakt Lübeck-Bremen", "road", listOf("lubeck")),
        Node("road_hamburg_bremen", "Trakt Hamburg-Bremen", "road", listOf("hamburg")),
        Node("road_dresden_praha", "Trakt Dresden-Praha", "road", listOf("dresden", "praha")),
        Node("road_dresden_leipzig", "Trakt Dresden-Leipzig", "road", listOf("dresden")),
        
        // ========== LASY / FORESTS ==========
        Node("forest_deep", "Głęboki Las", "forest", listOf("road_mag_frank")),
        Node("forest_dark", "Mroczna Puszcza", "forest", listOf("road_nurn_praha")),
        Node("forest_haunted", "Las Nawiedzonych", "forest", listOf("road_praha_wien")),
        Node("forest_bohemia", "Lasy Czeskie", "forest", listOf("road_dresden_praha"))
    )

    fun getNode(id: String): Node? = nodes.firstOrNull { it.id == id }

    fun getAllCities(): List<Node> = nodes.filter { it.region == "town" }

    fun getAllRoads(): List<Node> = nodes.filter { it.region == "road" }

    fun getAllForests(): List<Node> = nodes.filter { it.region == "forest" }

    fun getNeighbors(nodeId: String): List<Node> {
        val node = getNode(nodeId) ?: return emptyList()
        return node.neighbors.mapNotNull { getNode(it) }
    }

    fun getCityDistricts(cityId: String): List<CityDistrict> =
        getNode(cityId)?.districts ?: emptyList()
        
    fun getDistrictById(districtId: String): CityDistrict? {
        return getAllCities()
            .flatMap { it.districts }
            .firstOrNull { it.id == districtId }
    }
}
