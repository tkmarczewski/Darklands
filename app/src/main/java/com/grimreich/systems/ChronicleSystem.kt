package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.ChronicleEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChronicleSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    val allEntries = mutableMapOf<String, ChronicleEntry>()

    init {
        seed()
    }

    fun seed() {
        register(ChronicleEntry("lore_aelion_secret", "Tajemnica Aeliona", "Prorok widzi świat jako ciąg instrukcji.", "NPC", false))
        register(ChronicleEntry("lore_first_fracture", "Pierwsze Pęknięcie", "Dzień, w którym horyzont przestał być linią.", "HISTORIA", false))
        // ... and more entries as needed
    }

    fun register(entry: ChronicleEntry) {
        allEntries[entry.id] = entry
    }

    fun unlock(id: String) {
        gameRepository.updateState { state ->
            if (!state.unlockedLoreIds.contains(id)) {
                state.unlockedLoreIds.add(id)
                state.logEntries.add("Odblokowano nowy wpis w Kronice: ${allEntries[id]?.title ?: id}")
            }
        }
    }

    fun record(msg: String, stabilityImpact: Int = 0) {
        gameRepository.updateState { state ->
            state.logEntries.add(msg)
            if (stabilityImpact != 0) {
                state.world.globalStability = (state.world.globalStability + stabilityImpact).coerceIn(0, 100)
            }
        }
    }

    fun getUnlockedEntries(): List<ChronicleEntry> {
        val unlockedIds = gameRepository.currentState().unlockedLoreIds
        return allEntries.values.filter { it.id in unlockedIds }
    }

    fun isUnlocked(id: String): Boolean = gameRepository.currentState().unlockedLoreIds.contains(id)

    fun getAll(): List<ChronicleEntry> = allEntries.values.toList()
}
