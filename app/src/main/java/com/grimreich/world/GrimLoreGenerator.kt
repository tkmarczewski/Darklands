package com.grimreich.world

import kotlin.random.Random

object GrimLoreGenerator {
    private val prefixes = listOf("Mroczne", "Zapomniane", "Przeklęte", "Krwawe", "Cieniste", "Zniszczone")
    private val adjectives = listOf("pachnące siarką", "spowite mgłą", "pełne szeptów", "skalne", "starożytne")
    
    fun generateDescription(type: LocationType, random: Random): String {
        val atmosphere = when (type) {
            LocationType.RUINS -> "Powietrze tutaj jest ciężkie od pyłu i echa dawnych modlitw."
            LocationType.RAUBRITTER_CASTLE -> "Z murów wciąż zwisają resztki sztandarów, a wiatr wyje w pustych wieżach."
            LocationType.MONASTERY -> "Cisza jest tu niemal fizyczna, przerywana jedynie szeptem mchu na kamieniach."
            LocationType.DUNGEON -> "Wilgoć i zapach zgnilizny nie pozostawiają złudzeń co do przeznaczenia tego miejsca."
            LocationType.HAMLET -> "Biedne chaty tulą się do siebie, jakby szukały ochrony przed nadchodzącym mrokiem."
        }
        
        val detail = adjectives.random(random)
        return "${prefixes.random(random)} miejsce, $detail. $atmosphere"
    }
}
