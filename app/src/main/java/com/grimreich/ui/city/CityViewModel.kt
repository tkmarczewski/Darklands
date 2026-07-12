package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CityUiEvent {
    data class ToggleQuestMenu(val open: Boolean) : CityUiEvent
    data class OnNpcClick(val npc: NPC) : CityUiEvent
    data class OnQuestClick(val quest: QuestDefinition) : CityUiEvent
    data object OnExitClick : CityUiEvent
    data object OnMarketClick : CityUiEvent
    data object OnAlchemyClick : CityUiEvent
    data object OnTavernClick : CityUiEvent
    data object OnTempleClick : CityUiEvent
    data object OnRecruitClick : CityUiEvent
}

sealed interface CityUiEffect {
    data class NavigateToDialogue(val name: String, val role: String, val node: String) : CityUiEffect
    data object NavigateToExit : CityUiEffect
    data object NavigateToMarket : CityUiEffect
    data object NavigateToAlchemy : CityUiEffect
    data object NavigateToTavern : CityUiEffect
    data object NavigateToTemple : CityUiEffect
    data object NavigateToRecruit : CityUiEffect
}

@HiltViewModel
class CityViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val cityCatalogue: CityCatalogue,
    private val npcGenerator: ProceduralNpcGenerator,
    private val socialEventSystem: SocialEventSystem,
    private val atmosphericDescriptionSystem: AtmosphericDescriptionSystem,
    private val verdictIncidentsSystem: com.grimreich.systems.VerdictIncidentsSystem
) : ViewModel() {

    private val _isQuestMenuOpen = MutableStateFlow(false)

    val uiState: StateFlow<CityUiState> = combine(
        gameRepository.gameState,
        _isQuestMenuOpen
    ) { state, questMenuOpen ->
        val cityId = state.grimCurrentRegion
        val cityData = cityCatalogue.get(cityId)
        
        // --- TRIGGER VERDICT INCIDENTS ---
        // DESIGN CHOICE: Triggering here ensures that every meaningful "city entry" 
        // through UI navigation is recorded.
        verdictIncidentsSystem.onCityEntered(cityId)

        val localAvailable = questEngine.getAvailableQuestsForCity(cityId, state)
        val allAvailable = questEngine.getVisibleQuestBoard(state)
        val generatedNpcs = npcGenerator.generateForCity(cityId, state)

        val stability = state.world.globalStability
        val isGrim20 = stability < 35
        val finalGlitchIntensity = (state.world.echoIntensity + (100 - stability) / 50f).coerceAtMost(5f)

        CityUiState(
            cityName = if (isGrim20) "KRYPTA_PROCESU" else cityData?.name ?: "Nieznane",
            cityStatus = atmosphericDescriptionSystem.getCityDescription(cityId),
            backgroundDrawable = cityData?.backgroundDrawable ?: "bg_region_north_coast",
            npcs = generatedNpcs,
            activeLocalQuests = localAvailable,
            allAvailableQuests = allAvailable,
            isQuestMenuOpen = questMenuOpen,
            isGlitchActive = finalGlitchIntensity > 0.5f,
            glitchIntensity = finalGlitchIntensity,
            rulingFactionName = com.grimreich.core.FactionCatalogue.findById(cityData?.rulingFaction ?: "")?.name ?: "Neutralna"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CityUiState()
    )

    private val _uiEffect = MutableSharedFlow<CityUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        // init body is now empty as state is handled via stateIn
    }

    fun onEvent(event: CityUiEvent) {
        when (event) {
            is CityUiEvent.ToggleQuestMenu -> _isQuestMenuOpen.value = event.open
            is CityUiEvent.OnNpcClick -> startNpcDialogue(event.npc)
            is CityUiEvent.OnQuestClick -> selectQuestAndOpenDialogue(event.quest)
            CityUiEvent.OnExitClick -> emitEffect(CityUiEffect.NavigateToExit)
            CityUiEvent.OnMarketClick -> emitEffect(CityUiEffect.NavigateToMarket)
            CityUiEvent.OnAlchemyClick -> emitEffect(CityUiEffect.NavigateToAlchemy)
            CityUiEvent.OnTavernClick -> emitEffect(CityUiEffect.NavigateToTavern)
            CityUiEvent.OnTempleClick -> emitEffect(CityUiEffect.NavigateToTemple)
            CityUiEvent.OnRecruitClick -> emitEffect(CityUiEffect.NavigateToRecruit)
        }
    }

    private fun emitEffect(effect: CityUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    private fun startNpcDialogue(npc: NPC) {
        startDialogue(npc.name, npc.role, npc.startNodeId ?: "generic_start")
    }

    private fun startDialogue(name: String, role: String, node: String) {
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
            if (questToComplete != null) {
                s.pendingAction = com.grimreich.core.PendingWorldAction.Dialogue(
                    npcName = name,
                    npcRole = role,
                    nodeId = targetNode,
                    relatedQuestId = questToComplete.questId
                )
            } else {
                s.pendingAction = com.grimreich.core.PendingWorldAction.Dialogue(
                    npcName = name,
                    npcRole = role,
                    nodeId = targetNode
                )
            }
        }
        
        emitEffect(CityUiEffect.NavigateToDialogue(name, role, targetNode))
    }

    private fun selectQuestAndOpenDialogue(quest: QuestDefinition) {
        _isQuestMenuOpen.value = false
        val status = questEngine.getStatus(quest.id)
        
        val targetNode = if (status == QuestStatus.ACTIVE || status == QuestStatus.OBJECTIVE_MET) {
            "${quest.originNpcId.lowercase()}_quest_check"
        } else {
            "${quest.originNpcId.lowercase()}_start"
        }

        startDialogue(quest.originNpcId.uppercase(), quest.originNpcId, targetNode)
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
