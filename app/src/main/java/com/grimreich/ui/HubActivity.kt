package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R

/**
 * Główny hub nawigacyjny Grimreich 1.0.
 * Uruchamia ekrany: miasto, reputacja, mapa, święci, eventy, ekwipunek,
 * handel, transfer, status walki, questy i finał.
 */
class HubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        findViewById<Button>(R.id.openCity).setOnClickListener {
            startActivity(Intent(this, CityActivity::class.java)
                .putExtra("cityId", "grimhold"))
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
            startActivity(Intent(this, CityEventsActivity::class.java)
                .putExtra("cityId", "grimhold"))
        }
        findViewById<Button>(R.id.openInventory).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        findViewById<Button>(R.id.openTrade).setOnClickListener {
            startActivity(Intent(this, TradeActivity::class.java)
                .putExtra("cityId", "grimhold")
                .putExtra("basePrice", 100))
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
    }
}
