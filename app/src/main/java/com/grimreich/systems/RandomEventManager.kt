package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.ui.UiUtils
import kotlin.random.Random

/**
 * Zarządza zdarzeniami losowymi występującymi podczas nawigacji.
 */
object RandomEventManager {

    fun triggerHubEvent(context: Context) {
        if (Random.nextInt(100) < 15) { // 15% szansy
            val event = hubEvents.random()
            applyEffect(event)
            UiUtils.showNarrativePopup(context, event.title, event.description)
        }
    }

    fun triggerCityEvent(context: Context) {
        if (Random.nextInt(100) < 25) { // 25% szansy w mieście
            val event = cityEvents.random()
            applyEffect(event)
            UiUtils.showNarrativePopup(context, event.title, event.description)
        }
    }

    private fun applyEffect(event: RandomEvent) {
        val state = GameRepository.state
        state.gold = (state.gold + event.goldChange).coerceAtLeast(0)
        state.party.forEach { 
            it.hp = (it.hp + event.hpChange).coerceIn(0, it.maxHp)
        }
    }

    private val hubEvents = listOf(
        RandomEvent("ZNALEZISKO", "Podczas porządkowania obozowiska znaleziono sakiewkę ze srebrem.", 20, 0),
        RandomEvent("DOBRE WIEŚCI", "Wędrowny kupiec podzielił się zapasami. Drużyna odzyskała siły.", 0, 15),
        RandomEvent("MGŁA GĘSTNIEJE", "Dziwne szepty zza mgły sprawiły, że noc była niespokojna. Drużyna czuje zmęczenie.", 0, -5)
    )

    private val cityEvents = listOf(
        RandomEvent("KIESZONKOWIEC", "W tłumie na targu ktoś przeciął Twoją sakiewkę!", -30, 0),
        RandomEvent("BŁOGOSŁAWIEŃSTWO", "Lokalny kapłan pobłogosławił Waszą wyprawę.", 0, 10),
        RandomEvent("STARCIE W ZAUŁKU", "Zostaliście napadnięci przez rzezimieszków. Udało się uciec, ale nie bez ran.", 10, -15)
    )

    data class RandomEvent(val title: String, val description: String, val goldChange: Int, val hpChange: Int)
}
