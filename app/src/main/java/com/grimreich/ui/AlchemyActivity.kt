package com.grimreich.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.databinding.ActivityAlchemyBinding
import com.grimreich.systems.AlchemySystem

class AlchemyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlchemyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlchemyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnBrew.setOnClickListener {
            val idx = binding.etRecipeIndex.text.toString().toIntOrNull()
            val recipes = AlchemySystem.recipes
            if (idx != null && idx in 1..recipes.size) {
                val recipe = recipes[idx - 1]
                val result = AlchemySystem.brew(recipe)
                binding.tvResult.text = result
                render()
            } else {
                binding.tvResult.text = "Nieprawidłowy numer receptury"
            }
        }
        binding.btnBackAlchemy.setOnClickListener { finish() }
    }

    private fun render() {
        val recipes = AlchemySystem.recipes
        val sb = StringBuilder()
        sb.appendLine("=== ALCHEMIA ===")
        sb.appendLine()
        
        val inventory = GameRepository.state.inventory
        val ingredients = inventory.filter { it.type == "ingredient" }
        sb.appendLine("Twoje składniki:")
        if (ingredients.isEmpty()) sb.appendLine("- brak")
        else ingredients.groupBy { it.name }.forEach { (name, list) ->
            sb.appendLine("- $name x${list.size}")
        }
        sb.appendLine()
        
        sb.appendLine("Receptury:")
        recipes.forEachIndexed { i, r ->
            val canBrew = AlchemySystem.canBrew(r)
            val status = if (canBrew) "[MOŻNA WARZYĆ]" else "[BRAK SKŁADNIKÓW]"
            sb.appendLine("${i + 1}. ${r.resultName} $status")
            val ingredientSummary = r.ingredients.entries.joinToString { (id, count) -> 
                val name = com.grimreich.world.ItemCatalogue.findById(id)?.name ?: id
                "$name x$count"
            }
            sb.appendLine("   Wymaga: $ingredientSummary")
            sb.appendLine()
        }
        binding.tvAlchemy.text = sb.toString()
    }
}
