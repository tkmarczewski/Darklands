package com.grimreich.grimreich.v1

data class GrimRegionProfile(val regionName: String, val dominantPhenomenon: String, val rulingFaction: String, val bossName: String, val ritualName: String, val ecosystem: String, val endingBias: String, val concept: String)
data class GrimFactionProfile(val factionName: String, val ethos: String, val method: String, val enemyOf: String, val regionFocus: String)
data class GrimNpcProfile(val npcName: String, val archetype: String, val regionName: String, val factionName: String, val phenomenon: String, val role: String, val fateHook: String)
data class GrimArtifactProfile(val name: String, val concept: String, val ability: String)

object GrimLoreCatalogues {
    val regions = listOf(
        GrimRegionProfile("Wybrzeże Północne", "Mgła", "Zakon Świtu", "Prorok Morskiej Mgły", "Rytuał Trzech Faleń", "zimne klify, porty, wraki, latarnie", "wzmacnia_zakonczenie_mgly", "Zapomnienie jako forma ochrony przed bólem istnienia"),
        GrimRegionProfile("Równiny Koronne", "Krew", "Gildia", "Awatar Krwi", "Rytuał Czerwonej Żniwy", "urodzajne pola, huty, kanały krwi", "wzmacnia_zakonczenie_krwi", "Istnienie poprzez ból"),
        GrimRegionProfile("Serce Krainy", "Odbicie", "Trybunał", "Lustrzany Król", "Rytuał Siedmiu Zwierciadeł", "miasto-katedra, sale sądowe, archiwa", "wzmacnia_zakonczenie_lustra", "Prawda ontologiczna"),
        GrimRegionProfile("Południowe Ruiny", "Pełnia", "Zakon Świtu", "Złoty Strażnik", "Rytuał Zszycia Dnia", "ruiny świątyń, popiół, echa hymnów", "zamyka_lub_lamie_odkupienie", "Nadświadomość"),
        GrimRegionProfile("Góry Południowe", "Absolut", "Trybunał", "Awatar Pełni", "Rytuał Ostatniej Wysokości", "lodowe przełęcze, twierdze, cisza", "wzmacnia_absolut", "Ciężar materii"),
        GrimRegionProfile("Pogranicze Stepowe", "Rozdarcie", "Bractwo Cienia", "Demiurg Symetrii", "Rytuał Pustego Trotu", "step, kurhany, szlaki najazdów", "otwiera_zakonczenie_rozdarcia", "Portal do nicości"),
        GrimRegionProfile("Ziemie Dzikie", "Mgła", "Brak stałej władzy", "Bestia Rozdarcia", "Rytuał Pierwotnego Kręgu", "las, łowy, głód, stare runy", "otwiera_zakonczenie_absolutu", "Czysty chaos")
    )
    val factions = listOf(
        GrimFactionProfile("Zakon Świtu", "odkupienie przez rytuał i pamięć", "ochrona, pielgrzymki, pieczęcie", "Rozdarcie", "Wybrzeże Północne / Południowe Ruiny"),
        GrimFactionProfile("Trybunał", "porządek, prawda, symetria", "procesy, archiwa, wyroki", "Mgła i chaos pamięci", "Serce Krainy / Góry Południowe"),
        GrimFactionProfile("Gildia", "przetrwanie przez kontrolę ciała i zasobów", "handel, mutacje, ekonomia", "Pełnia i Absolut", "Równiny Koronne"),
        GrimFactionProfile("Bractwo Cienia", "wolność przez pęknięcie świata", "sabotaż, najazdy, infiltracja", "Pełnia", "Pogranicze Stepowe / Ziemie Dzikie")
    )
    val npcs = listOf(
        GrimNpcProfile("Aelion", "Prorok Mgły", "Wybrzeże Północne", "Zakon Świtu", "Mgła", "prorok i przewodnik", "widzi trzy przyszłości jednocześnie"),
        GrimNpcProfile("Xyrel", "Herold Krwi", "Równiny Koronne", "Gildia", "Krew", "dowódca rytuałów i żniw", "nigdy nie kończy tej samej wojny"),
        GrimNpcProfile("Sereth", "Strażnik Pełni", "Południowe Ruiny", "Zakon Świtu", "Pełnia", "obrońca zszywania świata", "jego przysięga leczy i rani"),
        GrimNpcProfile("Mira z Lustra", "Sędzia Odbicia", "Serce Krainy", "Trybunał", "Odbicie", "osądzający echo duszy", "każde odbicie mówi inną prawdę"),
        GrimNpcProfile("Rhovan", "Jeździec Rozdarcia", "Pogranicze Stepowe", "Bractwo Cienia", "Rozdarcie", "najemnik szczeliny", "nie umie wrócić tą samą drogą"),
        GrimNpcProfile("Helga von Nebel", "Łowczyni Ziem Dzikich", "Ziemie Dzikie", "Brak stałej władzy", "Mgła", "łowczyni bossów i znaków", "zna imiona wszystkich upiorów")
    )
    val artifacts = listOf(
        GrimArtifactProfile("Kielich Zapomnienia", "Wymazuje tożsamość w zamian za pokój", "Redukuje poziom chaosu"),
        GrimArtifactProfile("Lustro Absolutu", "Pozwala zajrzeć za kurtynę materii", "Ujawnia ukryte cele NPC"),
        GrimArtifactProfile("Maska Sereth", "Chroni przed oślepieniem prawdą", "Odporność na debuffy psychiczne")
    )
}
