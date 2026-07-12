package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.QuestProgress
import com.grimreich.core.QuestStatus
import javax.inject.Inject
import javax.inject.Singleton

/**
 * System obsługujący łańcuch Trybunału (Werdyktu).
 * Incydenty (ciała bez ran, napisy na ścianach) pojawiają się wędrownie
 * w różnych miastach, budując atmosferę globalnego zagrożenia.
 */
@Singleton
class VerdictIncidentsSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine
) {
    fun onCityEntered(cityId: String) {
        val state = gameRepository.currentState()
        
        // Nie naliczamy incydentów, jeśli kampania jest już gotowa lub ukończona
        if (state.quest.worldFlags.contains("verdict_campaign_ready") || 
            state.quest.completedQuestIds.contains("q_verdict_1")) return

        val current = state.quest.progress["meta_verdict_incidents"]?.variables?.get("count") ?: 0
        val next = current + 1

        state.quest.progress["meta_verdict_incidents"] = QuestProgress(
            questId = "meta_verdict_incidents",
            status = QuestStatus.ACTIVE,
            variables = mapOf("count" to next)
        )

        // Incydenty pojawiają się wędrownie w dowolnym mieście
        when (next) {
            1 -> state.logEntries.add("W cieniu bramy ${getCityName(cityId)} dostrzegasz ciało bez ran. Obok wyryto: WYROK WYKONANY.")
            3 -> state.logEntries.add("Mieszkańcy ${getCityName(cityId)} szepczą o 'beztwarzowych sędziach'. Na murze widnieje runa: WYMAZANA.")
            5 -> state.logEntries.add("W ruinach na obrzeżach miasta znaleziono listę z Twoim imieniem. Nagłówek głosi: WINNI.")
            7 -> {
                state.quest.worldFlags.add("verdict_campaign_ready")
                state.logEntries.add("Czujesz na sobie wzrok Ravenna. Musisz udać się do Opactwa Ciszy, aby zmierzyć się z Werdyktem.")
            }
        }
    }

    private fun getCityName(cityId: String): String = when(cityId) {
        "wybrzeze_polnocne" -> "Wybrzeża"
        "twierdza_zelazna" -> "Twierdzy"
        "port_mglisty" -> "Portu"
        "opactwo_ciszy" -> "Opactwa"
        "serce_krainy" -> "Serca Krainy"
        else -> "miasta"
    }
}
