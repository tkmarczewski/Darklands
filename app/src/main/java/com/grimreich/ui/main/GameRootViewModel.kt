package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameBootstrapper
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class GameScreenMode {
    MAIN_MENU, PLAYER_IDENTITY, CHARACTER_CREATOR, WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, WORLD_LOG, RECRUIT, CHAR_DETAIL, MARKET, DEV_MENU
}

@HiltViewModel
class GameRootViewModel @Inject constructor(
    val gameRepository: GameRepository,
    val gameBootstrapper: GameBootstrapper
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.MAIN_MENU)
    val mode: StateFlow<GameScreenMode> = _mode.asStateFlow()

    private val _inspectedHero = MutableStateFlow<Hero?>(null)
    val inspectedHero: StateFlow<Hero?> = _inspectedHero.asStateFlow()

    fun setMode(newMode: GameScreenMode) {
        _mode.value = newMode
    }

    fun restoreSessionIfValid(): Boolean {
        return if (gameRepository.restoreIfAvailable()) {
            setMode(GameScreenMode.HUB)
            true
        } else {
            false
        }
    }

    fun inspectHero(heroId: String) {
        val hero = gameRepository.currentState().party.find { it.id == heroId }
        _inspectedHero.value = hero
        setMode(GameScreenMode.CHAR_DETAIL)
    }

    fun saveGame() {
        gameRepository.persistCurrentState()
    }
}
