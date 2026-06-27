package com.grimreich.ui.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.VisualContentSystem
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HubUiState(
    val locationName: String = "",
    val day: Int = 1,
    val timeOfDay: String = "",
    val gold: Int = 0,
    val activeQuestsCount: Int = 0,
    val expeditionQuestsCount: Int = 0,
    val party: List<Hero> = emptyList(),
    val worldStability: Int = 100,
    val hubBackground: String = "bg_party_castle",
    val hubTintColor: Color = Color.Transparent,
    val atmosphericMessage: String = ""
)

@HiltViewModel
class HubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val visualContentSystem: VisualContentSystem,
    private val endingSystem: com.grimreich.systems.EndingSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(HubUiState())
    val uiState: StateFlow<HubUiState> = _uiState.asStateFlow()

    fun checkForEnding(onTrigger: () -> Unit) {
        if (endingSystem.shouldTriggerMetaEnding()) {
            onTrigger()
        }
    }

    init {
        gameRepository.gameState
            .onEach { state ->
                val currentCityId = state.grimCurrentRegion
                val city = cityCatalogue.get(currentCityId)
                val activeCount = state.quest.activeQuestIds.size
                val expeditionCount = questEngine.getActiveQuestsForCity(currentCityId).size
                val stability = state.world.globalStability

                _uiState.update { 
                    it.copy(
                        locationName = city?.name ?: "Pustka",
                        day = state.world.day,
                        timeOfDay = state.world.timeOfDay,
                        gold = state.gold,
                        activeQuestsCount = activeCount,
                        expeditionQuestsCount = expeditionCount,
                        party = state.party.toList(),
                        worldStability = stability,
                        hubBackground = visualContentSystem.getHubBackground(currentCityId, stability),
                        hubTintColor = visualContentSystem.getHubTintColor(stability),
                        atmosphericMessage = visualContentSystem.getAtmosphericMessage(stability)
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
