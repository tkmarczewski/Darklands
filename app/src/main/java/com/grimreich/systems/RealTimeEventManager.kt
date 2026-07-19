package com.grimreich.systems

import android.content.Context
import com.grimreich.core.GameRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Zarządza zdarzeniami zależnymi od rzeczywistego czasu i daty.
 */
@Singleton
class RealTimeEventManager @Inject constructor(
    private val gameRepository: GameRepository
) {

    fun checkRealTimeEvents(context: Context?): String? {
        val now = System.currentTimeMillis()
        val eventMessages = mutableListOf<String>()

        gameRepository.updateState { state ->
            val diffMillis = now - state.lastSaveTimestamp
            val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis)

            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // 1. Zdarzenie zależne od przerwy w grze
            if (diffHours >= 24) {
                val goldLost = (state.gold * 0.05).toInt().coerceAtMost(50)
                state.gold -= goldLost
                eventMessages.add("Długa nieobecność: Twoi najemnicy potrzebowali żołdu. Stracono $goldLost złota.")
            } else if (diffHours >= 8) {
                state.party.forEach { it.hp = (it.hp + 10).coerceAtMost(it.maxHp) }
                eventMessages.add("Odpoczynek: Drużyna zregenerowała siły podczas Twojej nieobecności (+10 HP).")
            }

            // 2. Zdarzenia kalendarzowe
            if (dayOfWeek == Calendar.SUNDAY) {
                eventMessages.add("Krwawa Niedziela: Cienie są silniejsze, ale skarby bogatsze (Modyfikator lootu x1.5).")
            } else if (dayOfWeek == Calendar.FRIDAY) {
                eventMessages.add("Piątek Pokutny: Modlitwy w Kaplicy są dwa razy skuteczniejsze.")
            }

            state.lastSaveTimestamp = now
        }

        return if (eventMessages.isNotEmpty()) eventMessages.joinToString("\n\n") else null
    }
}
