package com.grimreich.ui.map

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.TravelSystem
import com.grimreich.world.CityCatalogue
import com.grimreich.world.CityData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MapUiState(
    val discoveredLocations: List<String> = emptyList(),
    val selectedCityId: String? = null,
    val currentLocationId: String = "",
    val allCities: List<CityData> = emptyList(),
    val selectedCityData: CityData? = null
)

@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val travelSystem: TravelSystem,
    private val cityCatalogue: CityCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectCity(cityId: String?) {
        val cityData = cityId?.let { cityCatalogue.get(it) }
        _uiState.update { it.copy(selectedCityId = cityId, selectedCityData = cityData) }
    }

    fun travelToSelected(onSuccess: () -> Unit) {
        val cityId = _uiState.value.selectedCityId ?: return
        travelSystem.travelTo(cityId, null)
        refresh()
        onSuccess()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        _uiState.update { 
            it.copy(
                discoveredLocations = state.world.discoveredLocations.toList(),
                currentLocationId = state.grimCurrentRegion,
                allCities = cityCatalogue.all()
            )
        }
    }
}
