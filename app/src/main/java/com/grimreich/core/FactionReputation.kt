package com.grimreich.core

import javax.inject.Inject

enum class FactionType { church, nobility, merchants, peasants, outlaws, military, scholars }

data class Faction(
    val id: String,
    val name: String,
    val type: FactionType,
    val description: String
)


object FactionCatalogue {
    val factions = listOf(
        Faction("church", "Zakon Świtu", FactionType.church, "Strażnicy paradygmatu."),
        Faction("nobility", "Arystokracja", FactionType.nobility, "Dawni władcy tych ziem."),
        Faction("merchants", "Gildia Kupiecka", FactionType.merchants, "Władcy handlu i esencji."),
        Faction("peasants", "Chłopi", FactionType.peasants, "Ludzie starający się przetrwać."),
        Faction("outlaws", "Bandyci", FactionType.outlaws, "Ci, którzy odrzucili prawo."),
        Faction("military", "Wojsko", FactionType.military, "Ostatnia linia obrony murów."),
        Faction("scholars", "Kolegium Uczonych", FactionType.scholars, "Badacze pęknięć rzeczywistości.")
    )

    fun findById(id: String) = factions.find { it.id == id }
}

class FactionReputationSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    companion object {
        const val BASE_SELL_MULTIPLIER = 0.5f

        fun reputationLabel(rep: Int): String = when {
            rep <= GameConstants.HOSTILE_REPUTATION_THRESHOLD -> "WROGA"
            rep <= -20 -> "ZŁA"
            rep <= 20 -> "NEUTRALNA"
            rep <= 50 -> "DOBRA"
            else -> "WYBITNA"
        }

        fun buyModifier(rep: Int): Float {
            return (GameConstants.BASE_REPUTATION_BUY_MODIFIER - (rep * GameConstants.REPUTATION_MODIFIER_STEP)).coerceIn(0.7f, 1.3f)
        }

        fun sellModifier(rep: Int): Float {
            return (GameConstants.BASE_REPUTATION_BUY_MODIFIER + (rep * GameConstants.REPUTATION_MODIFIER_STEP)).coerceIn(0.7f, 1.3f)
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
