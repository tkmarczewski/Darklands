package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.SaintCatalogue
import com.darklandsmobile.core.ShrineType

/**
 * Religia: modlitwa, grzech, cnota i laska swietych.
 */
object ReligionSystem {

    fun pray(saintId: String, shrineType: ShrineType): String {
        val saint = SaintCatalogue.get(saintId) ?: return "Nieznany swiety: $saintId"
        val p = GameRepository.state.prayer

        val gain = when (shrineType) {
            ShrineType.CATHEDRAL -> 10
            ShrineType.CHAPEL -> 5
            ShrineType.MONASTERY -> 4
            ShrineType.ROADSIDE -> 2
            ShrineType.RUINS -> 1
        }

        p.favor[saintId] = (p.favor.getOrDefault(saintId, 0) + gain).coerceIn(0, 100)
        p.faith = minOf(100, p.faith + gain)
        p.blessings++

        GameRepository.log("Modlitwa do ${saint.name} przy ${shrineType.name}. Laska: ${p.favor[saintId]}")
        return "Modlitwa do ${saint.name}. +$gain laski. Wiara: ${p.faith}"
    }

    fun sin(amount: Int = 1): String {
        val p = GameRepository.state.prayer
        p.sins += amount
        p.virtue = maxOf(0, p.virtue - amount * 2)
        return "Grzech popelniony. Grzechy: ${p.sins}, Cnota: ${p.virtue}"
    }

    fun getSaint(saintId: String) = SaintCatalogue.get(saintId)

    fun allSaints() = SaintCatalogue.all()

    fun favorFor(saintId: String): Int =
        GameRepository.state.prayer.favor.getOrDefault(saintId, 0)
}
