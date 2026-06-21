package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.GameConstants
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
        // Force refresh recruitment pool on city entry logic
        val state = gameRepository.currentState()
        // If pool was never generated for this entry, do it
        state.hireableHeroes.clear()
        state.hireableHeroes.addAll(HeroPool.generatePool(state.grimCurrentRegion, GameConstants.MAX_RECRUITS_POOL_SIZE))
        gameRepository.persistCurrentState()
        
        refresh()
    }

    fun refresh() {
        val state = gameRepository.currentState()
        _uiState.update { it.copy(availableHeroes = state.hireableHeroes.toList(), gold = state.gold) }
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        if (state.gold >= GameConstants.HIRE_HERO_COST) {
            state.gold -= GameConstants.HIRE_HERO_COST
            state.party.add(hero)
            state.hireableHeroes.remove(hero)
            gameRepository.persistCurrentState()
            refresh()
        }
    }

    fun rerollRecruits() {
        val state = gameRepository.currentState()
        if (state.gold >= GameConstants.REROLL_RECRUITS_COST) {
            state.gold -= GameConstants.REROLL_RECRUITS_COST
            state.hireableHeroes.clear()
            state.hireableHeroes.addAll(HeroPool.generatePool(state.grimCurrentRegion, GameConstants.MAX_RECRUITS_POOL_SIZE))
            gameRepository.persistCurrentState()
            refresh()
        }
    }
}
