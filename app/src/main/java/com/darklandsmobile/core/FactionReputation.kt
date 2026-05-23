package com.darklandsmobile.core

enum class FactionType {
    CHURCH, NOBILITY, MERCHANTS, PEASANTS, OUTLAWS, MILITARY, SCHOLARS
}

data class Faction(
    val id: String,
    val name: String,
    val type: FactionType,
    val description: String = ""
)

data class FactionReputationEntry(
    val factionId: String,
    var reputation: Int = 0
)

object FactionCatalogue {
    val factions = listOf(
        Faction("church", "Kościół", FactionType.CHURCH, "Instytucja religijna. Ceni cnotę i wiarę."),
        Faction("nobility", "Szlachta", FactionType.NOBILITY, "Możni tego świata. Cenią honor i rodowód."),
        Faction("merchants", "Kupcy", FactionType.MERCHANTS, "Handel i pieniądze. Cenią zysk i kontakty."),
        Faction("peasants", "Lud", FactionType.PEASANTS, "Prosty lud. Ceni sprawiedliwość i ochronę."),
        Faction("outlaws", "Wyjawięci", FactionType.OUTLAWS, "Rozbiscy i złodzieje. Cenią siłę i milczenie."),
        Faction("military", "Rycerstwo", FactionType.MILITARY, "Wojownicy i straże. Cenią męstwo i lojalność."),
        Faction("scholars", "Uczeni", FactionType.SCHOLARS, "Mędrcy i alchemicy. Cenią wiedzę i dociekliwość.")
    )

    fun findById(id: String) = factions.firstOrNull { it.id == id }
}

class FactionReputationSystem {

    private val entries: MutableMap<String, FactionReputationEntry> = mutableMapOf()

    init {
        FactionCatalogue.factions.forEach { faction ->
            entries[faction.id] = FactionReputationEntry(faction.id, 0)
        }
    }

    fun getReputation(factionId: String): Int =
        entries[factionId]?.reputation ?: 0

    fun changeReputation(factionId: String, delta: Int): String {
        val entry = entries.getOrPut(factionId) { FactionReputationEntry(factionId, 0) }
        val before = entry.reputation
        entry.reputation = (entry.reputation + delta).coerceIn(-20, 20)
        val faction = FactionCatalogue.findById(factionId)
        val name = faction?.name ?: factionId
        return "$name: $before → ${entry.reputation} (${if (delta >= 0) "+$delta" else "$delta"})"
    }

    fun getAll(): Map<String, Int> =
        entries.mapValues { it.value.reputation }

    fun reputationLabel(reputation: Int): String = when {
        reputation >= 15 -> "Wielki Sojusznik"
        reputation >= 8 -> "Przyjaciel"
        reputation >= 3 -> "Znany"
        reputation >= 0 -> "Neutralny"
        reputation >= -5 -> "Podejrzany"
        reputation >= -10 -> "Wróg"
        else -> "Klątwa Frakcji"
    }

    fun tradeModifier(factionId: String): Float {
        val rep = getReputation(factionId)
        return 1.0f - (rep * 0.02f)
    }

    fun summary(): String {
        val sb = StringBuilder()
        FactionCatalogue.factions.forEach { faction ->
            val rep = getReputation(faction.id)
            sb.appendLine("${faction.name}: $rep (${reputationLabel(rep)})")
        }
        return sb.toString()
    }
}
