package com.grimreich.core

/**
 * Definicja symbolu używanego w rytuałach.
 */
enum class SymbolType {
    CROSS,      // Śmierć
    EYE,        // Percepcja
    FRACTURE,   // Pęknięcie
    MOON,       // Nieświadomość
    SNAKE       // Wiedza
}

/**
 * Przepis na rytualny przedmiot (Alchemia Krwi).
 */
data class RitualRecipe(
    val id: String,
    val name: String,
    val targetItemId: String,
    val requiredIngredients: List<String>, // List of itemTemplateIds
    val requiredCipher: List<SymbolType>,
    val sacrificeHp: Int = 10,
    val successMessage: String = "Rytuał zakończony sukcesem."
)

