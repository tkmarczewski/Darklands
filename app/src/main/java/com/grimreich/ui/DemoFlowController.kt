package com.grimreich.ui

class DemoFlowController {
    fun mainMenu(): String = "Grimreich MVP Demo"
    fun select(cityId: String): String = "Selected city: $cityId"
    fun openCurrentSlice(): String = "Legacy demo flow is disabled. Use MainActivity for the MVP slice."
    fun addPlaytestNote(note: String): String = "Playtest note saved: $note"
}