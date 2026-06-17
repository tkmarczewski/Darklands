package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.SocialEventSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CityUiState(
    val cityName: String = "",
    val cityStatus: String = "Miasto spowite mrokiem.",
    val backgroundDrawable: String = "bg_region_north_coast",
    val activeQuestsCount: Int = 0
)

class CityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = GameRepository.state
        val cityId = state.grimCurrentRegion ?: state.world.location.lowercase().replace(" ", "_")
        val cityData = CityCatalogue.get(cityId)
        
        val activeCityQuests = state.quest.activeQuests.mapNotNull { com.grimreich.systems.QuestSystem.getQuest(it) }
            .filter { it.cityId == cityId }

        _uiState.update { 
            it.copy(
                cityName = (cityData?.name ?: cityId.replace("_", " ")).uppercase(),
                cityStatus = SocialEventSystem.cityAudience(cityId, null),
                backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                activeQuestsCount = activeCityQuests.size
            )
        }
    }
}
