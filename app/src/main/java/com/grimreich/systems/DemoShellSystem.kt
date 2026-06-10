package com.grimreich.systems

import com.grimreich.core.DemoMainMenuState
import com.grimreich.core.DemoMenuItem
import com.grimreich.core.DemoShellState
import com.grimreich.core.PlaytestSessionNote
import com.grimreich.core.SliceSelectorItem
import com.grimreich.core.SliceSelectorState
import com.grimreich.world.CityCatalogue

object DemoShellSystem {
    private val citySummaries = mapOf(
        "wybrzeze_polnocne" to "Cold cliffs, wrecks and eternal mist.",
        "serce_krainy" to "Cathedral city, mirrors and archives of truth.",
        "rowniny_koronne" to "Fertile fields, red canals and guild law.",
        "pogranicze_stepowe" to "Steppe, rifts and shadow raids.",
        "poludniowe_ruiny" to "Ruined temples, ash and echoes of hymns.",
        "gory_poludniowe" to "Ice passes, absolute silence and summits.",
        "ziemie_dzikie" to "Forests, hunger and ancient runes."
    )

    fun build(): DemoShellState {
        CityCatalogue.seedCanonical()
        val cities = CityCatalogue.all()
        
        val sliceItems = cities.map { city ->
            SliceSelectorItem(
                cityId = city.id,
                cityTitle = city.name,
                summary = citySummaries[city.id] ?: "Unknown region"
            )
        }

        return DemoShellState(
            mainMenu = DemoMainMenuState(
                title = "GrimReich Internal Demo",
                subtitle = "Lore Alignment Build 1.5",
                items = listOf(
                    DemoMenuItem("slice_selector", "Enter Vertical Slice", "Narrative focused preview."),
                    DemoMenuItem("sandbox", "Free Sandbox Mode", "Test all systems freely.")
                )
            ),
            selector = SliceSelectorState(null, sliceItems),
            currentCityId = "wybrzeze_polnocne",
            sessionNotes = emptyList()
        )
    }

    fun selectCity(state: DemoShellState, cityId: String): DemoShellState {
        return state.copy(currentCityId = cityId)
    }

    fun addNote(state: DemoShellState, author: String, text: String): DemoShellState {
        val note = PlaytestSessionNote(author, text)
        return state.copy(sessionNotes = state.sessionNotes + note)
    }
}
