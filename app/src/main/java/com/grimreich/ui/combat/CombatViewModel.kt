package com.grimreich.ui.combat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.CombatState
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.QuestEngine
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
    private val questEngine: QuestEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(CombatUiState())
    val uiState: StateFlow<CombatUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                _uiState.update { 
                    it.copy(
                        combat = state.combat,
                        party = state.party.toList(),
                        potions = state.inventory.filter { i -> i.type == "potion" }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun attack() {
        combatSystem.playerAttack()
    }

    fun defend() {
        combatSystem.playerDefend()
    }

    fun usePotion(itemId: String) {
        combatSystem.usePotion(itemId)
    }

    fun useEchoSkill(skillType: String) {
        combatSystem.useEchoSkill(skillType)
    }

    fun exitCombat(onExit: () -> Unit) {
        val state = gameRepository.currentState()
        if (!state.combat.active) {
            onExit()
        }
    }
}
