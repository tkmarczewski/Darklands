package com.grimreich.systems

import com.grimreich.core.GameRepository

data class ChronicleEntry(
    val day: Int,
    val text: String,
    val importance: Int = 1
)

object ChronicleSystem {
    private val entries = mutableListOf<ChronicleEntry>()

    fun record(text: String, importance: Int = 1) {
        val entry = ChronicleEntry(GameRepository.state.world.day, text, importance)
        entries.add(entry)
        GameRepository.log("KRONIKA: $text")
    }

    fun getAll(): List<ChronicleEntry> = entries.toList()
    
    fun getSummary(): String = buildString {
        appendLine("=== KRONIKA GRIMREICH ===")
        if (entries.isEmpty()) appendLine("Brak zapisanych czynów.")
        else entries.sortedByDescending { it.day }.forEach { entry ->
            appendLine("Dzień ${entry.day}: ${entry.text}")
        }
    }
}
