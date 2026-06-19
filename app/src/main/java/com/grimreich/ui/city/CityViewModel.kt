package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.SocialEventSystem
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.grimreich.v1.NPC
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CityUiState(
    val cityName: String = "Ładowanie...",
    val cityStatus: String = "Skanowanie rzeczywistości...",
    val backgroundDrawable: String = "bg_region_north_coast",
    val activeQuestsCount: Int = 0,
    val npcs: List<NPC> = emptyList()
)

@HiltViewModel
class CityViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val cityCatalogue: CityCatalogue,
    private val npcGenerator: ProceduralNpcGenerator,
    private val socialEventSystem: SocialEventSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        cityCatalogue.seedCanonical()
        
        val rawId = state.grimCurrentRegion
        val cityId = rawId.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")

        val cityData = cityCatalogue.get(cityId)
        questSystem.seedIntegratedContent()
        
        val activeCount = state.quest.activeQuests.mapNotNull { questSystem.getQuest(it) }.count { it.cityId == cityId }
        val availableCount = questSystem.availableForCity(cityId).size
        val totalCount = activeCount + availableCount

        val seed = state.world.day + cityId.hashCode()
        val generatedNpcs = npcGenerator.generateForCity(cityId, seed)

        _uiState.update { 
            it.copy(
                cityName = (cityData?.name ?: "Nieznane Miejsce").uppercase(),
                cityStatus = socialEventSystem.cityAudience(cityId, null),
                backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                activeQuestsCount = totalCount,
                npcs = generatedNpcs
            )
        }
    }

    fun startDialogue(name: String, role: String, node: String, onStart: () -> Unit) {
        val state = gameRepository.currentState()
        state.pendingDialogueNpcName = name
        state.pendingDialogueNpcRole = role
        state.pendingDialogueNodeId = node
        gameRepository.persistCurrentState()
        onStart()
    }

    fun openQuestNode(onNpcClick: (String, String, String) -> Unit) {
        val state = gameRepository.currentState()
        val rawCity = state.grimCurrentRegion
        val cityId = rawCity.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")

        val quest = questSystem.availableForCity(cityId).firstOrNull()
            ?: questSystem.all().find { it.status == QuestStatus.AKTYWNE && it.cityId == cityId }
        
        if (quest != null) {
            val node = if (quest.id.startsWith("q_start")) "aelion_start" else "mystic_start"
            onNpcClick(quest.originRefId, quest.originRefId, node)
        }
    }
}
