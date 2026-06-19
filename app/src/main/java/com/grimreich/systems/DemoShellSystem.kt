package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.world.CityCatalogue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoShellSystem @Inject constructor(
    private val cityCatalogue: CityCatalogue
) {
    fun build(): DemoShellState {
        cityCatalogue.seedCanonical()
        val allCities = cityCatalogue.all()

        val mainMenu = DemoMainMenuState(
            title = "GrimReich Demo",
            subtitle = "Skanowanie rzeczywistości...",
            items = listOf(
                DemoMenuItem("start", "START", "Rozpocznij przygodę."),
                DemoMenuItem("exit", "WYJŚĆ", "Opuść demo.")
            )
        )

        val selector = SliceSelectorState(
            selectedCityId = allCities.firstOrNull()?.id,
            items = allCities.map { 
                SliceSelectorItem(it.id, it.name, it.loreDescription) 
            }
        )

        return DemoShellState(
            mainMenu = mainMenu,
            selector = selector,
            currentCityId = allCities.firstOrNull()?.id,
            sessionNotes = emptyList()
        )
    }

    fun selectCity(state: DemoShellState, cityId: String): DemoShellState {
        return state.copy(currentCityId = cityId)
    }

    fun addNote(state: DemoShellState, cityId: String, note: String): DemoShellState {
        val notes = state.sessionNotes.toMutableList()
        notes.add(PlaytestSessionNote(cityId, note))
        return state.copy(sessionNotes = notes)
    }
}
