package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.ui.UiUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RandomEventManager @Inject constructor(
    private val gameRepository: GameRepository
) {

    fun triggerCityEvent(context: Context) {
        if (Random.nextInt(100) > 40) return

        val event = cityEvents.random()
        applyEventEffects(event)

        (context as? android.app.Activity)?.runOnUiThread {
            UiUtils.showNarrativePopup(context, "MIEJSKIE WIEŚCI", event.description)
        }
    }

    fun triggerTravelEvent(context: Context) {
        val event = travelEvents.random()
        applyEventEffects(event)

        (context as? android.app.Activity)?.runOnUiThread {
            UiUtils.showNarrativePopup(context, "WYDARZENIE W PODRÓŻY", event.description)
        }
    }

    fun triggerHubEvent(context: Context) {
        if (Random.nextInt(100) > 30) return

        val event = cityEvents.random()
        applyEventEffects(event)

        (context as? android.app.Activity)?.runOnUiThread {
            UiUtils.showNarrativePopup(context, "ECHA HUB'U", event.description)
        }
    }

    private fun applyEventEffects(event: GameEvent) {
        val state = gameRepository.currentState()
        state.world.globalStability += event.stabilityDelta
        state.gold += event.goldDelta
        state.party.forEach {
            it.hp = (it.hp + event.hpDelta).coerceIn(0, it.maxHp)
            it.sanity = (it.sanity + event.sanityDelta).coerceIn(0, 100)
            it.morale = (it.morale + event.moraleDelta).coerceIn(0, 100)
        }
        gameRepository.persistCurrentState()
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
        GameEvent("Mgła wdarła się do miasta wcześniej niż zwykle.", moraleDelta = -10)
    )

    private val travelEvents = listOf(
        GameEvent("Znaleźliście opuszczony obóz. W popiele wciąż tli się żar.", sanityDelta = -2, goldDelta = 5),
        GameEvent("Napadła was wataha wychudzonych wilków.", hpDelta = -6, moraleDelta = -5),
        GameEvent("Spotkaliście pielgrzyma, który pobłogosławił waszą drogę.", sanityDelta = 5, moraleDelta = 5),
        GameEvent("Most był częściowo zawalony. Straciliście czas i siły.", hpDelta = -3, stabilityDelta = -1),
        GameEvent("W ruinach kapliczki odnaleźliście drobne kosztowności.", goldDelta = 20)
    )
}
