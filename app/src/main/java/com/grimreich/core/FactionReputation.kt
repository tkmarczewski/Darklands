package com.grimreich.core

import javax.inject.Inject

enum class FactionType { CHURCH, NOBILITY, MERCHANTS, PEASANTS, OUTLAWS, MILITARY, SCHOLARS }

data class Faction(
    val id: String,
    val name: String,
    val type: FactionType,
    val description: String
)


object FactionCatalogue {
    val factions = listOf(
        Faction("CHURCH", "Zakon Świtu", FactionType.CHURCH, "Strażnicy paradygmatu."),
        Faction("NOBILITY", "Arystokracja", FactionType.NOBILITY, "Dawni władcy tych ziem."),
        Faction("MERCHANTS", "Gildia Kupiecka", FactionType.MERCHANTS, "Władcy handlu i esencji."),
        Faction("PEASANTS", "Chłopi", FactionType.PEASANTS, "Ludzie starający się przetrwać."),
        Faction("OUTLAWS", "Bandyci", FactionType.OUTLAWS, "Ci, którzy odrzucili prawo."),
        Faction("MILITARY", "Wojsko", FactionType.MILITARY, "Ostatnia linia obrony murów."),
        Faction("SCHOLARS", "Kolegium Uczonych", FactionType.SCHOLARS, "Badacze pęknięć rzeczywistości.")
    )

    fun findById(id: String) = factions.find { it.id == id }
}

class FactionReputationSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    companion object {
        const val BASE_SELL_MULTIPLIER = 0.5f

        fun reputationLabel(rep: Int): String = when {
            rep <= -50 -> "WROGA"
            rep <= -20 -> "ZŁA"
            rep <= 20 -> "NEUTRALNA"
            rep <= 50 -> "DOBRA"
            else -> "WYBITNA"
        }

        fun buyModifier(rep: Int): Float {
            return (1.0f - (rep * 0.02f)).coerceIn(0.7f, 1.3f)
        }

        fun sellModifier(rep: Int): Float {
            return (1.0f + (rep * 0.02f)).coerceIn(0.7f, 1.3f)
        }
    }

    fun getReputation(factionId: String): Int = 
        gameRepository.currentState().reputation.globalFactions[factionId] ?: 0

    fun changeReputation(factionId: String, delta: Int): String {
        var result = ""
        gameRepository.updateState { state ->
            val faction = FactionCatalogue.findById(factionId) ?: run {
                result = "Nieznana frakcja: $factionId"
                return@updateState
            }
            val current = state.reputation.globalFactions[factionId] ?: 0
            val next = (current + delta).coerceIn(-100, 100)
            state.reputation.globalFactions[factionId] = next
            
            val label = reputationLabel(next)
            val appliedDelta = next - current
            result = "Twoja reputacja u ${faction.name} zmieniła się na: $label ($appliedDelta)"
            state.logEntries.add("TRIBUNAL_LOG_014: Reputacja ${faction.name}: $next ($appliedDelta).")
        }
        return result
    }

    fun getAll(): Map<String, Int> = gameRepository.currentState().reputation.globalFactions

    fun summary(): String {
        val factions = gameRepository.currentState().reputation.globalFactions
        return factions.entries.joinToString("\n") { (id, rep) -> 
            val name = FactionCatalogue.findById(id)?.name ?: id
            "$name: ${reputationLabel(rep)} ($rep)"
        }
    }
}
