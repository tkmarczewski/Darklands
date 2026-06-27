package com.grimreich.ui.saints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.systems.ChurchSystem
import com.grimreich.systems.ReligionSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SaintsUiState(
    val partyStatus: String = "",
    val saintsText: String = "",
    val log: String = ""
)

@HiltViewModel
class SaintsViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val churchSystem: ChurchSystem,
    private val religionSystem: ReligionSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaintsUiState())
    val uiState: StateFlow<SaintsUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { refresh() }
            .launchIn(viewModelScope)
    }

    fun pray() {
        val hero = gameRepository.currentState().party.firstOrNull() ?: return
        val msg = churchSystem.pray(hero.id)
        updateLog(msg)
    }

    fun makeOffering(amount: Int) {
        val msg = churchSystem.makeOffering(amount)
        updateLog(msg)
    }

    fun cleanse() {
        val hero = gameRepository.currentState().party.firstOrNull() ?: return
        val msg = churchSystem.cleanseRelic(hero.id)
        updateLog(msg)
    }

    fun updateLog(msg: String) {
        _uiState.update { it.copy(log = msg) }
    }

    fun refresh() {
        val g = gameRepository.currentState()
        val faith = g.prayer.faith
        val virtue = g.prayer.virtue
        
        val status = "Wiara: $faith | Cnota: $virtue"
        
        val saints = """
            SZEPTY PROROKÓW:
            ${religionSystem.getSaintsIntercession()}
        """.trimIndent()

        _uiState.update { 
            it.copy(
                partyStatus = status,
                saintsText = saints
            )
        }
    }
}
