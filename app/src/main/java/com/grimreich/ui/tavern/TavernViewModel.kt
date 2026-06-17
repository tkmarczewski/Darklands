package com.grimreich.ui.tavern

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.systems.SocialEventSystem
import com.grimreich.systems.SaveLoadSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TavernUiState(
    val gold: Int = 0,
    val log: String = "Karczmarz poleruje blat brudną szmatą..."
)

class TavernViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TavernUiState())
    val uiState: StateFlow<TavernUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun rest() {
        val state = GameRepository.state
        if (state.gold < 50) {
            updateLog("Nie stać cię na nocleg. Karczmarz wskazuje na stajnię...")
            return
        }

        state.gold -= 50
        state.party.forEach { hero ->
            val healAmount = hero.maxHp / 2
            hero.hp = (hero.hp + healAmount).coerceAtMost(hero.maxHp)
            hero.endurance = 20 
            hero.sanity = (hero.sanity + 10).coerceAtMost(100)
        }
        
        state.world.day += 1
        state.world.timeOfDay = "Morning"
        
        updateLog("Przespałeś noc w miarę czystym łóżku. Twoje rany się podgoiły, a umysł odpoczął. Jest nowy dzień.")
        refresh()
    }

    fun listenToGossip() {
        val gossip = SocialEventSystem.runTavernEvent()
        updateLog(gossip)
    }

    private fun updateLog(text: String) {
        _uiState.update { it.copy(log = text) }
    }

    fun refresh() {
        _uiState.update { it.copy(gold = GameRepository.state.gold) }
    }
}
