package com.grimreich.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.databinding.ActivityAlchemyBinding

class AlchemyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlchemyBinding

    // Simple alchemy recipes
    private val recipes = listOf(
        Triple("Mikstura leczenia", listOf("Ziele", "Woda"), "Leczy 20 HP"),
        Triple("Trucizna", listOf("Grzyb", "Kwas"), "Zadaje 15 obrazen"),
        Triple("Eliksir sily", listOf("Korzen", "Krew"), "+5 do sily na 3 tury")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlchemyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnBrew.setOnClickListener {
            val idx = binding.etRecipeIndex.text.toString().toIntOrNull()
            if (idx != null && idx in 1..recipes.size) {
                val recipe = recipes[idx - 1]
                binding.tvResult.text = "Stworzono: ${recipe.first}\n${recipe.third}"
            } else {
                binding.tvResult.text = "Nieprawidlowy numer receptury"
            }
        }
    }

    private fun render() {
        val sb = StringBuilder()
        sb.appendLine("=== ALCHEMIA ===")
        sb.appendLine()
        sb.appendLine("Receptury:")
        recipes.forEachIndexed { i, r ->
            sb.appendLine("${i + 1}. ${r.first}")
            sb.appendLine("   Skladniki: ${r.second.joinToString(", ")}")
            sb.appendLine("   Efekt: ${r.third}")
            sb.appendLine()
        }
        binding.tvAlchemy.text = sb.toString()
    }
}
