package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository

class HubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)
        render()

        findViewById<Button>(R.id.openCity).setOnClickListener { 
            startActivity(Intent(this, CityActivity::class.java)) 
        }
        findViewById<Button>(R.id.openMap).setOnClickListener { 
            startActivity(Intent(this, MapActivity::class.java)) 
        }
        findViewById<Button>(R.id.openInventory).setOnClickListener { 
            startActivity(Intent(this, InventoryActivity::class.java)) 
        }
        findViewById<Button>(R.id.openTrade).setOnClickListener { 
            startActivity(Intent(this, TradeActivity::class.java)) 
        }
        findViewById<Button>(R.id.openSaints).setOnClickListener { 
            startActivity(Intent(this, SaintsActivity::class.java)) 
        }
        
        // Safety: ensure hidden buttons don't accidentally get clicked if layout overlaps
        findViewById<Button>(R.id.openReputation).setOnClickListener { }
        findViewById<Button>(R.id.openQuests).setOnClickListener { }
        findViewById<Button>(R.id.openCityEvents).setOnClickListener { }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            com.grimreich.systems.SaveLoadSystem.save(this)
            Toast.makeText(this, "Gra zapisana!", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            if (com.grimreich.systems.SaveLoadSystem.load(this)) {
                Toast.makeText(this, "Gra wczytana!", Toast.LENGTH_SHORT).show()
                render()
            } else {
                Toast.makeText(this, "Brak zapisu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val state = GameRepository.state
        val world = state.world
        findViewById<TextView>(R.id.tvTime).text = "Lokacja: ${world.location}\nDzień ${world.day}, ${world.timeOfDay}"

        // Dynamic Visibility for MVP stability
        findViewById<Button>(R.id.openCombatStatus).visibility = 
            if (state.combat.active) View.VISIBLE else View.GONE
            
        findViewById<Button>(R.id.openFinale).visibility = 
            if (world.globalStability < 30) View.VISIBLE else View.GONE

        // Hide non-MVP/unfinished modules to prevent dead ends
        findViewById<Button>(R.id.openReputation).visibility = View.GONE
        findViewById<Button>(R.id.openQuests).visibility = View.GONE
        findViewById<Button>(R.id.openCityEvents).visibility = View.GONE
        findViewById<Button>(R.id.openTransfer).visibility = View.GONE
    }
}
