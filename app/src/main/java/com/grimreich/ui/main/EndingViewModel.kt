package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
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
        val s = gameRepository.currentState()
        s.persistentMeta.apply {
            totalSessionsFinished += 1
            unlockedLegacyBuffs.add("SCRIBES_EYE")
            maxMetaAwarenessReached = maxOf(maxMetaAwarenessReached, s.metaAwarenessLevel)
        }
        gameRepository.log("WYBRANO: ASCENDENCJA. Zostałeś Skrybą. Odblokowano: OKO SKRYBY.")
        gameRepository.persistCurrentState()
    }

    fun reboot() {
        val s = gameRepository.currentState()
        s.persistentMeta.apply {
            totalSessionsFinished += 1
            unlockedLegacyBuffs.add("REINFORCED_ANCHOR")
            maxMetaAwarenessReached = maxOf(maxMetaAwarenessReached, s.metaAwarenessLevel)
        }
        gameRepository.log("WYBRANO: REBOOT. Sesja odświeżona. Odblokowano: WZMOCNIONA KOTWICA.")
        gameRepository.persistCurrentState()
    }

    fun delete() {
        // True deletion - clear even persistent meta? 
        // Plan says "Wipes the meta-data as well".
        gameRepository.replaceState(GameState())
        gameRepository.log("WYBRANO: DESTRUKCJA. Świat i dziedzictwo wymazane.")
        gameRepository.persistCurrentState()
    }
}
