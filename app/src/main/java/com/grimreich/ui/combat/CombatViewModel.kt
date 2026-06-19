package com.grimreich.ui.combat

import androidx.lifecycle.ViewModel
import com.grimreich.core.CombatState
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.CombatSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CombatUiState(
    val combat: CombatState,
    val party: List<Hero> = emptyList()
)

@HiltViewModel
class CombatViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val combatSystem: CombatSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CombatUiState(
            combat = gameRepository.currentState().combat.copy(),
            party = gameRepository.currentState().party.toList()
        )
    )
    val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()

    fun attack() {
        if (combatSystem.isCombatActive()) {
            combatSystem.playerAttack()
            refresh()
        }
    }

    fun defend() {
        if (combatSystem.isCombatActive()) {
            combatSystem.playerDefend()
            refresh()
        }
    }

    fun useSpecial(type: String) {
        if (combatSystem.isCombatActive()) {
            combatSystem.playerUseSpecial(type)
            refresh()
        }
    }

    fun flee() {
        val state = gameRepository.currentState()
        val c = state.combat
        if (c.active) {
            c.active = false
            c.log.add("Uciekłeś z walki!")
            state.pendingQuestId = null
            gameRepository.persistCurrentState()
            refresh()
        }
    }

    fun refresh() {
        val state = gameRepository.currentState()
        _uiState.update { 
            it.copy(
                combat = state.combat.copy(),
                party = state.party.toList()
            ) 
        }
    }
}
