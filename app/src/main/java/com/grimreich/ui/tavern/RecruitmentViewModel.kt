package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
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
    val hireCosts: Map<String, Int> = emptyMap()  // heroId -> koszt
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
        val heroes = heroPool.generatePool(4)
        val costs = heroes.associate { hero ->
            hero.id to (hero.currentCareer?.let { heroPool.hireCostFor(it) } ?: 50)
        }
        _uiState.update {
            it.copy(
                availableHeroes = heroes,
                gold = state.gold,
                hireCosts = costs
            )
        }
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        val cost = _uiState.value.hireCosts[hero.id] ?: 50
        if (state.gold >= cost) {
            state.gold -= cost
            state.party.add(hero)
            gameRepository.persistCurrentState()
            // Usuń zatrudnionego z listy i odśwież złoto
            _uiState.update { current ->
                current.copy(
                    availableHeroes = current.availableHeroes.filter { it.id != hero.id },
                    gold = state.gold
                )
            }
        }
    }
}
