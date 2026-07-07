package com.grimreich.ui.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.EndingSystem
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.VisualContentSystem
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

import com.grimreich.R

data class HubUiState(
    val locationName: String = "",
    val locationNameRes: Int? = null,
    val day: Int = 1,
    val timeOfDay: String = "",
    val gold: Int = 0,
    val activeQuestsCount: Int = 0,
    val expeditionQuestsCount: Int = 0,
    val party: List<Hero> = emptyList(),
    val worldStability: Int = 100,
    val hubBackground: String = "",
    val hubTintColor: Color = Color.Transparent,
    val atmosphericMessageRes: Int = R.string.stability_high,
    val latestLogs: List<String> = emptyList()
)

@HiltViewModel
class HubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val visualContentSystem: VisualContentSystem,
    private val endingSystem: EndingSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(HubUiState())
    val uiState: StateFlow<HubUiState> = _uiState.asStateFlow()

    fun checkForEnding(onTrigger: () -> Unit) {
        if (endingSystem.shouldTriggerMetaEnding()) {
            onTrigger()
        }
    }

    init {
        combine(gameRepository.gameState, gameRepository.gameLogs) { state, logs ->
            state to logs
        }.onEach { (state, logs) ->
            val currentCityId = state.grimCurrentRegion
            val city = cityCatalogue.get(currentCityId)
            
            val stability = state.world.globalStability
            val tint = when {
                stability < 15 -> Color(0x66FF0000) // Dark Red Glitch
                stability < 35 -> Color(0x33AA0000) // Faint Red
                stability < 60 -> Color(0x22000000) // Dimming
                else -> Color.Transparent
            }

            val messageRes = when {
                stability < 20 -> R.string.stability_critical
                stability < 40 -> R.string.stability_low
                stability < 70 -> R.string.stability_medium
                else -> R.string.stability_high
            }

            _uiState.update { 
                it.copy(
                    locationName = city?.name ?: "",
                    locationNameRes = if (city == null) R.string.hub_location_unknown else null,
                    day = state.world.day,
                    timeOfDay = state.world.timeOfDay,
                    gold = state.gold,
                    activeQuestsCount = state.quest.activeQuestIds.size,
                    expeditionQuestsCount = questEngine.getActiveQuestsForCity(currentCityId).size,
                    party = state.party,
                    worldStability = stability,
                    hubBackground = city?.backgroundDrawable ?: "bg_generic_city",
                    hubTintColor = tint,
                    atmosphericMessageRes = messageRes,
                    latestLogs = logs.takeLast(5).reversed()
                )
            }
        }.launchIn(viewModelScope)
    }
}
