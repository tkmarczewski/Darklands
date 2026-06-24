package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameBootstrapper
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.CombatSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class GameScreenMode {
    MAIN_MENU, PLAYER_IDENTITY, CHARACTER_CREATOR, WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, WORLD_LOG, RECRUIT, CHAR_DETAIL, MARKET, DEV_MENU
}

@HiltViewModel
class GameRootViewModel @Inject constructor(
    val gameRepository: GameRepository,
    val gameBootstrapper: GameBootstrapper,
    val combatSystem: CombatSystem
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.MAIN_MENU)
    val mode: StateFlow<GameScreenMode> = _mode.asStateFlow()

    private val _inspectedHero = MutableStateFlow<Hero?>(null)
    val inspectedHero: StateFlow<Hero?> = _inspectedHero.asStateFlow()

    fun setMode(newMode: GameScreenMode) {
        _mode.value = newMode
    }

    fun restoreSessionIfValid(): Boolean {
        if (gameRepository.restoreIfAvailable()) {
            setMode(GameScreenMode.HUB)
            return true
        }
        return false
    }

    fun inspectHero(heroId: String) {
        val hero = gameRepository.currentState().party.find { it.id == heroId }
        _inspectedHero.value = hero
        setMode(GameScreenMode.CHAR_DETAIL)
    }

    fun upgradeStat(heroId: String, stat: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId }
            if (hero != null && hero.attributePoints > 0) {
                when (stat) {
                    "STR" -> hero.strength++
                    "AGI" -> hero.agility++
                    "PER" -> hero.perception++
                    "INT" -> hero.intelligence++
                    "END" -> {
                        hero.endurance++
                        hero.maxHp += 2 // Immediate HP bonus
                        hero.hp += 2
                    }
                    "CHA" -> hero.charisma++
                    "PIE" -> hero.piety++
                }
                hero.attributePoints--
                // Update inspected hero state flow if it's the same one
                if (_inspectedHero.value?.id == heroId) {
                    _inspectedHero.value = hero.copy()
                }
            }
        }
    }

    fun saveGame() {
        gameRepository.persistCurrentState()
    }
}
