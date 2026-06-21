package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ExpeditionUiState(
    val regionName: String = "",
    val outsideQuests: List<QuestEntry> = emptyList()
)

@HiltViewModel
class ExpeditionViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val cityCatalogue: CityCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpeditionUiState())
    val uiState: StateFlow<ExpeditionUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val currentCityId = state.grimCurrentRegion
        val city = cityCatalogue.get(currentCityId)

        // Find active quests for this region that are "outside city"
        // For now, we'll assume all local active quests can lead to an expedition
        val activeLocal = state.quest.activeQuests
            .mapNotNull { questSystem.getQuest(it) }
            .filter { it.cityId == currentCityId }

        _uiState.update { 
            it.copy(
                regionName = city?.name ?: "Nieznana okolica",
                outsideQuests = activeLocal
            )
        }
    }

    fun startQuestCombat(quest: QuestEntry, onStart: () -> Unit) {
        // Logic to transition to combat or specific expedition event
        onStart()
    }
}
