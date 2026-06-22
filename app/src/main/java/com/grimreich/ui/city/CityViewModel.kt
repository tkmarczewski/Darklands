package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.SocialEventSystem
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.grimreich.v1.NPC
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestStatus
import com.grimreich.systems.QuestEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CityUiState(
    val cityName: String = "Ładowanie...",
    val cityStatus: String = "Skanowanie rzeczywistości...",
    val backgroundDrawable: String = "bg_region_north_coast",
    val activeQuestsCount: Int = 0,
    val npcs: List<NPC> = emptyList(),
    val activeLocalQuests: List<QuestEntry> = emptyList(),
    val isQuestMenuOpen: Boolean = false
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
        gameRepository.gameState
            .onEach { state ->
                val rawId = state.grimCurrentRegion
                val cityId = rawIdToSlug(rawId)
                val cityData = cityCatalogue.get(cityId)
                
                val localActiveUrban = state.quest.activeQuests
                    .mapNotNull { questSystem.getQuest(it) }
                    .filter { it.cityId == cityId && !it.isOutsideCity }
                
                val seed = state.world.day + cityId.hashCode() + state.world.cityEntryCount
                val generatedNpcs = npcGenerator.generateForCity(cityId, seed)

                _uiState.update { 
                    it.copy(
                        cityName = (cityData?.name ?: "Nieznane Miejsce").uppercase(),
                        cityStatus = socialEventSystem.cityAudience(cityId, null),
                        backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                        activeQuestsCount = localActiveUrban.size,
                        npcs = generatedNpcs,
                        activeLocalQuests = localActiveUrban
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleQuestMenu(open: Boolean) {
        _uiState.update { it.copy(isQuestMenuOpen = open) }
    }

    fun startDialogue(name: String, role: String, node: String, onStart: () -> Unit) {
        gameRepository.updateState { state ->
            state.pendingDialogueNpcName = name
            state.pendingDialogueNpcRole = role
            state.pendingDialogueNodeId = node
        }
        onStart()
    }

    fun selectQuestAndOpenDialogue(quest: QuestEntry, onDialogue: () -> Unit) {
        toggleQuestMenu(false)
        val node = when (quest.id) {
            "q_blood_icon" -> "blood_icon_start"
            "q_doorless_tower" -> "mystic_tower_info"
            else -> when (quest.originRefId) {
                "guard" -> "guard_start"
                "merchant" -> "merchant_start"
                "zealot" -> "zealot_start"
                "mystic" -> "mystic_start"
                "aelion" -> "aelion_start"
                else -> "mystic_start"
            }
        }
        startDialogue(quest.originRefId.uppercase(), quest.originRefId, node, onDialogue)
    }

    private fun rawIdToSlug(rawId: String): String {
        return rawId.lowercase()
            .replace("ą", "a").replace("ć", "c").replace("ę", "e")
            .replace("ł", "l").replace("ń", "n").replace("ó", "o")
            .replace("ś", "s").replace("ź", "z").replace("ż", "z")
            .replace(" ", "_")
    }
}
