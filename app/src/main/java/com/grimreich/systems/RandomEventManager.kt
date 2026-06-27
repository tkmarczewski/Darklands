package com.grimreich.systems

import android.content.Context
import android.widget.Toast
import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RandomEventManager @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun triggerCityEvent(context: Context) {
        if (Random.nextFloat() < 0.15f) {
            val event = cityEvents.random()
            applyEventEffects(event)
            Toast.makeText(context, "WYDARZENIE: ${event.description}", Toast.LENGTH_LONG).show()
        }
    }

    fun triggerTravelEvent(context: Context) {
        if (Random.nextFloat() < 0.25f) {
            val event = travelEvents.random()
            applyEventEffects(event)
            Toast.makeText(context, "PODRÓŻ: ${event.description}", Toast.LENGTH_LONG).show()
        }
    }

    fun triggerHubEvent(context: Context) {
        // High stability reduces hub event chance
        val stability = gameRepository.currentState().world.globalStability
        val chance = if (stability < 40) 0.2f else 0.05f
        
        if (Random.nextFloat() < chance) {
            val event = travelEvents.random()
            applyEventEffects(event)
            Toast.makeText(context, "MIEJSCE POSTOJU: ${event.description}", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyEventEffects(event: GameEvent) {
        gameRepository.updateState { s ->
            s.world.globalStability = (s.world.globalStability + event.stabilityDelta).coerceIn(0, 100)
            s.gold = (s.gold + event.goldDelta).coerceAtLeast(0)
            s.party.forEach { hero ->
                hero.hp = (hero.hp + event.hpDelta).coerceIn(0, hero.maxHp)
                hero.sanity = (hero.sanity + event.sanityDelta).coerceIn(0, 100)
                hero.morale = (hero.morale + event.moraleDelta).coerceIn(0, 100)
            }
            s.logEntries.add("Zdarzenie: ${event.description}")
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
        GameEvent("Uliczny kaznodzieja głosi koniec świata.", sanityDelta = -5, stabilityDelta = -2),
        GameEvent("Znaleziono porzuconą sakiewkę.", goldDelta = 25),
        GameEvent("Mieszkańcy świętują festiwal światła.", stabilityDelta = 5, moraleDelta = 10),
        GameEvent("Podejrzany kupiec oferuje dziwne mikstury.", sanityDelta = -2),
        GameEvent("Warta miejska żąda opłaty za przejście.", goldDelta = -10)
    )

    private val travelEvents = listOf(
        GameEvent("Odpoczynek przy czystym źródle.", hpDelta = 10, moraleDelta = 5),
        GameEvent("Napad zbójców na szlaku!", hpDelta = -5, goldDelta = -20),
        GameEvent("Mgła gęstnieje, tracicie orientację.", sanityDelta = -10, stabilityDelta = -5),
        GameEvent("Spotkanie z wędrownym bardem.", moraleDelta = 15),
        GameEvent("Odnaleziono ruiny dawnej kapliczki.", stabilityDelta = 3, goldDelta = 5)
    )
}
