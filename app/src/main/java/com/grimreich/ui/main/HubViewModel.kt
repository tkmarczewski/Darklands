package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
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

data class HubUiState(
    val locationName: String = "",
    val day: Int = 1,
    val timeOfDay: String = "",
    val gold: Int = 0,
    val activeQuestsCount: Int = 0,
    val expeditionQuestsCount: Int = 0,
    val party: List<Hero> = emptyList(),
    val worldStability: Int = 100
)

@HiltViewModel
class HubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val cityCatalogue: CityCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(HubUiState())
    val uiState: StateFlow<HubUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                val currentCityId = rawIdToSlug(state.grimCurrentRegion)
                val city = cityCatalogue.get(currentCityId)
                val active = state.quest.activeQuests.mapNotNull { questSystem.getQuest(it) }
                val expeditionCount = active.count { it.cityId == currentCityId && it.isOutsideCity }

                _uiState.update { 
                    it.copy(
                        locationName = city?.name ?: "Pustka",
                        day = state.world.day,
                        timeOfDay = state.world.timeOfDay,
                        gold = state.gold,
                        activeQuestsCount = active.size,
                        expeditionQuestsCount = expeditionCount,
                        party = state.party.toList(),
                        worldStability = state.world.globalStability
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun rawIdToSlug(rawId: String): String {
        return rawId.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")
    }
}
