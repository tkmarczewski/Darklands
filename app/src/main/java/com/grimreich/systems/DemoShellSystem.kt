package com.grimreich.systems

import com.grimreich.core.DemoMainMenuState
import com.grimreich.core.DemoMenuItem
import com.grimreich.core.DemoShellState
import com.grimreich.core.PlaytestSessionNote
import com.grimreich.core.SliceSelectorItem
import com.grimreich.core.SliceSelectorState

object DemoShellSystem {
    private val citySummaries = mapOf(
        "grimhold" to "Dockside intrigue and merchant paranoia.",
        "praha" to "Cathedral stone, scholars and secrets.",
        "koln" to "Pilgrims, relics and river commerce.",
        "brno" to "Frontier markets and guarded gates.",
        "wroclaw" to "Bridges, dark water and whispered bargains.",
        "vienna" to "Court ambition behind heavy walls."
    )

    fun build(): DemoShellState {
        val items = listOf(
            DemoMenuItem("start", "Start Internal Demo", "Launch a curated city slice for testing."),
            DemoMenuItem("notes", "Playtest Notes", "Review internal notes captured during sessions."),
            DemoMenuItem("saves", "Save/Load", "Check persistence behavior inside the prototype.")
        )

        val selectorItems = citySummaries.map { (cityId, summary) ->
            SliceSelectorItem(cityId, cityId.replaceFirstChar { it.uppercase() }, summary)
        }

        return DemoShellState(
            mainMenu = DemoMainMenuState(
                title = "Grimreich Internal Demo",
                subtitle = "Playable city slices for closed testing.",
                items = items
            ),
            selector = SliceSelectorState(
                selectedCityId = null,
                items = selectorItems
            ),
            currentCityId = null,
            sessionNotes = emptyList()
        )
    }

    fun selectCity(state: DemoShellState, cityId: String): DemoShellState {
        return state.copy(
            selector = state.selector.copy(selectedCityId = cityId),
            currentCityId = cityId
        )
    }

    fun addNote(state: DemoShellState, cityId: String, note: String): DemoShellState {
        return state.copy(sessionNotes = state.sessionNotes + PlaytestSessionNote(cityId, note))
    }
}
