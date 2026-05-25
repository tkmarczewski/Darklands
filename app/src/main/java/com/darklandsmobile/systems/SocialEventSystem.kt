package com.darklandsmobile.systems

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.SaintCatalogue
import com.darklandsmobile.world.CityCatalogue

/**
 * Eventy społeczne wywoływane po wejściu do miasta albo po modlitwie.
 */
object SocialEventSystem {
    fun cityAudience(cityId: String, saintId: String? = null): String {
        val city = CityCatalogue.get(cityId) ?: return "Nieznane miasto: $cityId"
        val rep = ReputationSystem.getCityRep(cityId)  // używamy cityId, nie nazwy miasta
        val saint = saintId?.let { SaintCatalogue.get(it) }
        val base = when {
            rep >= 50 -> "Mieszczanie witają was przychylnie."
            rep >= 0 -> "Ludzie obserwują was z ciekawością."
            rep >= -50 -> "Straż miejska ma was na oku."
            else -> "W mieście narasta nieufność wobec was."
        }
        val saintLine = saint?.let { " Patron: ${it.name} (${it.domain})." } ?: ""
        val msg = base + saintLine
        GameRepository.log("SocialEvent: $msg @ ${city.name}")
        return msg
    }

    fun prayerReaction(saintId: String): String {
        val saint = SaintCatalogue.get(saintId) ?: return "Nieznany święty: $saintId"
        val favor = ReligionSystem.favorFor(saintId)
        return when {
            favor >= 50 -> "Ludzie mówią o łasce ${saint.name}."
            favor >= 20 -> "Modlitwa do ${saint.name} została dobrze przyjęta."
            else -> "Modlitwa odbyła się bez większego echa."
        }
    }
}