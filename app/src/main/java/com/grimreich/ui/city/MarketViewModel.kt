package com.grimreich.ui.city

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MarketItem(
    val id: String,
    val name: String,
    val price: Int,
    val sellPrice: Int = (price * 0.6f).toInt()
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
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        refresh()
    }

    private fun toSlug(raw: String) = raw.lowercase()
        .replace("ą","a").replace("ć","c").replace("ę","e")
        .replace("ł","l").replace("ń","n").replace("ó","o")
        .replace("ś","s").replace("ź","z").replace("ż","z")
        .replace(" ","_")

    fun refresh() {
        val state = gameRepository.currentState()
        val cityId = toSlug(state.grimCurrentRegion)
        val cityData = cityCatalogue.get(cityId)
        val modifier = cityData?.priceModifier ?: 1.0f

        val forSale = itemCatalogue.all().map { item ->
            MarketItem(
                id = item.id,
                name = item.name,
                price = (item.value * modifier).toInt().coerceAtLeast(1)
            )
        }

        val toSell = state.inventory.map { item ->
            MarketItem(
                id = item.id,
                name = item.name,
                price = item.value,
                sellPrice = (item.value * 0.6f * modifier).toInt().coerceAtLeast(1)
            )
        }

        _uiState.update {
            it.copy(
                cityName = cityData?.name ?: "Nieznane Miasto",
                playerGold = state.gold,
                itemsForSale = forSale,
                itemsToSell = toSell,
                errorMessage = null
            )
        }
    }

    fun buy(itemId: String) {
        val state = gameRepository.currentState()
        val item = _uiState.value.itemsForSale.find { it.id == itemId } ?: return
        if (state.gold < item.price) {
            _uiState.update { it.copy(errorMessage = "Za mało złota.") }
            return
        }
        state.gold -= item.price
        itemCatalogue.get(itemId)?.let { state.inventory.add(it) }
        gameRepository.persistCurrentState()
        refresh()
    }

    fun sell(itemId: String) {
        val state = gameRepository.currentState()
        val item = _uiState.value.itemsToSell.find { it.id == itemId } ?: return
        state.gold += item.sellPrice
        state.inventory.removeAll { it.id == itemId }
        gameRepository.persistCurrentState()
        refresh()
    }
}
