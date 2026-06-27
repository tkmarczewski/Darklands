package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameConstants
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.Career
import com.grimreich.world.HeroPool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        gameRepository.gameState
            .onEach { state ->
                val costs = state.hireableHeroes.associate { it.id to heroPool.hireCostFor(it.currentCareer ?: Career.MERCENARY) }
                _uiState.update { 
                    it.copy(
                        availableHeroes = state.hireableHeroes,
                        gold = state.gold,
                        hireCosts = costs,
                        isPartyFull = state.party.size >= GameConstants.MAX_PARTY_SIZE
                    )
                }
                
                if (state.hireableHeroes.isEmpty()) {
                    gameRepository.updateState { s ->
                        s.hireableHeroes.addAll(heroPool.generatePool(GameConstants.MAX_RECRUITS_POOL_SIZE))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun hireHero(hero: Hero) {
        val state = gameRepository.currentState()
        val cost = _uiState.value.hireCosts[hero.id] ?: 100
        
        if (state.gold < cost || state.party.size >= GameConstants.MAX_PARTY_SIZE) return

        gameRepository.updateState { s ->
            s.gold -= cost
            s.party.add(hero)
            s.hireableHeroes.removeIf { it.id == hero.id }
            s.logEntries.add("Zrekrutowano: ${hero.name} za $cost zł.")
        }
    }
}
