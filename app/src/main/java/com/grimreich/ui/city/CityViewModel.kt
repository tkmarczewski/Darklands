package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.SocialEventSystem
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.grimreich.v1.NPC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CityUiState(
    val cityName: String = "",
    val cityStatus: String = "Miasto spowite mrokiem.",
    val backgroundDrawable: String = "bg_region_north_coast",
    val activeQuestsCount: Int = 0,
    val npcs: List<NPC> = emptyList()
)

class CityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = GameRepository.state
        // NORMALIZE cityId to avoid Polish character mismatches in matching
        val rawId = state.grimCurrentRegion ?: state.world.location
        val cityId = rawId.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")

        val cityData = CityCatalogue.get(cityId)
        
        val activeCityQuests = state.quest.activeQuests.mapNotNull { com.grimreich.systems.QuestSystem.getQuest(it) }
            .filter { it.cityId == cityId }
            
        val availableCityQuests = com.grimreich.systems.QuestSystem.availableForCity(cityId)
        
        // Use static seed based on day to avoid flickering npcs on rotation/recomposition
        val sessionSeed = state.world.day + cityId.hashCode()
        val npcs = ProceduralNpcGenerator.generateForCity(cityId, sessionSeed)

        _uiState.update { 
            it.copy(
                cityName = (cityData?.name ?: cityId.replace("_", " ")).uppercase(),
                cityStatus = SocialEventSystem.cityAudience(cityId, null),
                backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                activeQuestsCount = activeCityQuests.size + availableCityQuests.size,
                npcs = npcs
            )
        }
    }
}
