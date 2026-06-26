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
    val hireCosts: Map<String, Int> = emptyMap(),
    val isPartyFull: Boolean = false
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
     * Odswieża pule - generuje 4 nowych losowych bohaterów.
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
                hireCosts = costs,
                isPartyFull = state.party.size >= GameConstants.MAX_PARTY_SIZE
            )
        }
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        val cost = _uiState.value.hireCosts[hero.id] ?: GameConstants.HIRE_HERO_COST
        // Bug fix: guard against unbounded party growth
        if (state.gold >= cost && state.party.size < GameConstants.MAX_PARTY_SIZE) {
            state.gold -= cost
            state.party.add(hero)
            state.hireableHeroes.removeIf { it.id == hero.id }
            gameRepository.persistCurrentState()
            refresh()
        }
    }
}
