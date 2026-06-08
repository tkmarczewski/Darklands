package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
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
        findViewById<Button>(R.id.openQuests).setOnClickListener { 
            startActivity(Intent(this, QuestFinalActivity::class.java)) 
        }
        findViewById<Button>(R.id.openCombatStatus).setOnClickListener { 
            startActivity(Intent(this, CombatActivity::class.java)) 
        }
        findViewById<Button>(R.id.openReputation).setOnClickListener { 
            startActivity(Intent(this, ReputationActivity::class.java)) 
        }
        findViewById<Button>(R.id.openFinale).setOnClickListener { 
            startActivity(Intent(this, FinaleActivity::class.java)) 
        }

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

        // Dynamic Visibility
        findViewById<Button>(R.id.openCombatStatus).visibility = 
            if (state.combat.active) View.VISIBLE else View.GONE
            
        findViewById<Button>(R.id.openFinale).visibility = 
            if (world.globalStability < 30) View.VISIBLE else View.GONE

        renderPartyStrip()
    }

    private fun renderPartyStrip() {
        val party = GameRepository.state.party
        val slots = listOf(
            R.id.charSlot0 to (R.id.tvChar0Name to R.id.pbChar0HP),
            R.id.charSlot1 to (R.id.tvChar1Name to R.id.pbChar1HP),
            R.id.charSlot2 to (R.id.tvChar2Name to R.id.pbChar2HP),
            R.id.charSlot3 to (R.id.tvChar3Name to R.id.pbChar3HP)
        )

        slots.forEachIndexed { i, (containerId, views) ->
            val container = findViewById<View>(containerId)
            if (i < party.size) {
                container.visibility = View.VISIBLE
                val hero = party[i]
                findViewById<TextView>(views.first).text = hero.name
                findViewById<ProgressBar>(views.second).progress = (hero.hp * 100 / hero.maxHp)
                // Add click listener to hero card?
                container.setOnClickListener {
                    startActivity(Intent(this, CharacterActivity::class.java).putExtra("heroId", hero.id))
                }
            } else {
                container.visibility = View.INVISIBLE
            }
        }
    }
}
