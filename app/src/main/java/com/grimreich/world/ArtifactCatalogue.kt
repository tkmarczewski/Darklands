package com.grimreich.world

import com.grimreich.grimreich.v1.Item

/**
 * Central repository for canonical ontological artifacts.
 */
object ArtifactCatalogue {

    fun getArtifacts(): List<Item> {
        return listOf(
            Item(
                instanceId = "",
                templateId = "art_forget",
                name = "Kielich Zapomnienia",
                type = "artifact",
                rarity = "LEGENDARY",
                value = 5000,
                weight = 1.0,
                properties = mapOf("description" to "Woda w nim nigdy nie wysycha. Picie z niego usuwa ból, ale i cząstkę duszy."),
                effects = mapOf("sanity" to -20, "hp" to 50)
            ),
            Item(
                instanceId = "",
                templateId = "art_despair",
                name = "Sztandar Rozpaczy",
                type = "artifact",
                rarity = "LEGENDARY",
                value = 6000,
                weight = 5.0,
                properties = mapOf("description" to "Szarpią go wichry, których nikt inny nie czuje. Budzi grozę w sercach wrogów."),
                effects = mapOf("morale" to 30, "strength" to 5)
            ),
            Item(
                instanceId = "",
                templateId = "art_mirror",
                name = "Lustro Absolutu",
                type = "artifact",
                rarity = "LEGENDARY",
                value = 10000,
                weight = 0.5,
                properties = mapOf("description" to "Nie pokazuje Twojego oblicza, lecz Twoją prawdziwą formę w Sferze Fenomenów."),
                effects = mapOf("intelligence" to 10, "sanity" to -5)
            ),
            Item(
                instanceId = "",
                templateId = "art_mask",
                name = "Maska Sereth",
                type = "artifact",
                rarity = "LEGENDARY",
                value = 4500,
                weight = 2.0,
                properties = mapOf("description" to "Zimny metal, który wydaje się oddychać w rytm Twojego serca."),
                effects = mapOf("divine_favor" to 20, "perception" to 3)
            )
        )
    }
}
