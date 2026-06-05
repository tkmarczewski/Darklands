package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.InventorySystem

/**
 * Ekran szczegolow itemu (Sprint 12). Extra: "itemId" (default "sword_01").
 */
class InventoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_detail)

        val itemId = intent.getStringExtra("itemId") ?: "sword_01"
        findViewById<TextView>(R.id.inventoryDetailStatus).text =
            InventorySystem.itemDetail(itemId)
    }
}
