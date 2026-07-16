package com.grimreich.core

/**
 * Katalog dostępnych rytuałów alchemii krwi.
 */
object RitualCatalog {
    private val recipes = listOf(
        RitualRecipe(
            id = "r_echo_blade",
            name = "Miecz Echa",
            targetItemId = "sword_long", // Zmienione na istniejący szablon dla uproszczenia
            requiredIngredients = listOf("ing_stone", "ing_echo_dust"),
            requiredCipher = listOf(SymbolType.FRACTURE, SymbolType.CROSS),
            sacrificeHp = 15,
            successMessage = "Wykułeś ostrze z pękniętej rzeczywistości."
        ),
        RitualRecipe(
            id = "r_divine_elixir",
            name = "Eliksir Boskości",
            targetItemId = "relic_echo_shard",
            requiredIngredients = listOf("ing_gold_ore", "pot_heal"),
            requiredCipher = listOf(SymbolType.EYE, SymbolType.MOON, SymbolType.SNAKE),
            sacrificeHp = 25,
            successMessage = "Płyn pulsuje życiem, którego nie da się zdefiniować."
        ),
        RitualRecipe(
            id = "r_void_armor",
            name = "Pancerz Pustki",
            targetItemId = "armor_plate_partial",
            requiredIngredients = listOf("ing_bone", "ing_stone", "ing_echo_dust"),
            requiredCipher = listOf(SymbolType.CROSS, SymbolType.FRACTURE, SymbolType.MOON),
            sacrificeHp = 20,
            successMessage = "Zbroja wydaje się cięższa niż sam świat."
        )
    )

    fun getAllRecipes(): List<RitualRecipe> = recipes
    
    fun getById(id: String): RitualRecipe? = recipes.find { it.id == id }
}

