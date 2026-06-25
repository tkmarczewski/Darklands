package com.grimreich.ui.quests

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestSystem
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

    init {
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val currentCityId = state.grimCurrentRegion
        
        val active = state.quest.activeQuests.mapNotNull { questSystem.getQuest(it) }
        val completed = state.quest.completedQuests.mapNotNull { questSystem.getQuest(it) }
        
        val occupiedIds = (active.map { it.id } + completed.map { it.id }).toSet()
        
        // Show available quests for the current city
        val available = questSystem.all()
            .filter { it.status == com.grimreich.systems.QuestStatus.DOSTEPNE && !occupiedIds.contains(it.id) }
            .filter { it.cityId == currentCityId }
            .shuffled()
            .take(5)

        _uiState.update {
            it.copy(
                activeQuests = active,
                completedQuests = completed,
                availableQuests = available
            )
        }
    }

    fun acceptQuest(questId: String) {
        val state = gameRepository.currentState()
        // Standard limit: total active quests cannot exceed 5
        if (state.quest.activeQuests.size < 5) {
            questSystem.activate(questId)
            gameRepository.persistCurrentState()
            refresh()
        }
    }
}
