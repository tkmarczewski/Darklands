package com.grimreich.core

enum class FactionType { CHURCH, NOBILITY, MERCHANTS, PEASANTS, OUTLAWS, MILITARY, SCHOLARS }

data class Faction(
    val id: String,
    val name: String,
    val type: FactionType,
    val description: String
)

data class FactionReputationEntry(
    val factionId: String,
    var reputation: Int
)

object FactionCatalogue {
    val factions = listOf(
        Faction("CHURCH", "Zakon Świtu", FactionType.CHURCH, "Strażnicy paradygmatu."),
        Faction("NOBILITY", "Arystokracja", FactionType.NOBILITY, "Dawni władcy tych ziem."),
        Faction("MERCHANTS", "Gildia Kupiecka", FactionType.MERCHANTS, "Władcy handlu i esencji."),
        Faction("PEASANTS", "Chłopi", FactionType.PEASANTS, "Ludzie starający się przetrwać."),
        Faction("OUTLAWS", "Bandyci", FactionType.OUTLAWS, "Ci, którzy odrzucili prawo."),
        Faction("MILITARY", "Wojsko", FactionType.MILITARY, "Ostatnia linia obrony murów.")
    )

    fun findById(id: String) = factions.find { it.id == id }
}

class FactionReputationSystem {
    companion object {
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

    private val entries = mutableMapOf<String, FactionReputationEntry>()

    init {
        FactionCatalogue.factions.forEach { 
            entries[it.id] = FactionReputationEntry(it.id, 0)
        }
    }

    fun getReputation(factionId: String): Int = entries[factionId]?.reputation ?: 0

    fun changeReputation(factionId: String, delta: Int): String {
        val entry = entries[factionId] ?: return "Nieznana frakcja"
        entry.reputation = (entry.reputation + delta).coerceIn(-100, 100)
        val label = reputationLabel(entry.reputation)
        return "Twoja reputacja u ${FactionCatalogue.findById(factionId)?.name} zmieniła się na: $label ($delta)"
    }

    fun getAll(): Map<String, Int> = entries.mapValues { it.value.reputation }

    fun summary(): String {
        return entries.values.joinToString("\n") { 
            val name = FactionCatalogue.findById(it.factionId)?.name ?: it.factionId
            "$name: ${reputationLabel(it.reputation)} (${it.reputation})"
        }
    }
}
