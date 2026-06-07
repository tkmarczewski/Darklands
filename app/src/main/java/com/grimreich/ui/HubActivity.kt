package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
        findViewById<Button>(R.id.openReputation).setOnClickListener { 
            startActivity(Intent(this, ReputationActivity::class.java)) 
        }
        findViewById<Button>(R.id.openMap).setOnClickListener { 
            startActivity(Intent(this, MapActivity::class.java)) 
        }
        findViewById<Button>(R.id.openSaints).setOnClickListener { 
            startActivity(Intent(this, SaintsActivity::class.java)) 
        }
        findViewById<Button>(R.id.openCityEvents).setOnClickListener { 
            startActivity(Intent(this, CityEventsActivity::class.java)) 
        }
        findViewById<Button>(R.id.openInventory).setOnClickListener { 
            startActivity(Intent(this, InventoryActivity::class.java)) 
        }
        findViewById<Button>(R.id.openTrade).setOnClickListener { 
            startActivity(Intent(this, TradeActivity::class.java)) 
        }
        findViewById<Button>(R.id.openTransfer).setOnClickListener { 
            startActivity(Intent(this, InventoryTransferActivity::class.java)) 
        }
        findViewById<Button>(R.id.openCombatStatus).setOnClickListener { 
            startActivity(Intent(this, CombatStatusActivity::class.java)) 
        }
        findViewById<Button>(R.id.openQuests).setOnClickListener { 
            startActivity(Intent(this, QuestFinalActivity::class.java)) 
        }
        findViewById<Button>(R.id.openFinale).setOnClickListener { 
            startActivity(Intent(this, FinaleActivity::class.java)) 
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            com.grimreich.systems.SaveLoadSystem.save(this)
            android.widget.Toast.makeText(this, "Gra zapisana!", android.widget.Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            if (com.grimreich.systems.SaveLoadSystem.load(this)) {
                android.widget.Toast.makeText(this, "Gra wczytana!", android.widget.Toast.LENGTH_SHORT).show()
                render()
            } else {
                android.widget.Toast.makeText(this, "Brak zapisu!", android.widget.Toast.LENGTH_SHORT).show()
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

        // Dynamic Visibility
        findViewById<Button>(R.id.openCombatStatus).visibility = 
            if (state.combat.active) android.view.View.VISIBLE else android.view.View.GONE
            
        findViewById<Button>(R.id.openFinale).visibility = 
            if (world.globalStability < 30) android.view.View.VISIBLE else android.view.View.GONE

        findViewById<Button>(R.id.openTransfer).visibility = 
            if (state.party.size >= 2) android.view.View.VISIBLE else android.view.View.GONE
    }
}
