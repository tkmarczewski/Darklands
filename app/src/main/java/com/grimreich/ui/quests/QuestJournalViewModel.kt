package com.grimreich.ui.quests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestDefinition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class QuestJournalUiState(
    val activeQuests: List<Pair<QuestDefinition, String>> = emptyList(),
    val completedQuests: List<QuestDefinition> = emptyList()
)

@HiltViewModel
class QuestJournalViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestJournalUiState())
    val uiState: StateFlow<QuestJournalUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                val active = state.quest.activeQuestIds.mapNotNull { qId ->
                    questEngine.getDefinition(qId)?.let { def ->
                        def to questEngine.getCurrentObjective(qId, state)
                    }
                }
                val completed = state.quest.completedQuestIds.mapNotNull { questEngine.getDefinition(it) }
                
                _uiState.update { 
                    it.copy(
                        activeQuests = active,
                        completedQuests = completed
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
