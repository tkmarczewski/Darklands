package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.ChronicleEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChronicleSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val allEntries = mutableMapOf<String, ChronicleEntry>()

    init {
        seed()
    }

    fun seed() {
        register(ChronicleEntry("lore_fracture_origin", "Początek Pęknięcia", "W roku 1242 Mgła po raz pierwszy przestała być tylko zjawiskiem pogodowym.", "HISTORIA", false))
        register(ChronicleEntry("lore_scribes", "Archiwiści Absolutu", "Mówią, że świat jest zapisywany w czasie rzeczywistym przez istoty spoza paradygmatu.", "ONTOLOGIA", false))
        register(ChronicleEntry("lore_black_anchor", "Czarna Kotwica", "Ostatnie zabezpieczenie przed całkowitym wymazaniem GrimReich.", "TAJEMNICA", false))
    }

    fun register(entry: ChronicleEntry) {
        allEntries[entry.id] = entry
    }

    fun unlock(id: String) {
        val entry = allEntries[id] ?: return
        if (!entry.unlocked) {
            allEntries[id] = entry.copy(unlocked = true)
            gameRepository.updateState { state ->
                state.unlockedLoreIds.add(id)
                state.logEntries.add("Nowy wpis w Kronice: ${entry.title}")
            }
        }
    }

    fun record(msg: String, stabilityThreshold: Int = 100) {
        val currentStability = gameRepository.currentState().world.globalStability
        if (currentStability <= stabilityThreshold) {
            gameRepository.log("[KRONIKA] $msg")
        }
    }

    fun getUnlockedEntries(): List<ChronicleEntry> {
        val unlockedIds = gameRepository.currentState().unlockedLoreIds
        return allEntries.values.filter { it.id in unlockedIds || it.unlocked }
    }

    fun isUnlocked(id: String): Boolean = gameRepository.currentState().unlockedLoreIds.contains(id)

    fun getAll(): List<ChronicleEntry> = allEntries.values.toList()
}
