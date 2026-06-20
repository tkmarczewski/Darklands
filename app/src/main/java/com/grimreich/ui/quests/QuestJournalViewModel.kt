package com.grimreich.ui.quests

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class QuestJournalUiState(
    val activeQuests: List<QuestEntry> = emptyList(),
    val availableQuests: List<QuestEntry> = emptyList(),
    val completedQuests: List<QuestEntry> = emptyList()
)

@HiltViewModel
class QuestJournalViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestJournalUiState())
    val uiState: StateFlow<QuestJournalUiState> = _uiState.asStateFlow()

    fun refresh() {
        questSystem.seedIntegratedContent()

        val state = gameRepository.currentState()
        val rawCity = state.grimCurrentRegion
        val cityId = rawCity.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")

        val active = state.quest.activeQuests.mapNotNull { questSystem.getQuest(it) }
        val completed = state.quest.completedQuests.mapNotNull { questSystem.getQuest(it) }
        
        val occupiedIds = (active.map { it.id } + completed.map { it.id }).toSet()
        val freeSlots = (5 - active.size).coerceAtMost(5).coerceAtLeast(0)
        
        val available = questSystem.availableForCity(cityId, excludeIds = occupiedIds)
            .shuffled()
            .take(freeSlots)

        _uiState.update {
            it.copy(
                activeQuests = active,
                completedQuests = completed,
                availableQuests = available
            )
        }
    }

    fun acceptQuest(questId: String) {
        questSystem.activate(questId)
        refresh()
    }
}
