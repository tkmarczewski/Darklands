package com.grimreich.ui.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.R
import com.grimreich.core.GameConstants
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.EndingSystem
import com.grimreich.systems.QuestEngine
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

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
    val atmosphericMessageRes: Int = com.grimreich.R.string.stability_high,
    val latestLogs: List<String> = emptyList(),
    val hasPendingLevelUp: Boolean = false
)

@HiltViewModel
class HubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val visualContentSystem: com.grimreich.systems.VisualContentSystem,
    private val atmosphericLogSystem: com.grimreich.systems.AtmosphericLogSystem,
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
            val currentCityId = state.world.locationId
            val city = cityCatalogue.get(currentCityId)
            
            val stability = state.world.globalStability
            val tint = when {
                stability < 15 -> Color(GameConstants.ANOMALY_TINT_ALPHA shl 24 or 0xFF0000)
                stability < GameConstants.STABILITY_THRESHOLD_LOW + 5 -> Color(0x33AA0000)
                stability < GameConstants.STABILITY_THRESHOLD_HIGH - 10 -> Color(0x22000000)
                else -> Color.Transparent
            }

            val messageRes = when {
                stability < GameConstants.STABILITY_THRESHOLD_CRITICAL -> com.grimreich.R.string.stability_critical
                stability < GameConstants.STABILITY_THRESHOLD_LOW + 10 -> com.grimreich.R.string.stability_low
                stability < GameConstants.STABILITY_THRESHOLD_HIGH -> com.grimreich.R.string.stability_medium
                else -> com.grimreich.R.string.stability_high
            }

            val dailySeed = state.world.day.toLong() + state.world.locationId.hashCode()
            val playerName = state.playerName ?: "Wędrowiec"
            val heroName = state.party.firstOrNull { !it.isDead }?.name ?: state.heroName ?: "Kotwica"
            
            val quote = atmosphericLogSystem.getRandomMessage(dailySeed, playerName, heroName)

            _uiState.update { 
                it.copy(
                    locationName = city?.name ?: "",
                    locationNameRes = if (city == null) com.grimreich.R.string.hub_location_unknown else null,
                    day = state.world.day,
                    timeOfDay = state.world.timeOfDay,
                    gold = state.gold,
                    activeQuestsCount = state.quest.activeQuestIds.size,
                    expeditionQuestsCount = state.quest.activeQuestIds.count { id -> 
                        val def = questEngine.getDefinition(id)
                        def != null && def.cityId == currentCityId
                    }.coerceAtLeast(1), // FORCED MIN 1 TO ENSURE BUTTON VISIBILITY
                    party = state.party,
                    worldStability = stability,
                    hubBackground = city?.backgroundDrawable ?: "bg_generic_city",
                    hubTintColor = tint,
                    atmosphericMessageRes = messageRes,
                    latestLogs = listOf(quote) + logs.takeLast(GameConstants.LATEST_LOGS_DISPLAY_COUNT).reversed(),
                    hasPendingLevelUp = state.party.any { h -> h.attributePoints > 0 },
                    expeditionQuestsCount = 1 // FORCING VISIBILITY FOR DEBUG - SHOULD BE state.quest.activeQuestIds.size in future
                )
            }
        }.launchIn(viewModelScope)
    }
}
