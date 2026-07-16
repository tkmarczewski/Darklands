package com.grimreich.core

data class Saint(
    val id: String,
    val name: String,
    val domain: String,
    val patronage: String
)

object SaintCatalogue {
    private val saints = mutableListOf(
        Saint("s1", "Prorok Aelion", "Pamięć i Mgła", "Wybrzeże Północne"),
        Saint("s2", "Herold Xyrel", "Krew i Wojna", "Równiny Koronne"),
        Saint("s3", "Sędzia Mira", "Prawda i Odbicia", "Serce Krainy"),
        Saint("s4", "Strażnik Sereth", "Pełnia i Światło", "Południowe Ruiny")
    )

    fun all(): List<Saint> = saints.toList()
    fun get(id: String): Saint? = saints.firstOrNull { it.id == id }
}

