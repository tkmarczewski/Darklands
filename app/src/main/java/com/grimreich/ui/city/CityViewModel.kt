package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.SocialEventSystem
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.grimreich.v1.NPC
import com.grimreich.systems.QuestSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CityUiState(
    val cityName: String = "Ladowanie...",
    val cityStatus: String = "Skanowanie rzeczywistosci...",
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
        
        // Ensure canonical data
        CityCatalogue.seedCanonical()
        
        val rawId = state.grimCurrentRegion
        // STRICT NORMALIZATION
        val cityId = rawId.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")

        val cityData = CityCatalogue.get(cityId)
        
        // FORCE SEED QUESTS FOR THE SESSION
        QuestSystem.seedIntegratedContent()
        
        val activeCount = state.quest.activeQuests.mapNotNull { QuestSystem.getQuest(it) }.count { it.cityId == cityId }
        val availableCount = QuestSystem.availableForCity(cityId).size
        val totalCount = activeCount + availableCount

        // GENERATE NPCs (Deterministic per day/city)
        val seed = state.world.day + cityId.hashCode()
        val generatedNpcs = ProceduralNpcGenerator.generateForCity(cityId, seed)

        _uiState.update { 
            it.copy(
                cityName = (cityData?.name ?: "Nieznane Miejsce").uppercase(),
                cityStatus = SocialEventSystem.cityAudience(cityId, null),
                backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                activeQuestsCount = totalCount,
                npcs = generatedNpcs
            )
        }
    }
}
