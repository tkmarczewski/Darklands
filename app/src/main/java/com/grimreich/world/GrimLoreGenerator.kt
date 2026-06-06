package com.grimreich.world

import kotlin.random.Random

object GrimLoreGenerator {

    fun generateDescription(location: ProceduralLocation): String {
        val random = Random(location.id.hashCode())
        
        val baseDesc = when (location.type) {
            LocationType.RUINS            -> "Zgliszcza spowite nienaturalną mgłą."
            LocationType.MONASTERY        -> "Opactwo, w którym dzwony biją same z siebie."
            LocationType.RAUBRITTER_CASTLE -> "Twierdza zbudowana na kościach zapomnianych królów."
            LocationType.DUNGEON          -> "Lochy, w których ściany wydają się oddychać."
            LocationType.SHRINE           -> "Kapliczka, która krwawi, gdy nikt nie patrzy."
        }

        val atmosphere = listOf(
            "Czuć tu zapach ozonu i starej krwi.",
            "W powietrzu unosi się pył z rozpadającej się rzeczywistości.",
            "Cisza jest tak gęsta, że można ją kroić nożem."
        ).random(random)

        return "$baseDesc $atmosphere"
    }
}
