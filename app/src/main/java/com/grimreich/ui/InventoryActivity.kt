package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.InventorySystem
import com.grimreich.grimreich.v1.Item

class InventoryActivity : AppCompatActivity() {

    private var selectedHeroId: String? = null
    private lateinit var adapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        selectedHeroId = GameRepository.state.activeHeroId ?: GameRepository.state.party.firstOrNull()?.id
        
        setupHeroSelector()
        setupRecyclerView()
        
        findViewById<Button>(R.id.btnBackFromInv).setOnClickListener {
            finish()
        }
        
        render()
    }

    private fun setupHeroSelector() {
        val container = findViewById<LinearLayout>(R.id.llHeroSelector)
        container.removeAllViews()
        
        GameRepository.state.party.forEach { hero ->
            val btn = Button(this).apply {
                text = hero.name
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                setOnClickListener {
                    selectedHeroId = hero.id
                    render()
                }
            }
            container.addView(btn)
        }
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvInventory)
        adapter = InventoryAdapter(emptyList()) { item ->
            showItemActions(item)
        }
        rv.adapter = adapter
    }

    private fun showItemActions(item: Item) {
        val hero = GameRepository.state.party.find { it.id == selectedHeroId } ?: return
        
        val actions = mutableListOf("Użyj")
        if (item.type == "weapon" || item.type == "armor" || item.slot != null) {
            actions.add("Wyposaż")
        }
        
        val options = actions.toTypedArray()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(item.name)
            .setItems(options) { _, which ->
                when (options[which]) {
                    "Użyj" -> {
                        val res = InventorySystem.useItem(item.id)
                        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                        render()
                    }
                    "Wyposaż" -> {
                        val res = InventorySystem.equip(hero.id, item.id)
                        Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
                        render()
                    }
                }
            }
            .show()
    }

    private fun render() {
        val hero = GameRepository.state.party.find { it.id == selectedHeroId }
        findViewById<TextView>(R.id.tvActiveHeroInfo).text = "Bohater: ${hero?.name ?: "Brak"}"
        
        adapter.updateItems(GameRepository.state.inventory)
    }
}
