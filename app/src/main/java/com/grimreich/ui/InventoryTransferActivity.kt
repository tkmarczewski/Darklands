package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.InventorySystem

/**
 * Ekran transferu ekwipunku miedzy postaciami (Sprint 12).
 * Extras: "fromHero", "toHero", "itemId".
 */
class InventoryTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_transfer)

        val fromHero = intent.getStringExtra("fromHero") ?: "hero_1"
        val toHero   = intent.getStringExtra("toHero")   ?: "hero_2"
        val itemId   = intent.getStringExtra("itemId")   ?: "sword_01"

        findViewById<TextView>(R.id.transferStatus).text =
            InventorySystem.transferItem(fromHero, toHero, itemId)
    }
}
