package com.grimreich.ui.alchemy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.AlchemySystem
import com.grimreich.systems.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class AlchemyUiState(
    val recipes: List<Recipe> = emptyList(),
    val inventory: List<Item> = emptyList(),
    val party: List<Hero> = emptyList(),
    val selectedRecipe: Recipe? = null,
    val selectedHero: Hero? = null,
    val statusMessage: String? = null
)

@HiltViewModel
class AlchemyViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val alchemySystem: AlchemySystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlchemyUiState(recipes = alchemySystem.recipes))
    val uiState: StateFlow<AlchemyUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                _uiState.update { 
                    it.copy(
                        inventory = state.inventory.toList(),
                        party = state.party.toList(),
                        selectedHero = it.selectedHero ?: state.party.firstOrNull()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectRecipe(recipe: Recipe) {
        _uiState.update { it.copy(selectedRecipe = recipe, statusMessage = null) }
    }

    fun selectHero(hero: Hero) {
        _uiState.update { it.copy(selectedHero = hero) }
    }

    fun craft() {
        val recipe = _uiState.value.selectedRecipe ?: return
        val hero = _uiState.value.selectedHero ?: return
        
        val result = alchemySystem.craft(recipe, hero.id)
        _uiState.update { it.copy(statusMessage = result) }
    }
}
