package com.grimreich.ui.map

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.TravelSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MapUiState(
    val discoveredLocations: List<String> = emptyList(),
    val selectedCityId: String? = null,
    val currentLocationId: String = ""
)

class WorldMapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectCity(cityId: String?) {
        _uiState.update { it.copy(selectedCityId = cityId) }
    }

    fun travelToSelected(onSuccess: () -> Unit) {
        val cityId = _uiState.value.selectedCityId ?: return
        // Using existing TravelSystem logic
        TravelSystem.travelTo(cityId, null)
        refresh()
        onSuccess()
    }

    fun refresh() {
        val state = GameRepository.state
        _uiState.update { 
            it.copy(
                discoveredLocations = state.world.discoveredLocations.toList(),
                currentLocationId = state.grimCurrentRegion ?: ""
            )
        }
    }
}
