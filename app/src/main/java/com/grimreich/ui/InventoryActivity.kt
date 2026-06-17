package com.grimreich.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.ui.inventory.InventoryScreen
import com.grimreich.ui.inventory.InventoryViewModel

class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = InventoryViewModel()
        
        setContent {
            InventoryScreen(viewModel = viewModel, onBack = { finish() })
        }
    }
}
