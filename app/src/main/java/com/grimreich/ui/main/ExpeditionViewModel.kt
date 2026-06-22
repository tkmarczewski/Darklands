package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        gameRepository.gameState
            .onEach { state ->
                val currentCityId = state.grimCurrentRegion
                val city = cityCatalogue.get(currentCityId)
                val activeOutside = state.quest.activeQuests
                    .mapNotNull { questSystem.getQuest(it) }
                    .filter { it.cityId == currentCityId && it.isOutsideCity }

                _uiState.update { 
                    it.copy(
                        regionName = city?.name ?: "Nieznana okolica",
                        outsideQuests = activeOutside
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun startQuestCombat(quest: QuestEntry, onStart: () -> Unit) {
        onStart()
    }
}
