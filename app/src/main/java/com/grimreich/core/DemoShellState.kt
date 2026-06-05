package com.grimreich.core

data class DemoMenuItem(
    val id: String,
    val title: String,
    val description: String
)

data class DemoMainMenuState(
    val title: String,
    val subtitle: String,
    val items: List<DemoMenuItem>
)

data class SliceSelectorItem(
    val cityId: String,
    val cityTitle: String,
    val summary: String
)

data class SliceSelectorState(
    val selectedCityId: String?,
    val items: List<SliceSelectorItem>
)

data class PlaytestSessionNote(
    val cityId: String,
    val note: String
)

data class DemoShellState(
    val mainMenu: DemoMainMenuState,
    val selector: SliceSelectorState,
    val currentCityId: String?,
    val sessionNotes: List<PlaytestSessionNote>
)
