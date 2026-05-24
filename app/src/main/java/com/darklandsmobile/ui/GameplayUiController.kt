package com.darklandsmobile.ui

import com.darklandsmobile.systems.GameViewModel

/**
 * Lightweight adapter exposing fully rendered screen text for early UI wiring,
 * previews, or temporary non-Compose integration.
 */
class GameplayUiController(
    private val viewModel: GameViewModel = GameViewModel()
) {
    fun city(): String = GameplayScreens.renderCity(viewModel.cityScreenState)

    fun journal(): String = GameplayScreens.renderJournal(viewModel.journalState)

    fun acceptQuest(questId: String): String {
        viewModel.acceptQuest(questId)
        return city()
    }

    fun travel(): String {
        viewModel.travelToActiveQuest()
        return GameplayScreens.renderTravel(requireNotNull(viewModel.travelScreenState))
    }

    fun resolve(): String {
        viewModel.resolveActiveQuest()
        return GameplayScreens.renderResolution(requireNotNull(viewModel.resolutionScreenState))
    }

    fun save(): String {
        val save = viewModel.saveGame()
        return "Saved game v${save.version} for city ${save.playerState.currentCityId}"
    }

    fun load(): String {
        val loaded = viewModel.loadGame() ?: return "No save found"
        return "Loaded game v${loaded.version} for city ${loaded.playerState.currentCityId}"
    }
}
