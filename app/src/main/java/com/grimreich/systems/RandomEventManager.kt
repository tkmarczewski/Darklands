package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.ui.UiUtils
import kotlin.random.Random

object RandomEventManager {

    fun triggerCityEvent(context: Context) {
        if (Random.nextInt(100) > 40) return // 40% szansy na zdarzenie przy wejściu

        val event = cityEvents.random()
        applyEventEffects(event)
        UiUtils.showNarrativePopup(context, "MIEJSKIE WIEŚCI", event.description)
    }

    fun triggerTravelEvent(context: Context) {
        val event = travelEvents.random()
        applyEventEffects(event)
        UiUtils.showNarrativePopup(context, "DROGA", event.description)
    }

    fun triggerHubEvent(context: Context) {
        if (Random.nextInt(100) > 30) return // 30% chance in Hub
        val event = cityEvents.random() // Hub uses city events for now
        applyEventEffects(event)
        UiUtils.showNarrativePopup(context, "ECHA HUB'U", event.description)
    }

    private fun applyEventEffects(event: GameEvent) {
        val state = GameRepository.state
        state.world.globalStability += event.stabilityDelta
        state.gold += event.goldDelta
        state.party.forEach { 
            it.hp = (it.hp + event.hpDelta).coerceIn(0, it.maxHp)
            it.sanity = (it.sanity + event.sanityDelta).coerceIn(0, 100)
            it.morale = (it.morale + event.moraleDelta).coerceIn(0, 100)
        }
    }

    data class GameEvent(
        val description: String,
        val hpDelta: Int = 0,
        val sanityDelta: Int = 0,
        val goldDelta: Int = 0,
        val stabilityDelta: Int = 0,
        val moraleDelta: Int = 0
    )

    private val cityEvents = listOf(
        GameEvent("Uliczny kaznodzieja krzyczy o nadchodzącym wymazaniu. Jego słowa budzą niepokój.", sanityDelta = -5),
        GameEvent("Znalazłeś porzuconą sakiewkę w cieniu pękniętego muru.", goldDelta = 25),
        GameEvent("Lokalna straż wymusza 'podatek za istnienie'.", goldDelta = -15),
        GameEvent("Poczułeś nagły przypływ wiary patrząc na symbol Proroka.", sanityDelta = 10, moraleDelta = 5),
        GameEvent("Widziałeś jak szczur zmienił się w pył na Twoich oczach. Rzeczywistość pęka.", stabilityDelta = -2, sanityDelta = -3),
        GameEvent("Ktoś zostawił ciepły posiłek na progu karczmy. Zjedliście go w milczeniu.", hpDelta = 5),
        GameEvent("Słyszysz śpiew dochodzący z wnętrza studni. Jest piękny i przerażający.", sanityDelta = -10, stabilityDelta = -1),
        GameEvent("Kupiec pomylił się przy wydawaniu reszty na Twoją korzyść.", goldDelta = 10),
        GameEvent("Mgła wdarła się do miasta wcześniej niż zwykle.", moraleDelta = -10),
        GameEvent("Dziecko narysowało Twoją twarz na piasku... ze skrzydłami z ognia.", sanityDelta = 5)
    )

    private val travelEvents = listOf(
        GameEvent("Napotkaliście grupę uchodźców uciekających przed Mgłą.", moraleDelta = -5),
        GameEvent("Odkryliście starożytny menhir, który pulsuje rytmem serca.", sanityDelta = 15, stabilityDelta = 1),
        GameEvent("Zasadzka! Musieliście salwować się ucieczką przez cierniste krzewy.", hpDelta = -10),
        GameEvent("Znalazłeś grzyby o smaku starych wspomnień.", sanityDelta = 5, hpDelta = 2),
        GameEvent("Niebo zmieniło kolor na purpurowy. Czas wydaje się stać w miejscu.", stabilityDelta = -3),
        GameEvent("Kruki śledzą każdy Wasz krok, powtarzając Wasze imiona.", sanityDelta = -15),
        GameEvent("Deszcz czarnej wody obmył Wasze pancerze. Stal wydaje się cięższa.", hpDelta = -2),
        GameEvent("Napotkany pustelnik podzielił się z Wami wiedzą o Sferze Fenomenów.", sanityDelta = 10, stabilityDelta = 1),
        GameEvent("Ziemia zatrzęsła się pod stopami. To nie był wstrząs, to był oddech.", stabilityDelta = -5, sanityDelta = -5),
        GameEvent("Odnaleźliście zapomniany obóz z zapasami.", hpDelta = 10, goldDelta = 15)
    )
}
