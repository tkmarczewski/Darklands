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
    val activeQuests: List<QuestJournalItem> = emptyList(),
    val completedQuests: List<QuestDefinition> = emptyList()
)

data class QuestJournalItem(
    val definition: QuestDefinition,
    val objective: String,
    val isReadyToTurnIn: Boolean
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
                        QuestJournalItem(
                            definition = def,
                            objective = questEngine.getCurrentObjective(qId, state),
                            isReadyToTurnIn = questEngine.isObjectiveMet(qId, state)
                        )
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
