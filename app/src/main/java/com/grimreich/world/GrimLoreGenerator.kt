package com.grimreich.world

import kotlin.random.Random

object GrimLoreGenerator {

    fun generateDescription(location: ProceduralLocation): String {
        val random = Random(location.id.hashCode())
        
        val baseDesc = when (location.type) {
            LocationType.ZGLISZCZA      -> "Zgliszcza spowite nienaturalną mgłą."
            LocationType.MROCZNY_ZAKON  -> "Miejsce, w którym dzwony biją same z siebie."
            LocationType.TWIERDZA_CIENIA -> "Twierdza zbudowana na kościach zapomnianych królów."
            LocationType.KATAKUMBY_MROKU -> "Lochy, w których ściany wydają się oddychać."
            LocationType.KAPLICZKA_KRWI  -> "Kapliczka, która krwawi, gdy nikt nie patrzy."
        }

        val atmosphere = listOf(
            "Czuć tu zapach ozonu i starej krwi.",
            "W powietrzu unosi się pył z rozpadającej się rzeczywistości.",
            "Cisza jest tak gęsta, że można ją kroić nożem."
        ).random(random)

        return "$baseDesc $atmosphere"
    }
}
