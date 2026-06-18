package com.grimreich.ui.combat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.CombatState
import com.grimreich.systems.CombatSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CombatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameRepository.state.combat)
    val uiState: StateFlow<CombatState> = _uiState.asStateFlow()

    fun attack() {
        if (GameRepository.state.combat.active) {
            CombatSystem.playerAttack()
            refresh()
        }
    }

    fun defend() {
        if (GameRepository.state.combat.active) {
            CombatSystem.playerDefend()
            refresh()
        }
    }

    fun useSpecial(type: String) {
        if (GameRepository.state.combat.active) {
            CombatSystem.playerUseSpecial(type)
            refresh()
        }
    }

    fun flee() {
        val c = GameRepository.state.combat
        if (c.active) {
            c.active = false
            c.log.add("Uciekłeś z walki!")
            GameRepository.state.pendingQuestId = null
            refresh()
        }
    }

    fun refresh() {
        _uiState.update { GameRepository.state.combat.copy() }
    }
}
