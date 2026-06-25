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
    val isGlitchActive: Boolean = false,
    val priceModifier: Float = 1.0f,
    val glitchIntensity: Float = 1.0f
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
                
                val generatedNpcs = npcGenerator.generateForCity(cityId, state)

                val stability = state.world.globalStability
                val isCorrupted = stability < GameConstants.STABILITY_THRESHOLD_LOW
                val isGrim20 = stability < 35

                // Calculate Glitch Intensity based on echo and stability
                val baseGlitch = if (ontologicalEngine.isGlitchActive()) 1.2f else 0f
                val stabilityFactor = (100 - stability) / 100f // 0.0 to 1.0
                val finalGlitchIntensity = (baseGlitch + state.world.echoIntensity + stabilityFactor * 2f).coerceAtMost(5f)

                val bg = if (isCorrupted && cityData?.corruptedBackgroundDrawable != null) {
                    cityData.corruptedBackgroundDrawable
                } else {
                    cityData?.backgroundDrawable ?: "bg_region_north_coast"
                }

                val transformedCityName = if (isGrim20) {
                    "KRYPTA_PROCESU_${cityId.uppercase().take(3)}_${cityId.hashCode().toString().takeLast(4)}"
                } else {
                    (cityData?.name ?: "Nieznane Miejsce").uppercase()
                }

                val transformedCityStatus = if (isGrim20) {
                    "OSTRZEŻENIE: Spójność danych krytycznie niska. Próba odzyskania narracji... NIEPOWODZENIE. Lokacja oznaczona przez Skrybę jako 'DO WYMAZANIA'."
                } else {
                    (cityData?.loreDescription ?: socialEventSystem.cityAudience(cityId, null))
                }

                // Reputation-based pricing
                val factionId = when (cityData?.rulingFaction?.lowercase()) {
                    "zakon switu" -> "zakon"
                    "inkwizycja" -> "inkwizycja"
                    "klasztor milczenia" -> "milczenie"
                    else -> null
                }
                var baseModifier = cityData?.priceModifier ?: 1.0f
                factionId?.let { fid ->
                    val score = state.reputation.globalFactions[fid] ?: 0
                    val level = com.grimreich.grimreich.v1.ReputationLevel.fromScore(score)
                    baseModifier *= when (level) {
                        com.grimreich.grimreich.v1.ReputationLevel.EXALTED -> 0.8f // 20% discount
                        com.grimreich.grimreich.v1.ReputationLevel.FRIENDLY -> 0.9f // 10% discount
                        com.grimreich.grimreich.v1.ReputationLevel.HOSTILE -> 1.2f // 20% markup
                        com.grimreich.grimreich.v1.ReputationLevel.HATED -> 1.5f // 50% markup
                        else -> 1.0f
                    }
                }

                _uiState.update { 
                    it.copy(
                        cityName = transformedCityName,
                        cityStatus = transformedCityStatus,
                        backgroundDrawable = bg,
                        activeQuestsCount = localActiveUrban.size,
                        npcs = generatedNpcs,
                        activeLocalQuests = localActiveUrban,
                        isGlitchActive = finalGlitchIntensity > 0.5f,
                        priceModifier = baseModifier,
                        glitchIntensity = finalGlitchIntensity
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
