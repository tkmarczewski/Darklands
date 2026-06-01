package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.SaveSnapshot
import com.darklandsmobile.core.deepCopy

object SaveSystem {
    private val saves = mutableListOf<SaveSnapshot>()
    private var versionCounter = 0

    fun snapshot(label: String): SaveSnapshot {
        versionCounter++
        val snap = SaveSnapshot(
            version   = versionCounter,
            timestamp = System.currentTimeMillis(),
            label     = label,
            state     = GameRepository.state.deepCopy()
        )
        saves.add(snap)
        if (saves.size > 10) saves.removeAt(0)
        GameRepository.log("Zapisano: v$versionCounter - $label")
        return snap
    }

    fun load(version: Int): String {
        val snap = saves.firstOrNull { it.version == version }
            ?: return "Brak zapisu v$version"
        GameRepository.state = snap.state.deepCopy()
        GameRepository.log("Wczytano: v$version - ${snap.label}")
        return "Wczytano: v$version - ${snap.label}"
    }

    fun listSaves() = saves.map { "v${it.version}: ${it.label}" }
}
