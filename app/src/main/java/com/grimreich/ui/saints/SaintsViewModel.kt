package com.grimreich.ui.saints

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.ChurchSystem
import com.grimreich.systems.ReligionSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        refresh()
    }

    fun pray() {
        val hero = gameRepository.currentState().party.firstOrNull() ?: return
        val msg = churchSystem.pray(hero)
        updateLog(msg)
        refresh()
    }

    fun makeOffering(amount: Int) {
        val msg = churchSystem.makeOffering(amount)
        updateLog(msg)
        refresh()
    }

    fun cleanse() {
        val hero = gameRepository.currentState().party.firstOrNull() ?: return
        val msg = churchSystem.cleanseRelic(hero)
        updateLog(msg)
        refresh()
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
