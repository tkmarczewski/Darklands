package com.grimreich.ui.combat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.CombatState
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.grimreich.v1.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CombatUiState(
    val combat: CombatState = CombatState(),
    val party: List<Hero> = emptyList(),
    val potions: List<Item> = emptyList()
)

@HiltViewModel
class CombatViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val combatSystem: CombatSystem,
    private val questSystem: QuestSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(CombatUiState())
    val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                _uiState.update { 
                    it.copy(
                        combat = state.combat.copy(),
                        party = state.party.toList(),
                        potions = state.inventory.filter { item -> item.type == "potion" }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun attack() {
        if (combatSystem.isCombatActive()) {
            combatSystem.playerAttack()
        }
    }

    fun defend() {
        if (combatSystem.isCombatActive()) {
            combatSystem.playerDefend()
        }
    }

    fun useSpecial(type: String) {
        if (combatSystem.isCombatActive()) {
            combatSystem.playerUseSpecial(type)
        }
    }

    fun usePotion(itemId: String) {
        if (combatSystem.isCombatActive()) {
            combatSystem.usePotion(itemId)
        }
    }

    fun flee() {
        gameRepository.updateState { 
            it.combat.active = false
            it.combat.log.add("Uciekłeś z walki!")
        }
    }

    fun exitCombat(onExit: () -> Unit) {
        val state = gameRepository.currentState()
        // If combat is finished (not active), handle quest completion if tied to combat
        if (!state.combat.active) {
            state.pendingQuestId?.let { qId ->
                if (qId.startsWith("COMBAT_WIN:")) {
                    questSystem.markObjectiveComplete(qId.removePrefix("COMBAT_WIN:"))
                }
            }
            gameRepository.updateState { 
                it.pendingQuestId = null
                it.combat.log.clear()
            }
        }
        onExit()
    }
}
