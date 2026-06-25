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
import com.grimreich.core.GameConstants
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
    val isQuestMenuOpen: Boolean = false,
    val isGlitchActive: Boolean = false
)

@HiltViewModel
class CityViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val cityCatalogue: CityCatalogue,
    private val npcGenerator: ProceduralNpcGenerator,
    private val socialEventSystem: SocialEventSystem,
    private val ontologicalEngine: com.grimreich.core.engine.OntologicalEngine
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

                val stability = state.world.globalStability
                val isCorrupted = stability < GameConstants.STABILITY_THRESHOLD_LOW
                val bg = if (isCorrupted && cityData?.corruptedBackgroundDrawable != null) {
                    cityData.corruptedBackgroundDrawable
                } else {
                    cityData?.backgroundDrawable ?: "bg_region_north_coast"
                }

                _uiState.update { 
                    it.copy(
                        cityName = (cityData?.name ?: "Nieznane Miejsce").uppercase(),
                        cityStatus = socialEventSystem.cityAudience(cityId, null),
                        backgroundDrawable = bg,
                        activeQuestsCount = localActiveUrban.size,
                        npcs = generatedNpcs,
                        activeLocalQuests = localActiveUrban,
                        isGlitchActive = ontologicalEngine.isGlitchActive()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleQuestMenu(open: Boolean) {
        _uiState.update { it.copy(isQuestMenuOpen = open) }
    }

    fun startDialogue(name: String, role: String, node: String, onStart: () -> Unit) {
        val state = gameRepository.currentState()
        val cityId = rawIdToSlug(state.grimCurrentRegion)
        
        // Redirect to report back if any quest for this NPC/role is ready for reward
        val questToReport = state.quest.activeQuests
            .mapNotNull { questSystem.getQuest(it) }
            .find { it.cityId == cityId && it.originRefId.lowercase() == role.lowercase() && it.status == QuestStatus.CEL_OSIAGNIETY }

        val targetNode = if (questToReport != null) {
            when (role.lowercase()) {
                "guard", "straznik" -> "guard_report_back"
                "merchant", "kupiec" -> "merchant_report_back"
                "mystic", "mistyk" -> "mystic_report_back"
                "zealot", "pielgrzym" -> "zealot_report_back"
                else -> "quest_report_back_generic"
            }
        } else node

        gameRepository.updateState { s ->
            s.pendingDialogueNpcName = name
            s.pendingDialogueNpcRole = role
            s.pendingDialogueNodeId = targetNode
            if (questToReport != null) {
                s.pendingQuestId = "FINALIZE:${questToReport.id}"
            }
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
