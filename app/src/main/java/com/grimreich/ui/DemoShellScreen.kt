package com.grimreich.ui

import com.grimreich.core.DemoShellState

object DemoShellScreen {
    fun render(state: DemoShellState): String = buildString {
        appendLine("=== ${state.mainMenu.title.uppercase()} ===")
        appendLine(state.mainMenu.subtitle)
        appendLine("Menu:")
        state.mainMenu.items.forEach { item ->
            appendLine("- ${item.title}: ${item.description}")
        }
        appendLine("Slices:")
        state.selector.items.forEach { item ->
            val marker = if (item.cityId == state.selector.selectedCityId) "*" else "-"
            appendLine("$marker ${item.cityTitle}: ${item.summary}")
        }
        appendLine("Current city: ${state.currentCityId ?: "none"}")
        appendLine("Session notes: ${state.sessionNotes.size}")
    }
}
