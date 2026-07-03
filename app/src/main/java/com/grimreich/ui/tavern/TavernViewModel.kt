package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.TravelSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TavernUiState(
    val gold: Int = 0,
    val log: String = ""
)

@HiltViewModel
class TavernViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val travelSystem: TravelSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(TavernUiState())
    val uiState: StateFlow<TavernUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun rest() {
        // Atomic: check and deduct inside a single updateState{} to avoid
        // race conditions between the read and the write.
        var canRest = false
        var msg = ""
        gameRepository.updateState { state ->
            if (state.gold >= 50) {
                state.gold -= 50
                canRest = true
                msg = travelSystem.restDirect(state)
            }
        }

        if (!canRest) {
            updateLog("Brak złota na nocleg (50 G).")
            return
        }

        updateLog(msg)
        refresh()
    }

    fun listenToGossip() {
        updateLog("Karczmarz szepcze: 'Mgła gęstnieje na północy... tam, gdzie nic już nie ma.'")
    }

    fun updateLog(msg: String) {
        _uiState.update { it.copy(log = msg) }
    }

    fun refresh() {
        _uiState.update { it.copy(gold = gameRepository.currentState().gold) }
    }
}
