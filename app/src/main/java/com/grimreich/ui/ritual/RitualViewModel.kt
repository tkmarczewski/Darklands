package com.grimreich.ui.ritual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.RitualSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RitualViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    val ritualSystem: RitualSystem
) : ViewModel() {

    private val _deadHero = MutableStateFlow<Hero?>(null)
    val deadHero: StateFlow<Hero?> = _deadHero.asStateFlow()

    val globalStability: StateFlow<Int> = gameRepository.gameState
        .map { it.world.globalStability }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 100)

    init {
        gameRepository.gameState
            .onEach { state ->
                val hero = state.party.find { it.isDead }
                _deadHero.value = hero
            }
            .launchIn(viewModelScope)
    }
}
