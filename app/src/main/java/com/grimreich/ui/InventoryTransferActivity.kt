package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository

class InventoryTransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_transfer)

        val g = GameRepository.state
        val party = g.party
        
        findViewById<TextView>(R.id.transferStatus).text = if (party.size < 2) {
            "Potrzebujesz co najmniej dwóch bohaterów do transferu przedmiotów."
        } else {
            "System transferu między ${party.joinToString { it.name }}."
        }

        findViewById<Button>(R.id.btnExitTransfer).setOnClickListener {
            finish()
        }
    }
}
