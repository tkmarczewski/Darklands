package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
            
        // When entering expedition screen, mark as active
        enterExpedition()
    }

    private fun enterExpedition() {
        gameRepository.updateState { it.isExpeditionActive = true }
    }

    fun exitExpedition(onBack: () -> Unit) {
        gameRepository.updateState { it.isExpeditionActive = false }
        onBack()
    }

    fun startQuestCombat(quest: QuestEntry, onStart: () -> Unit) {
        onStart()
    }

    fun completeNonCombatQuest(quest: QuestEntry, onComplete: () -> Unit) {
        questSystem.complete(quest.id)
        onComplete()
    }

    fun questHasCombat(quest: QuestEntry): Boolean = quest.hasCombat

    override fun onCleared() {
        super.onCleared()
        // Defensive cleanup if user leaves via other means
        gameRepository.updateState { it.isExpeditionActive = false }
    }
}
