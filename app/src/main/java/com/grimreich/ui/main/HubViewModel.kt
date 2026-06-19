package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.world.CityCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HubUiState(
    val locationName: String = "",
    val day: Int = 1,
    val timeOfDay: String = "Poranek",
    val party: List<Hero> = emptyList(),
    val activeQuestsCount: Int = 0,
    val gold: Int = 0
)

@HiltViewModel
class HubViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val cityCatalogue: CityCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(HubUiState())
    val uiState: StateFlow<HubUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val rawId = state.grimCurrentRegion
        val cityId = rawId.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")

        val cityData = cityCatalogue.get(cityId)

        _uiState.update { 
            it.copy(
                locationName = (cityData?.name ?: state.world.location).uppercase(),
                day = state.world.day,
                timeOfDay = state.world.timeOfDay,
                party = state.party.toList(),
                activeQuestsCount = state.quest.activeQuests.size,
                gold = state.gold
            )
        }
    }
}
