package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.GameRootStateSaver
import com.grimreich.core.GameState
import com.grimreich.ui.city.CityViewModel
import com.grimreich.ui.combat.CombatViewModel
import com.grimreich.ui.dialogue.DialogueViewModel
import com.grimreich.ui.inventory.InventoryViewModel
import com.grimreich.ui.map.WorldMapViewModel
import com.grimreich.ui.tavern.TavernViewModel
import com.grimreich.ui.saints.SaintsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class GameScreenMode {
    WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, WORLD_LOG, RECRUIT
}

class GameRootViewModel(
    private val saver: GameRootStateSaver? = null
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.HUB)
    val mode: StateFlow<GameScreenMode> = _mode

    // Sub-ViewModels
    val hubVM = HubViewModel()
    val cityVM = CityViewModel()
    val combatVM = CombatViewModel()
    val inventoryVM = InventoryViewModel()
    val worldMapVM = WorldMapViewModel()
    val tavernVM = TavernViewModel()
    val saintsVM = SaintsViewModel()
    val dialogueVM = DialogueViewModel()

    fun setMode(newMode: GameScreenMode) {
        _mode.value = newMode
        // REFRESH on mode change to ensure UI 2.0 consistency
        when (newMode) {
            GameScreenMode.HUB -> hubVM.refresh()
            GameScreenMode.CITY -> cityVM.refresh()
            GameScreenMode.TAVERN -> tavernVM.refresh()
            GameScreenMode.TEMPLE -> saintsVM.refresh()
            GameScreenMode.WORLD_MAP -> worldMapVM.refresh()
            GameScreenMode.QUESTS -> hubVM.refresh() // Quests are driven by hub state counts
            else -> {}
        }
    }

    fun saveGame() {
        saver?.save(GameRepository.state)
    }

    fun loadGame() {
        saver?.load()?.let { loaded ->
            GameRepository.state = loaded
            refreshAll()
        }
    }

    private fun refreshAll() {
        hubVM.refresh()
        cityVM.refresh()
        combatVM.refresh()
        inventoryVM.refresh()
        worldMapVM.refresh()
        tavernVM.refresh()
        saintsVM.refresh()
    }
}
