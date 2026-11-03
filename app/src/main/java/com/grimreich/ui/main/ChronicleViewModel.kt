package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.ChronicleEntry
import com.grimreich.systems.ChronicleSystem
import com.grimreich.systems.WikiCodex
import com.grimreich.systems.WikiDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class ChronicleTab {
    CHRONICLE_LORE,
    PRZEWODNIK_WIKI
}

@HiltViewModel
class ChronicleViewModel @Inject constructor(
    private val chronicleSystem: ChronicleSystem,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _unlockedEntries = MutableStateFlow<List<ChronicleEntry>>(emptyList())
    val unlockedEntries: StateFlow<List<ChronicleEntry>> = _unlockedEntries.asStateFlow()

    private val _wikiEntries = MutableStateFlow<List<WikiDisplayModel>>(emptyList())
    val wikiEntries: StateFlow<List<WikiDisplayModel>> = _wikiEntries.asStateFlow()

    private val _selectedTab = MutableStateFlow(ChronicleTab.CHRONICLE_LORE)
    val selectedTab: StateFlow<ChronicleTab> = _selectedTab.asStateFlow()

    init {
        gameRepository.gameState
            .onEach { state ->
                _unlockedEntries.value = chronicleSystem.getUnlockedEntries()
                _wikiEntries.value = WikiCodex.getEntriesForState(state)
            }
            .launchIn(viewModelScope)
    }

    fun selectTab(tab: ChronicleTab) {
        _selectedTab.value = tab
    }
}
