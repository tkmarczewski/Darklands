package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class GameScreenMode {
    WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, WORLD_LOG, RECRUIT, CHAR_DETAIL
}

@HiltViewModel
class GameRootViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.HUB)
    val mode: StateFlow<GameScreenMode> = _mode

    private val _inspectedHero = MutableStateFlow<Hero?>(null)
    val inspectedHero: StateFlow<Hero?> = _inspectedHero

    fun setMode(newMode: GameScreenMode) {
        _mode.value = newMode
    }

    fun inspectHero(heroId: String) {
        _inspectedHero.value = gameRepository.currentState().party.find { it.id == heroId }
        setMode(GameScreenMode.CHAR_DETAIL)
    }

    fun saveGame() {
        gameRepository.persistCurrentState()
    }
}
