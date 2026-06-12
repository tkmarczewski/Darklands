package com.grimreich.systems

import java.util.Calendar

/**
 * Synchronizes the game's ontological state with the real-world calendar.
 */
object CalendarAuraSystem {

    data class Aura(val name: String, val effect: String, val description: String)

    fun getCurrentAura(): Aura {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) // 0-indexed

        // SPECIAL DATES
        if (month == Calendar.OCTOBER && dayOfMonth == 31) {
            return Aura("PRZENIKANIE", "Wycieki rzeczywistości +50%", "Granica między sferami zanika. Echa przeszłości są niemal materialne.")
        }
        if (dayOfMonth == 13) {
            return Aura("DZIEŃ PĘKNIĘCIA", "Stabilność Świata -20", "W trzynastym dniu miesiąca Absolut mruga częściej.")
        }

        // DAYS OF THE WEEK
        return when (dayOfWeek) {
            Calendar.MONDAY -> Aura("PONIEDZIAŁEK MGŁY", "Sanity recovery -50%", "Aelion odbiera barwy wspomnieniom. Początek tygodnia jest pusty.")
            Calendar.WEDNESDAY -> Aura("ŚRODA ODBIĆ", "Lustra pokazują prawdę", "Sędzia Mira waży każde słowo. Kłamstwa bolą podwójnie.")
            Calendar.FRIDAY -> Aura("PIĄTEK POKUTNY", "Damage +25%", "Xyrel żąda krwi. Wszelkie rany goją się wolniej, ale stal jest ostrzejsza.")
            Calendar.SUNDAY -> Aura("DZIEŃ ABSOLUTU", "Glitch Intensity MAX", "Cisza przed wymazaniem. Rzeczywistość traci nasycenie.")
            else -> Aura("DZIEŃ FENOMENÓW", "Brak specjalnych modyfikatorów", "Świat pęka w swoim naturalnym rytmie.")
        }
    }
}
