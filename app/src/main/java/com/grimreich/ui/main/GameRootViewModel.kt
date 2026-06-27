package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameBootstrapper
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.AudioEngine
import com.grimreich.systems.CombatSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GameScreenMode {
    MAIN_MENU, PLAYER_IDENTITY, CHARACTER_CREATOR, WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, CHRONICLE, RECRUIT, CHAR_DETAIL, MARKET, DEV_MENU, RITUAL, ENDING, EXPEDITION
}

@HiltViewModel
class GameRootViewModel @Inject constructor(
    val gameRepository: GameRepository,
    private val gameBootstrapper: GameBootstrapper,
    private val combatSystem: CombatSystem,
    private val audioEngine: AudioEngine
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.MAIN_MENU)
    val mode: StateFlow<GameScreenMode> = _mode.asStateFlow()

    private val _inspectedHeroId = MutableStateFlow<String?>(null)
    val inspectedHero: StateFlow<Hero?> = combine(gameRepository.gameState, _inspectedHeroId) { state, id ->
        id?.let { state.party.find { h -> h.id == it } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        // Audio handled externally
    }

    fun setMode(mode: GameScreenMode) {
        _mode.value = mode
    }

    fun startNewGame() {
        viewModelScope.launch {
            gameBootstrapper.bootstrapFreshWorld()
            setMode(GameScreenMode.HUB)
        }
    }

    fun restoreSessionIfValid(): Boolean {
        if (gameRepository.restoreIfAvailable()) {
            setMode(GameScreenMode.HUB)
            return true
        }
        return false
    }

    fun inspectHero(heroId: String) {
        _inspectedHeroId.value = heroId
        setMode(GameScreenMode.CHAR_DETAIL)
    }

    fun upgradeStat(heroId: String, stat: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (hero.attributePoints > 0) {
                hero.attributePoints--
                when (stat.lowercase()) {
                    "strength", "siła" -> hero.strength++
                    "agility", "zręczność" -> hero.agility++
                    "intelligence", "inteligencja" -> hero.intelligence++
                    "endurance", "wytrzymałość" -> hero.endurance++
                    "perception", "percepcja" -> hero.perception++
                    "charisma", "charyzma" -> hero.charisma++
                    "piety", "pobożność" -> hero.piety++
                }
                state.logEntries.add("${hero.name} rozwija swoją naturę: $stat +1.")
            }
        }
    }

    fun saveGame() {
        gameRepository.persistCurrentState()
    }
}
