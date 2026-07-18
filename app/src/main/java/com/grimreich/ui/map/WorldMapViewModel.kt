package com.grimreich.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.TravelSystem
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
    val cityQuestCounts: Map<String, Int> = emptyMap(),
    val worldStability: Int = 100
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

    fun travelToSelected(onDone: () -> Unit) {
        val cityId = _uiState.value.selectedCityId ?: return
        travelSystem.travelTo(cityId)
        onDone()
    }

    private fun refresh() {
        gameRepository.gameState
            .onEach { state ->
                val allCities = cityCatalogue.all()
                val counts = allCities.associate { city ->
                    city.id to questEngine.getActiveQuestsForCity(city.id).size
                }

                _uiState.update { 
                    it.copy(
                        discoveredLocations = state.world.discoveredLocations.toList(),
                        currentLocationId = state.world.locationId,
                        allCities = allCities,
                        selectedCityData = cityCatalogue.get(it.selectedCityId),
                        cityQuestCounts = counts,
                        worldStability = state.world.globalStability
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
