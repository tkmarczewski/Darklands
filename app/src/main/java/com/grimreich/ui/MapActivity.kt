package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.WorldMap
import com.grimreich.systems.TravelSystem

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        renderMap()
    }

    private fun renderMap() {
        val tv = findViewById<TextView>(R.id.tvMap)
        val container = findViewById<LinearLayout>(R.id.destinationsContainer)
        val w = GameRepository.state.world

        tv.text = buildString {
            appendLine("Aktualna lokacja: ${w.location}")
            appendLine("Dzień: ${w.day}, ${w.timeOfDay}")
            appendLine("Pora roku: ${TravelSystem.getSeasonDisplay()}")
            appendLine("Zmęczenie: ${w.fatigue}%")
            appendLine("Stabilność Świata: ${w.globalStability}%")
        }
        
        container.removeAllViews()
        val currentLocationId = w.location.lowercase().replace(" ", "_")
        val neighbors = WorldMap.neighbors(currentLocationId)
        
        neighbors.forEach { conn ->
            val destId = if (conn.fromCityId == currentLocationId) conn.toCityId else conn.fromCityId
            val destName = com.grimreich.world.CityCatalogue.get(destId)?.name ?: destId
            
            val btn = Button(this)
            btn.text = "Podróżuj do: $destName (${conn.terrain.name})"
            btn.setOnClickListener {
                TravelSystem.travelTo(destId, this)
                finish() // Close map after starting travel
            }
            container.addView(btn)
        }
    }
}
