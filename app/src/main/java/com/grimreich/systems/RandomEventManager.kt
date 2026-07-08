package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.CombatRandomProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomEventManager @Inject constructor(
    private val gameRepository: GameRepository,
    private val random: CombatRandomProvider
) {
    data class GameEvent(
        val description: String,
        val hpDelta: Int = 0,
        val sanityDelta: Int = 0,
        val goldDelta: Int = 0,
        val stabilityDelta: Int = 0,
        val moraleDelta: Int = 0
    )

    // FIX: mutableListOf — umożliwia dodawanie eventów z zewnątrz (testy, DLC)
    private val cityEvents = mutableListOf(
        GameEvent("Uliczny kaznodzieja głosi koniec świata.", sanityDelta = -5, stabilityDelta = -2),
        GameEvent("Znaleziono porzuconą sakiewkę.", goldDelta = 25),
        GameEvent("Mieszkańcy świętują festiwal światła.", stabilityDelta = 5, moraleDelta = 10),
        GameEvent("Podejrzany kupiec oferuje dziwne mikstury.", sanityDelta = -2),
        GameEvent("Warta miejska żąda opłaty za przejście.", goldDelta = -10)
    )

    private val travelEvents = mutableListOf(
        GameEvent("Odpoczynek przy czystym źródle.", hpDelta = 10, moraleDelta = 5),
        GameEvent("Napad zbójców na szlaku!", hpDelta = -5, goldDelta = -20),
        GameEvent("Mgła gęstnieje, tracicie orientację.", sanityDelta = -10, stabilityDelta = -5),
        GameEvent("Spotkanie z wędrownym bardem.", moraleDelta = 15),
        GameEvent("Odnaleziono ruiny dawnej kapliczki.", stabilityDelta = 3, goldDelta = 5)
    )

    private val hubEvents = mutableListOf(
        GameEvent("Cicha noc przy ognisku. Drużyna odpoczywa.", hpDelta = 5, moraleDelta = 5),
        GameEvent("Nieznajomy opowiada o upadłym mieście.", sanityDelta = -3, stabilityDelta = -2),
        GameEvent("Znaleziono porzucone zapasy.", goldDelta = 15),
        GameEvent("Echo szepcze w ciemności.", sanityDelta = -5)
    )

    /**
     * Próbuje wywołać zdarzenie miejskie.
     * Zwraca opis zdarzenia lub null jeśli nic się nie wydarzyło.
     */
    fun triggerCityEvent(): String? {
        if (random.nextFloat() >= 0.15f) return null
        if (cityEvents.isEmpty()) return null
        val event = cityEvents[random.nextInt(cityEvents.size)]
        applyEventEffects(event)
        return "WYDARZENIE: ${event.description}"
    }

    /**
     * Próbuje wywołać zdarzenie podczas podróży.
     */
    fun triggerTravelEvent(): String? {
        if (random.nextFloat() >= 0.25f) return null
        if (travelEvents.isEmpty()) return null
        val event = travelEvents[random.nextInt(travelEvents.size)]
        applyEventEffects(event)
        return "PODRÓŻ: ${event.description}"
    }

    /**
     * Próbuje wywołać zdarzenie w Hubie.
     * Szansa wzrasta przy niskiej stabilności.
     */
    fun triggerHubEvent(): String? {
        val stability = gameRepository.currentState().world.globalStability
        val chance = if (stability < 40) 0.2f else 0.05f
        if (random.nextFloat() >= chance) return null
        if (hubEvents.isEmpty()) return null
        val event = hubEvents[random.nextInt(hubEvents.size)]
        applyEventEffects(event)
        return "MIEJSCE POSTOJU: ${event.description}"
    }

    private fun applyEventEffects(event: GameEvent) {
        gameRepository.updateState { s ->
            s.world.globalStability = (s.world.globalStability + event.stabilityDelta).coerceIn(0, 100)
            // FIX: gold nie może spaść poniżej 0
            s.gold = (s.gold + event.goldDelta).coerceAtLeast(0)
            s.party.forEach { hero ->
                hero.hp      = (hero.hp      + event.hpDelta     ).coerceIn(0, hero.maxHp)
                hero.sanity  = (hero.sanity  + event.sanityDelta ).coerceIn(0, 100)
                hero.morale  = (hero.morale  + event.moraleDelta ).coerceIn(0, 100)
            }
            s.logEntries.add("Zdarzenie: ${event.description}")
        }
    }

    // --- Rozszerzalność ---
    fun addCityEvent(event: GameEvent)   { cityEvents.add(event) }
    fun addTravelEvent(event: GameEvent) { travelEvents.add(event) }
    fun addHubEvent(event: GameEvent)    { hubEvents.add(event) }
}
