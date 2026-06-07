package com.grimreich.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.databinding.ActivityInventoryBinding
import com.grimreich.systems.InventorySystem

// Ekran ekwipunku - lista przedmiotow z numeracja, podstawowe info i akcja "uzyj".
// Uzytkownik podaje 1-indeksowany numer przedmiotu, aktywujemy przez InventorySystem.useItem(itemId).
class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnUse.setOnClickListener {
            val idx = binding.etItemIndex.text.toString().toIntOrNull()
            val items = GameRepository.state.inventory
            if (idx != null && idx in 1..items.size) {
                val itemId = items[idx - 1].id
                val result = InventorySystem.useItem(itemId)
                binding.tvResult.text = result
                render()
            } else {
                binding.tvResult.text = "Bledny numer przedmiotu"
            }
        }

        binding.btnExitInventory.setOnClickListener {
            finish()
        }
    }

    private fun render() {
        binding.tvInventory.text = buildString {
            append("=== EKWIPUNEK ===\n\n")
            append(InventorySystem.listInventory())
        }
    }
}
