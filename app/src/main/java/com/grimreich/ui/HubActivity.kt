package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.RealTimeEventManager
import com.grimreich.systems.SaveLoadSystem

class HubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)
        
        // Zdarzenia czasu rzeczywistego przy wejściu do gry
        val eventMessage = RealTimeEventManager.checkRealTimeEvents(this)
        if (eventMessage != null) {
            UiUtils.showNarrativePopup(this, "UPŁYW CZASU", eventMessage)
        }

        setupNavigation()
        setupDevTrigger()
        
        // Zdarzenia losowe w HUBie
        com.grimreich.systems.RandomEventManager.triggerHubEvent(this)

        render()
    }

    private fun setupNavigation() {
        findViewById<Button>(R.id.openCity)?.setOnClickListener { 
            startActivity(Intent(this, CityActivity::class.java)) 
        }
        findViewById<Button>(R.id.openMap)?.setOnClickListener { 
            startActivity(Intent(this, MapActivity::class.java)) 
        }
        findViewById<Button>(R.id.openInventory)?.setOnClickListener { 
            startActivity(Intent(this, InventoryActivity::class.java)) 
        }
        findViewById<Button>(R.id.openQuests)?.setOnClickListener { 
            startActivity(Intent(this, QuestJournalActivity::class.java)) 
        }
        findViewById<Button>(R.id.openCombatStatus)?.setOnClickListener { 
            startActivity(Intent(this, CombatActivity::class.java)) 
        }
        findViewById<Button>(R.id.openReputation)?.setOnClickListener { 
            startActivity(Intent(this, ReputationActivity::class.java)) 
        }
        findViewById<Button>(R.id.openSaints)?.setOnClickListener { 
            startActivity(Intent(this, SaintsActivity::class.java)) 
        }
        findViewById<Button>(R.id.openCityEvents)?.setOnClickListener {
             startActivity(Intent(this, CityEventsActivity::class.java))
        }
        findViewById<Button>(R.id.openTransfer)?.setOnClickListener {
             startActivity(Intent(this, InventoryTransferActivity::class.java))
        }
    }

    private fun setupDevTrigger() {
        findViewById<TextView>(R.id.tvDevMenuTrigger)?.setOnClickListener {
            startActivity(Intent(this, DevMenuActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        render()
        // Automatyczny zapis przy powrocie do HUBa
        SaveLoadSystem.save(this)
    }

    private fun render() {
        val state = GameRepository.state
        val world = state.world
        
        // Czysty nagłówek bez etykiet
        val timeLabel = when(world.timeOfDay.lowercase()) {
            "morning" -> "Poranek"
            "midday" -> "Południe"
            "afternoon" -> "Popołudnie"
            "dusk" -> "Zmierzch"
            "evening" -> "Wieczór"
            else -> world.timeOfDay
        }
        
        // Formatowanie nazwy lokacji (usuwanie technicznych podkreśleń)
        val locationFormatted = world.location.replace("_", " ").capitalize()
        
        findViewById<TextView>(R.id.tvTime)?.text = "$locationFormatted | Dzień ${world.day} | $timeLabel"

        findViewById<Button>(R.id.openCombatStatus)?.visibility = 
            if (state.combat.active) View.VISIBLE else View.GONE
            
        findViewById<Button>(R.id.openFinale)?.visibility = 
            if (world.globalStability < 30) View.VISIBLE else View.GONE

        renderPartyStrip()
    }

    private fun renderPartyStrip() {
        val party = GameRepository.state.party
        val slotIds = listOf(R.id.charSlot0, R.id.charSlot1, R.id.charSlot2, R.id.charSlot3)
        val nameIds = listOf(R.id.tvChar0Name, R.id.tvChar1Name, R.id.tvChar2Name, R.id.tvChar3Name)
        val hpIds = listOf(R.id.pbChar0HP, R.id.pbChar1HP, R.id.pbChar2HP, R.id.pbChar3HP)
        val portIds = listOf(R.id.ivChar0Portrait, R.id.ivChar1Portrait, R.id.ivChar2Portrait, R.id.ivChar3Portrait)

        slotIds.forEachIndexed { i, containerId ->
            val container = findViewById<View>(containerId) ?: return@forEachIndexed
            if (i < party.size) {
                container.visibility = View.VISIBLE
                val hero = party[i]
                findViewById<TextView>(nameIds[i])?.text = hero.name
                findViewById<ProgressBar>(hpIds[i])?.progress = (hero.hp * 100 / hero.maxHp)
                
                // Ustawianie portretu na podstawie klasy/cechy
                val portrait = findViewById<ImageView>(portIds[i])
                val resId = when {
                    hero.portraitRes.isNotEmpty() -> resources.getIdentifier(hero.portraitRes, "drawable", packageName)
                    hero.strength > 15 -> R.drawable.port_knight
                    hero.intelligence > 15 -> R.drawable.port_mage
                    else -> R.drawable.port_alchemist
                }
                if (resId != 0) portrait?.setImageResource(resId)

                container.setOnClickListener {
                    startActivity(Intent(this, CharacterActivity::class.java).putExtra("heroId", hero.id))
                }
            } else {
                container.visibility = View.INVISIBLE
            }
        }
    }
}
