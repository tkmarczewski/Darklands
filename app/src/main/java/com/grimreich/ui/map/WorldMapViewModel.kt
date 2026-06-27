package com.grimreich.ui.map

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.TravelSystem
import com.grimreich.systems.QuestEngine
import com.grimreich.world.CityCatalogue
import com.grimreich.world.CityData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MapUiState(
    val discoveredLocations: List<String> = emptyList(),
    val selectedCityId: String? = null,
    val currentLocationId: String = "",
    val allCities: List<CityData> = emptyList(),
    val selectedCityData: CityData? = null,
    val cityQuestCounts: Map<String, Int> = emptyMap()
)

@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val travelSystem: TravelSystem,
    private val cityCatalogue: CityCatalogue,
    private val questEngine: QuestEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectCity(cityId: String?) {
        _uiState.update { it.copy(selectedCityId = cityId, selectedCityData = cityCatalogue.get(cityId)) }
    }

    fun travelToSelected(onArrived: () -> Unit) {
        val dest = _uiState.value.selectedCityId ?: return
        travelSystem.travelTo(dest)
        onArrived()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        
        // Use QuestEngine to get counts per city
        val counts = cityCatalogue.all().associate { city ->
            city.id to questEngine.getActiveQuestsForCity(city.id).size
        }

        _uiState.update {
            it.copy(
                discoveredLocations = state.world.discoveredLocations,
                currentLocationId = state.grimCurrentRegion,
                allCities = cityCatalogue.all(),
                cityQuestCounts = counts
            )
        }
    }
}
