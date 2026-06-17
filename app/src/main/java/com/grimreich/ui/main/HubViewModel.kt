package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.QuestSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HubUiState(
    val locationName: String = "",
    val day: Int = 1,
    val timeOfDay: String = "Poranek",
    val party: List<Hero> = emptyList(),
    val activeQuestsCount: Int = 0,
    val gold: Int = 0
)

class HubViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HubUiState())
    val uiState: StateFlow<HubUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = GameRepository.state
        val cityId = state.grimCurrentRegion ?: state.world.location.lowercase().replace(" ", "_")
        val cityData = com.grimreich.world.CityCatalogue.get(cityId)

        _uiState.update { 
            it.copy(
                locationName = (cityData?.name ?: cityId.replace("_", " ")).uppercase(),
                day = state.world.day,
                timeOfDay = state.world.timeOfDay,
                party = state.party.toList(),
                activeQuestsCount = state.quest.activeQuests.size,
                gold = state.gold
            )
        }
    }
}
