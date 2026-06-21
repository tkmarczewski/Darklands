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
    val selectedCityData: CityData? = null,
    val cityQuestCounts: Map<String, Int> = emptyMap() // NEW
)

@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val travelSystem: TravelSystem,
    private val cityCatalogue: CityCatalogue,
    private val questSystem: com.grimreich.systems.QuestSystem
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
        // Define canonical IDs to filter locations visible from start
        val canonicalIds = setOf(
            "wybrzeze_polnocne", 
            "rowniny_koronne", 
            "twierdza_zakonu", 
            "serce_krainy", 
            "poludniowe_ruiny", 
            "gory_poludniowe", 
            "pogranicze_stepowe", 
            "ziemie_dzikie"
        )
        
        // Calculate quest counts per city
        val questCounts = state.quest.activeQuests
            .mapNotNull { questSystem.getQuest(it) }
            .groupBy { it.cityId }
            .mapValues { it.value.size }

        _uiState.update { 
            it.copy(
                discoveredLocations = state.world.discoveredLocations.toList(),
                currentLocationId = state.grimCurrentRegion,
                allCities = cityCatalogue.all().filter { city ->
                    canonicalIds.contains(city.id) || state.world.discoveredLocations.contains(city.id)
                },
                cityQuestCounts = questCounts
            )
        }
    }
}
