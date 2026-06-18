package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import com.grimreich.ui.UiUtils
import kotlin.random.Random

object RandomEventManager {

    /**
     * DEPRECATED: Automatic XML popups disabled to prevent UI 2.0 flow interruption.
     * Events should now be triggered manually via Hub or City actions.
     */
    fun triggerCityEvent(context: Context) {
        // Disabled for UI 2.0 stability
    }

    fun triggerTravelEvent(context: Context) {
        // Disabled for UI 2.0 stability
    }

    fun triggerHubEvent(context: Context) {
        // Disabled for UI 2.0 stability
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
        GameEvent("Lokalna straż wymusza 'podatek za istnienie'.", goldDelta = -15)
    )

    private val travelEvents = listOf(
        GameEvent("Napotkaliście grupę uchodźców uciekających przed Mgłą.", moraleDelta = -5),
        GameEvent("Odkryliście starożytny menhir, który pulsuje rytmem serca.", sanityDelta = 15, stabilityDelta = 1)
    )
}
