package com.darklandsmobile.grimreich.v1

object GrimRegionCatalogue {
    val coastalNorth = RegionConsciousness("Wybrzeże Północne", "mgła_pamięci", "krew_fal", "odbicie_latarnie", "niepokój", listOf("stare_żagle", "burza"), listOf("widma", "przypływ"), "odpływ", "sztorm")
    val heartland = RegionConsciousness("Serce Krainy", "mgła_centrum", "krew_rdzeń", "odbicie_zwierciadło", "kontrola", listOf("tron", "rynek"), listOf("dyscyplina", "intryga"), "stabilizacja", "koronacja")
    val crownPlains = RegionConsciousness("Równiny Koronne", "mgła_stepu", "krew_jeźdźców", "odbicie_banner", "duma", listOf("szlak", "kurhan"), listOf("najazd", "warta"), "ekspansja", "wojna")
    val steppeBorder = RegionConsciousness("Pogranicze Stepowe", "mgła_horyzontu", "krew_pylu", "odbicie_wichru", "czujność", listOf("droga", "posterunek"), listOf("rajdy", "czaty"), "granica", "pęknięcie")
    val southernRuins = RegionConsciousness("Południowe Ruiny", "mgła_gruzu", "krew_kamienia", "odbicie_echa", "żałoba", listOf("ruina", "studnia"), listOf("cienie", "zapadanie"), "osunięcie", "upadek")
    val southernMountains = RegionConsciousness("Góry Południowe", "mgła_szczytów", "krew_rudy", "odbicie_lodu", "duma", listOf("grań", "kopalnia"), listOf("lawina", "echa"), "izolacja", "cisza")
    val wildLands = RegionConsciousness("Ziemie Dzikie", "mgła_dziczy", "krew_zwierząt", "odbicie_krzaków", "szał", listOf("puszcza", "bór"), listOf("łowy", "wilki"), "nieokiełznanie", "pierwotność")
    val all = listOf(coastalNorth, heartland, crownPlains, steppeBorder, southernRuins, southernMountains, wildLands)
}
