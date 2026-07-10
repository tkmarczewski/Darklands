package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.QuestStatus
import com.grimreich.world.CityCatalogue
import com.grimreich.systems.SocialEventSystem
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.grimreich.v1.NPC
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestDefinition
import com.grimreich.systems.AtmosphericDescriptionSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val npcGenerator: ProceduralNpcGenerator,
    private val socialEventSystem: SocialEventSystem,
    private val atmosphericDescriptionSystem: AtmosphericDescriptionSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { refresh() }
            .launchIn(viewModelScope)
    }

    fun toggleQuestMenu(open: Boolean) {
        _uiState.update { it.copy(isQuestMenuOpen = open) }
        if (open) refresh()
    }

    fun startDialogue(name: String, role: String, node: String, onStart: () -> Unit) {
        val state = gameRepository.currentState()
        val cityId = state.grimCurrentRegion
        
        val questToComplete = state.quest.progress.values.find {
            val def = questEngine.getDefinition(it.questId)
            it.status == QuestStatus.OBJECTIVE_MET && 
            def?.cityId == cityId && def.originNpcId.lowercase() == role.lowercase()
        }

        val targetNode = if (questToComplete != null) {
            when (role.lowercase()) {
                "guard", "straznik" -> "guard_report_back"
                "merchant", "kupiec" -> "merchant_report_back"
                "mira" -> "mira_report_back"
                else -> "quest_report_back_generic"
            }
        } else node

        gameRepository.updateState { s ->
            s.pendingDialogueNpcName = name
            s.pendingDialogueNpcRole = role
            s.pendingDialogueNodeId = targetNode
            s.pendingQuestId = if (questToComplete != null) "FINALIZE:${questToComplete.questId}" else null
        }
        onStart()
    }

    fun selectQuestAndOpenDialogue(quest: QuestDefinition, onDialogue: () -> Unit) {
        toggleQuestMenu(false)
        val status = questEngine.getStatus(quest.id)
        
        val targetNode = if (status == QuestStatus.ACTIVE || status == QuestStatus.OBJECTIVE_MET) {
            "${quest.originNpcId.lowercase()}_quest_check"
        } else {
            "${quest.originNpcId.lowercase()}_start"
        }

        startDialogue(quest.originNpcId.uppercase(), quest.originNpcId, targetNode, onDialogue)
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val cityId = state.grimCurrentRegion
        val cityData = cityCatalogue.get(cityId)
        
        val localAvailable = questEngine.getAvailableQuestsForCity(cityId, state)
        val allAvailable = questEngine.getVisibleQuestBoard(state)
        val generatedNpcs = npcGenerator.generateForCity(cityId, state)

        val stability = state.world.globalStability
        val isGrim20 = stability < 35
        val finalGlitchIntensity = (state.world.echoIntensity + (100 - stability) / 50f).coerceAtMost(5f)

        _uiState.update { 
            it.copy(
                cityName = if (isGrim20) "KRYPTA_PROCESU" else cityData?.name ?: "Nieznane",
                cityStatus = atmosphericDescriptionSystem.getCityDescription(cityId),
                backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                npcs = generatedNpcs,
                activeLocalQuests = localAvailable,
                allAvailableQuests = allAvailable,
                isQuestMenuOpen = it.isQuestMenuOpen,
                isGlitchActive = finalGlitchIntensity > 0.5f,
                glitchIntensity = finalGlitchIntensity,
                rulingFactionName = cityData?.rulingFaction ?: "Neutralna"
            )
        }
    }
}

data class CityUiState(
    val cityName: String = "Ładowanie...",
    val cityStatus: String = "Skanowanie rzeczywistości...",
    val backgroundDrawable: String = "bg_region_north_coast",
    val npcs: List<NPC> = emptyList(),
    val activeLocalQuests: List<QuestDefinition> = emptyList(),
    val allAvailableQuests: Map<String, List<QuestDefinition>> = emptyMap(),
    val isQuestMenuOpen: Boolean = false,
    val isGlitchActive: Boolean = false,
    val glitchIntensity: Float = 1.0f,
    val rulingFactionName: String = "Neutralna"
)
