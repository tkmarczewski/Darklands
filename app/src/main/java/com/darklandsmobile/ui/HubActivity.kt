package com.darklandsmobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R

/**
 * Glowny hub nawigacyjny gry (Sprint 9+).
 * Pozwala uruchomic ekrany miasta, modlitwy, reputacji, mapy, swietych, eventow miasta,
 * ekwipunku, handlu, transferu, statusu walki, questow i finalu (Baphomet).
 *
 * UWAGA: To dodatkowy hub testowy. MainActivity pozostaje glownym launcherem aplikacji
 * (dlatego HubActivity nie ma intent-filtera MAIN/LAUNCHER).
 */
class HubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        // Sprint 8: ekran miasta i modlitwy.
        findViewById<Button>(R.id.openCity).setOnClickListener {
            startActivity(Intent(this, CityActivity::class.java).putExtra("cityId", "magdeburg"))
        }
        // Sprint 9: ekran reputacji.
        findViewById<Button>(R.id.openReputation).setOnClickListener {
            startActivity(Intent(this, ReputationActivity::class.java))
        }
        // Sprint 10: mapa, swieci, eventy miasta.
        findViewById<Button>(R.id.openMap).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        findViewById<Button>(R.id.openSaints).setOnClickListener {
            startActivity(Intent(this, SaintsActivity::class.java))
        }
        findViewById<Button>(R.id.openCityEvents).setOnClickListener {
            startActivity(Intent(this, CityEventsActivity::class.java).putExtra("cityId", "magdeburg"))
        }
        // Sprint 11: ekwipunek i handel.
        findViewById<Button>(R.id.openInventory).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        findViewById<Button>(R.id.openTrade).setOnClickListener {
            startActivity(Intent(this, TradeActivity::class.java)
                .putExtra("cityId", "magdeburg")
                .putExtra("basePrice", 100))
        }
        // Sprint 12+: transfer ekwipunku, status walki, podsumowanie questow, final.
        findViewById<Button>(R.id.openTransfer).setOnClickListener {
            startActivity(Intent(this, InventoryTransferActivity::class.java))
        }
        findViewById<Button>(R.id.openCombatStatus).setOnClickListener {
            startActivity(Intent(this, CombatStatusActivity::class.java))
        }
        findViewById<Button>(R.id.openQuests).setOnClickListener {
            startActivity(Intent(this, QuestFinalActivity::class.java))
        }
        findViewById<Button>(R.id.openBaphomet).setOnClickListener {
            startActivity(Intent(this, BaphometActivity::class.java))
        }
    }
}
