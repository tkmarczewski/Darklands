package com.grimreich.ui.combat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.CombatState
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.core.CombatSkill
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.SkillCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CombatUiState(
    val combat: CombatState = CombatState(),
    val party: List<Hero> = emptyList(),
    val potions: List<Item> = emptyList(),
    val availableSkills: List<CombatSkill> = emptyList(),
    val worldStability: Int = 100,
    val ontologicalLevel: com.grimreich.grimreich.v1.OntologicalLevel = com.grimreich.grimreich.v1.OntologicalLevel.MATERIAL
)

@HiltViewModel
class CombatViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val combatSystem: CombatSystem
) : ViewModel() {

    val uiState: StateFlow<CombatUiState> = gameRepository.gameState
        .map { state ->
            CombatUiState(
                combat = state.combat,
                party = state.party,
                potions = state.inventory.filter { it.effects.containsKey("heal") },
                availableSkills = SkillCatalogue.allSkills,
                worldStability = state.world.globalStability,
                ontologicalLevel = state.world.ontologicalLevel
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CombatUiState()
        )

    init {
        // No longer need manual collection into _uiState
    }

    fun attack() {
        combatSystem.playerAttack()
    }

    fun defend() {
        combatSystem.playerDefend()
    }

    fun useSkill(skillId: String) {
        combatSystem.useSkill(skillId)
    }

    fun usePotion(itemId: String) {
        combatSystem.usePotion(itemId)
    }

    fun useEchoSkill(type: String) {
        combatSystem.useEchoSkill(type)
    }
    
    fun selectHero(heroId: String) {
        combatSystem.setActiveHero(heroId)
    }

    fun exitCombat(onExit: () -> Unit) {
        gameRepository.updateState { 
            it.combat.active = false 
            it.combat.log.clear()
        }
        onExit()
    }
}
