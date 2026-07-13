package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import java.text.Normalizer

data class MarketItem(
    val id: String,
    val name: String,
    val price: Int,
    val sellPrice: Int
)

data class MarketUiState(
    val cityName: String = "",
    val playerGold: Int = 0,
    val itemsForSale: List<MarketItem> = emptyList(),
    val itemsToSell: List<MarketItem> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { refresh() }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        val state = gameRepository.currentState()
        val cityId = state.world.locationId
        val city = cityCatalogue.get(cityId)
        
        val stock = city?.marketStock ?: emptyList()
        val forSale = stock.mapNotNull { itemId ->
            itemCatalogue.get(itemId)?.let { item ->
                MarketItem(item.templateId, item.name, item.value, (item.value * 0.5).toInt())
            }
        }

        val toSell = state.inventory.map { item ->
            MarketItem(item.instanceId, item.name, item.value, (item.value * 0.5).toInt())
        }

        _uiState.update { 
            it.copy(
                cityName = city?.name ?: "Nieznane Miasto",
                playerGold = state.gold,
                itemsForSale = forSale,
                itemsToSell = toSell
            )
        }
    }

    fun buy(itemId: String) {
        val state = gameRepository.currentState()
        val item = itemCatalogue.get(itemId) ?: return
        if (state.gold < item.value) {
            _uiState.update { it.copy(errorMessage = "Brak złota!") }
            return
        }

        gameRepository.updateState { s ->
            s.gold -= item.value
            itemCatalogue.createInstance(itemId)?.let { s.inventory.add(it) }
            s.logEntries.add("Kupiono: ${item.name} za ${item.value} G.")
        }
    }

    fun sell(itemId: String) {
        val state = gameRepository.currentState()
        val item = state.inventory.find { it.instanceId == itemId } ?: return
        val price = (item.value * 0.5).toInt()

        gameRepository.updateState { s ->
            val toRemove = s.inventory.find { it.instanceId == itemId }
            if (toRemove != null) {
                s.inventory.remove(toRemove)
                s.gold += price
                s.logEntries.add("Sprzedano: ${item.name} za $price G.")
            }
        }
    }
}
