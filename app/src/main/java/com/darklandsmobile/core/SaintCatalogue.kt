package com.darklandsmobile.core

/**
 * Prosty katalog świętych używany przez religię i eventy społeczne.
 */
data class Saint(
    val id: String,
    val name: String,
    val domain: String,
    val patronage: String
)

object SaintCatalogue {
    private val saints = mutableListOf(
        Saint("s1", "Święty Jerzy", "courage", "warriors"),
        Saint("s2", "Święta Katarzyna", "wisdom", "scholars"),
        Saint("s3", "Święty Mikołaj", "mercy", "travelers"),
        Saint("s4", "Święty Marcin", "charity", "the poor")
    )

    fun all(): List<Saint> = saints.toList()
    fun get(id: String): Saint? = saints.firstOrNull { it.id == id }
}