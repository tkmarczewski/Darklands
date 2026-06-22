package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.GameConstants
import com.grimreich.world.HeroPool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RecruitmentUiState(
    val availableHeroes: List<Hero> = emptyList(),
    val gold: Int = 0,
    val hireCosts: Map<String, Int> = emptyMap()
)

@HiltViewModel
class RecruitmentViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val heroPool: HeroPool
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecruitmentUiState())
    val uiState: StateFlow<RecruitmentUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /**
     * Odświeża pulę — generuje 4 nowych losowych bohaterów.
     * Wywoływane przy każdym wejściu na ekran rekrutacji.
     */
    fun refresh() {
        val state = gameRepository.currentState()
        // If pool is empty, generate it (initial entry)
        if (state.hireableHeroes.isEmpty()) {
            state.hireableHeroes.addAll(heroPool.generatePool(GameConstants.MAX_RECRUITS_POOL_SIZE))
            gameRepository.persistCurrentState()
        }
        
        val costs = state.hireableHeroes.associate { hero ->
            hero.id to (hero.currentCareer?.let { heroPool.hireCostFor(it) } ?: GameConstants.HIRE_HERO_COST)
        }

        _uiState.update {
            it.copy(
                availableHeroes = state.hireableHeroes.toList(),
                gold = state.gold,
                hireCosts = costs
            )
        }
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        val cost = _uiState.value.hireCosts[hero.id] ?: GameConstants.HIRE_HERO_COST
        if (state.gold >= cost) {
            state.gold -= cost
            state.party.add(hero)
            state.hireableHeroes.removeIf { it.id == hero.id }
            gameRepository.persistCurrentState()
            refresh()
        }
    }

    fun rerollRecruits() {
        val state = gameRepository.currentState()
        if (state.gold >= GameConstants.REROLL_RECRUITS_COST) {
            state.gold -= GameConstants.REROLL_RECRUITS_COST
            state.hireableHeroes.clear()
            state.hireableHeroes.addAll(heroPool.generatePool(GameConstants.MAX_RECRUITS_POOL_SIZE))
            gameRepository.persistCurrentState()
            refresh()
        }
    }
}
