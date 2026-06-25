package com.grimreich.ui.ritual

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.RitualSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RitualViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    val ritualSystem: RitualSystem
) : ViewModel() {

    private val _deadHero = MutableStateFlow<Hero?>(null)
    val deadHero: StateFlow<Hero?> = _deadHero.asStateFlow()

    init {
        val state = gameRepository.currentState()
        val hero = state.party.find { it.id == state.activeHeroId }
        if (hero?.isDead == true) {
            _deadHero.value = hero
        }
    }
}
