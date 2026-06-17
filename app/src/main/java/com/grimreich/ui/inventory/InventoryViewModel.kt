package com.grimreich.ui.inventory

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class InventoryUiState(
    val activeHero: Hero? = null,
    val inventory: List<Item> = emptyList(),
    val selectedItem: Item? = null
)

class InventoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectHero(heroId: String) {
        val hero = GameRepository.state.party.find { it.id == heroId }
        _uiState.update { it.copy(activeHero = hero) }
    }

    fun selectItem(item: Item?) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun refresh() {
        val state = GameRepository.state
        val activeHero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull()
        _uiState.update { 
            it.copy(
                activeHero = activeHero,
                inventory = state.inventory.toList()
            )
        }
    }
}
