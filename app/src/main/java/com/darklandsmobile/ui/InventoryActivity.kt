package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityInventoryBinding
import com.darklandsmobile.systems.InventorySystem

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnUse.setOnClickListener {
            val idx = binding.etItemIndex.text.toString().toIntOrNull()
            if (idx != null) {
                val result = InventorySystem.useItem(idx)
                binding.tvResult.text = result
                render()
            }
        }
    }

    private fun render() {
        val inventory = GameRepository.state.party.inventory
        val sb = StringBuilder()
        sb.appendLine("=== EKWIPUNEK ===")
        sb.appendLine()
        if (inventory.isEmpty()) {
            sb.appendLine("Brak przedmiotow")
        } else {
            inventory.forEachIndexed { i, item ->
                sb.appendLine("${i + 1}. ${item.name} [${item.type}] - ${item.description}")
            }
        }
        binding.tvInventory.text = sb.toString()
    }
}
