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

        // 1. CHOOSE PATH: Suspect or Investigator (once per session)
        if (!state.quest.worldFlags.any { it.startsWith("verdict_path_") }) {
            // Chance to be a suspect increases with companion shadows
            val suspectChance = 0.3f + (state.companionShadows.size * 0.15f)
            // PURIFICATION: Always use lowercase for path values
            val path = if (kotlin.random.Random.nextFloat() < suspectChance) "suspect" else "investigator"
            state.quest.worldFlags.add("verdict_path_$path")
            android.util.Log.d("Verdict", "Path chosen: $path (Suspect chance was $suspectChance)")
        }

        val isSuspect = state.quest.worldFlags.contains("verdict_path_suspect")

        val current = state.quest.progress["meta_verdict_incidents"]?.variables?.get("count") ?: 0
        val next = current + 1

        state.quest.progress["meta_verdict_incidents"] = QuestProgress(
            questId = "meta_verdict_incidents",
            status = QuestStatus.ACTIVE,
            variables = mapOf("count" to next)
        )

        // Store city in history for Ravenn's accusation evidence
        val visitHistory = state.quest.progress["meta_verdict_history"]?.variables?.toMutableMap() ?: mutableMapOf()
        visitHistory[cityId] = (visitHistory[cityId] ?: 0) + 1
        state.quest.progress["meta_verdict_history"] = QuestProgress("meta_verdict_history", QuestStatus.ACTIVE, variables = visitHistory.toMap())

        // Incydenty pojawiają się wędrownie w dowolnym mieście
        when (next) {
            1 -> {
                val msg = if (isSuspect) "W cieniu bramy ${getCityName(cityId)} dostrzegasz ciało. Masz wrażenie, że ktoś patrzy na Twoje ręce."
                else "W cieniu bramy ${getCityName(cityId)} dostrzegasz ciało bez ran. Obok wyryto: WYROK WYKONANY."
                state.logEntries.add(msg)
            }
            3 -> {
                val msg = if (isSuspect) "Mieszkańcy ${getCityName(cityId)} milkną na Twój widok. Strażnik zanotował czas Twojego przyjazdu."
                else "Mieszkańcy ${getCityName(cityId)} szepczą o 'beztwarzowych sędziach'. Na murze widnieje runa: WYMAZANA."
                state.logEntries.add(msg)
            }
            5 -> {
                val msg = if (isSuspect) "W ruinach na obrzeżach miasta znaleziono listę 'Pęknięć'. Twoje imię jest na szczycie."
                else "W ruinach na obrzeżach miasta znaleziono listę z Twoim imieniem. Nagłówek głosi: WINNI."
                state.logEntries.add(msg)
            }
            7 -> {
                state.quest.worldFlags.add("verdict_campaign_ready")
                state.quest.worldFlags.add("verdict_ravenn_interaction_pending")
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
