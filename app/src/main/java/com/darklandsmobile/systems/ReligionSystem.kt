package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.SaintCatalogue
import com.darklandsmobile.core.ShrineType

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

        p.faith = minOf(100, p.faith + gain)
        p.virtue = minOf(100, p.virtue + (gain / 2).coerceAtLeast(1))
        p.blessings += 1

        GameRepository.log("Modlitwa do ${saint.name} przy ${shrineType.name}. Wiara: ${p.faith}")
        return "Modlitwa do ${saint.name}. +$gain wiary. Wiara: ${p.faith}"
    }

    fun sin(amount: Int = 1): String {
        val p = GameRepository.state.prayer
        p.sins += amount
        p.virtue = maxOf(0, p.virtue - amount * 2)
        return "Grzech popelniony. Grzechy: ${p.sins}, Cnota: ${p.virtue}"
    }

    fun getSaint(saintId: String) = SaintCatalogue.get(saintId)

    fun allSaints() = SaintCatalogue.all()

    fun favorFor(saintId: String): Int {
        val p = GameRepository.state.prayer
        return (p.faith + p.blessings * 5 - p.sins * 2).coerceIn(0, 100)
    }
}