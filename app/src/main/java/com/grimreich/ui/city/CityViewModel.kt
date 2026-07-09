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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val npcGenerator: ProceduralNpcGenerator,
    private val socialEventSystem: SocialEventSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        android.util.Log.e("CityViewModel", "INIT: Observing GameState flow...")
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
        
        // Szukamy zadania gotowego do oddania u tego NPC
        val questToComplete = state.quest.progress.values.find {
            val def = questEngine.getDefinition(it.questId)
            it.status == QuestStatus.OBJECTIVE_MET && 
            def?.cityId == cityId && def.originNpcId.lowercase() == role.lowercase()
        }

        val targetNode = if (questToComplete != null) {
            when (questToComplete.questId) {
                "q_verdict_1" -> "guard_verdict_done"
                "q_deserter" -> "guard_deserter_done"
                "q_coast_harvest" -> "merchant_report_back"
                "q_scribes_1" -> "mira_report_back"
                else -> when (role.lowercase()) {
                    "guard", "straznik" -> "guard_report_back"
                    "merchant", "kupiec" -> "merchant_report_back"
                    "aelion" -> "aelion_quest"
                    else -> "quest_report_back_generic"
                }
            }
        } else node

        android.util.Log.i("CityViewModel", "[DIALOGUE] Starting dialogue with $name ($role). Node: $targetNode. Quest to complete: ${questToComplete?.questId}")

        gameRepository.updateState { s ->
            s.pendingDialogueNpcName = name
            s.pendingDialogueNpcRole = role
            s.pendingDialogueNodeId = targetNode
            if (questToComplete != null) {
                s.pendingQuestId = "FINALIZE:${questToComplete.questId}"
            } else {
                s.pendingQuestId = null
            }
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
        
        val quests = questEngine.getAllRelevantQuestsForCity(cityId, state)
        val generatedNpcs = npcGenerator.generateForCity(cityId, state)

        val stability = state.world.globalStability
        val isGrim20 = stability < 35
        val finalGlitchIntensity = (state.world.echoIntensity + (100 - stability) / 50f).coerceAtMost(5f)

        android.util.Log.i("CityViewModel", "[REFRESH] City: $cityId, Quests: ${quests.size}")

        _uiState.update { 
            it.copy(
                cityName = if (isGrim20) "KRYPTA_PROCESU" else cityData?.name ?: "Nieznane",
                cityStatus = cityData?.loreDescription ?: socialEventSystem.cityAudience(cityId, stability),
                backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
                activeQuestsCount = quests.size,
                npcs = generatedNpcs,
                activeLocalQuests = quests,
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
    val activeQuestsCount: Int = 0,
    val npcs: List<NPC> = emptyList(),
    val activeLocalQuests: List<QuestDefinition> = emptyList(),
    val isQuestMenuOpen: Boolean = false,
    val isGlitchActive: Boolean = false,
    val glitchIntensity: Float = 1.0f,
    val rulingFactionName: String = "Neutralna"
)
