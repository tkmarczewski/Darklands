package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.HeroPool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RecruitmentUiState(
    val availableHeroes: List<Hero> = emptyList(),
    val gold: Int = 0
)

@HiltViewModel
class RecruitmentViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val dialogueManager: DialogueManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecruitmentUiState())
    val uiState: StateFlow<RecruitmentUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        
        // Use real hireableHeroes from state
        if (state.hireableHeroes.isEmpty()) {
            state.hireableHeroes.addAll(HeroPool.generatePool(state.grimCurrentRegion, 3))
        }

        _uiState.update { it.copy(availableHeroes = state.hireableHeroes.toList(), gold = state.gold) }
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        if (state.gold >= 50) {
            state.gold -= 50
            state.party.add(hero)
            state.hireableHeroes.remove(hero)
            gameRepository.persistCurrentState()
            refresh()
        }
    }

    fun rerollRecruits() {
        val state = gameRepository.currentState()
        if (state.gold >= 10) {
            state.gold -= 10
            state.hireableHeroes.clear()
            state.hireableHeroes.addAll(HeroPool.generatePool(state.grimCurrentRegion, 3))
            gameRepository.persistCurrentState()
            refresh()
        }
    }
}
