package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

data class ChronicleEntry(
    val day: Int,
    val text: String,
    val importance: Int = 1
)

@Singleton
class ChronicleSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val entries = mutableListOf<ChronicleEntry>()

    fun record(text: String, importance: Int = 1) {
        val entry = ChronicleEntry(gameRepository.currentState().world.day, text, importance)
        entries.add(entry)
        gameRepository.log(text)
    }

    fun getAll(): List<ChronicleEntry> = entries

    fun getSummary(): String {
        return if (entries.isEmpty()) {
            "Kronika jest pusta..."
        } else {
            entries.joinToString("\n") { "[Dzień ${it.day}] ${it.text}" }
        }
    }
}
