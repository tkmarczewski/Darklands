package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.EndingSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class EndingUiState(
    val summary: String = "",
    val metaAwareness: Int = 0,
    val stability: Int = 100
)

@HiltViewModel
class EndingViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val endingSystem: EndingSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(EndingUiState())
    val uiState: StateFlow<EndingUiState> = _uiState.asStateFlow()

    init {
        val s = gameRepository.currentState()
        _uiState.value = EndingUiState(
            summary = endingSystem.finaleStatus(),
            metaAwareness = s.metaAwarenessLevel,
            stability = s.world.globalStability
        )
    }

    fun ascend() {
        gameRepository.log("WYBRANO: ASCENDENCJA. Zostałeś Skrybą.")
        // Final logic would go here
    }

    fun reboot() {
        gameRepository.log("WYBRANO: REBOOT. Sesja odświeżona.")
        // New Game Plus logic
    }

    fun delete() {
        gameRepository.log("WYBRANO: DESTRUKCJA. Świat wymazany.")
        // Cleanup logic
    }
}
